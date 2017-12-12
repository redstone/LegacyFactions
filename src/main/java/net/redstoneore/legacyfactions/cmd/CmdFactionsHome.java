package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsEngine;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsIntegration;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.locality.Locality;
import net.redstoneore.legacyfactions.util.SmokeUtil;
import net.redstoneore.legacyfactions.util.WarmUpUtil;

import java.util.ArrayList;
import java.util.List;


public class CmdFactionsHome extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsHome instance = new CmdFactionsHome();
	public static CmdFactionsHome get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsHome() {
		this.aliases.addAll(CommandAliases.cmdAliasesHome);

		this.optionalArgs.put("who", "you");
		this.optionalArgs.put("type", "faction/player");

		this.permission = Permission.HOME.getNode();
		this.disableOnLock = false;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		if (!Config.homesEnabled) {
			fme.sendMessage(Lang.COMMAND_HOME_DISABLED);
			return;
		}
		
		Faction faction = fme.getFaction();
		String other = this.argAsString(0, null);
		if (other != null && (Permission.HOME_ANY.has(me) || fme.isAdminBypassing())) {
			String type = this.argAsString(1, null);
			if (type == null) {
				Lang.COMMAND_HOME_FACTIONORPLAYER.getBuilder().parse().sendTo(fme);
				fme.sendMessage(this.getUseageTemplate());
				return;
			}
			type = type.toLowerCase();
			
			if (type.startsWith("p")) {
				faction = FPlayerColl.get(other).getFaction();
			} else if (type.startsWith("f")) {
				faction = FactionColl.get(other);
			} else {
				Lang.COMMAND_HOME_FACTIONORPLAYER.getBuilder().parse().sendTo(fme);
				fme.sendMessage(this.getUseageTemplate());
				return;
			}
		}
		
		if (!Config.homesTeleportCommandEnabled) {
			Lang.COMMAND_HOME_TELEPORTDISABLED.getBuilder().parse().sendTo(fme);
			return;
		}

		if (!faction.hasHome()) {
			fme.sendMessage(Lang.COMMAND_HOME_NOHOME.getBuilder().parse().toString() + (fme.getRole().getValue() < Role.MODERATOR.getValue() ? Lang.GENERIC_ASKYOURLEADER.getBuilder().parse().toString() : Lang.GENERIC_YOUSHOULD.getBuilder().parse().toString()));
			fme.sendMessage(CmdFactionsSethome.get().getUseageTemplate());
			return;
		}

		if (!Config.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory() && !fme.isAdminBypassing()) {
			Lang.COMMAND_HOME_INENEMY.getBuilder().parse().sendTo(fme);
			return;
		}

		if (!Config.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != faction.getHome().getWorld().getUID() && !fme.isAdminBypassing()) {
			Lang.COMMAND_HOME_WRONGWORLD.getBuilder().parse().sendTo(fme);
			return;
		}

		Faction factionAt = Board.get().getFactionAt(Locality.of(me.getLocation()));
		final Location loc = me.getLocation().clone();

		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if (!fme.isAdminBypassing() && Config.homesTeleportAllowedEnemyDistance > 0 && !factionAt.isSafeZone() && (!fme.isInOwnTerritory() || (fme.isInOwnTerritory() && !Config.homesTeleportIgnoreEnemiesIfInOwnTerritory))) {
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player onlinePlayer : me.getServer().getOnlinePlayers()) {
				if (onlinePlayer == null || !onlinePlayer.isOnline() || onlinePlayer.isDead() || onlinePlayer == me || onlinePlayer.getWorld() != w) {
					continue;
				}

				FPlayer target = FPlayerColl.get(onlinePlayer);
				if (this.fme.getRelationTo(target) != Relation.ENEMY || target.isVanished(this.fme)) {
					continue;
				}

				Location l = onlinePlayer.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Config.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max) {
					continue;
				}

				Lang.COMMAND_HOME_NEARENEMY.getBuilder().parse().replace("<blocks>", String.valueOf(Config.homesTeleportAllowedEnemyDistance)).sendTo(fme);
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Config.econCostHome, Lang.COMMAND_HOME_TOTELEPORT.toString(), Lang.COMMAND_HOME_FORTELEPORT.toString())) {
			return;
		}

		// if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
		if (EssentialsIntegration.get().isEnabled()) {
			if (EssentialsEngine.handleTeleport(me, faction.getHome())) {
				return;
			}
		}

		// calculate warmup
		long warmup = Config.warmupHome;
		
		if (fme.isAdminBypassing()) {
			warmup = 0;
		}
		
		final Location warmupLocation = faction.getHome();
		final Player warmupPlayer = me;
		
		this.doWarmUp(WarmUpUtil.Warmup.HOME, Lang.WARMUPS_NOTIFY_TELEPORT, "Home", new Runnable() {
			@Override
			public void run() {
				// Create a smoke effect
				if (Config.homesTeleportCommandSmokeEffectEnabled) {
					List<Location> smokeLocations = new ArrayList<Location>();
					smokeLocations.add(loc);
					smokeLocations.add(loc.add(0, 1, 0));
					smokeLocations.add(CmdFactionsHome.this.myFaction.getHome());
					smokeLocations.add(CmdFactionsHome.this.myFaction.getHome().clone().add(0, 1, 0));
					SmokeUtil.spawnCloudRandom(smokeLocations, Config.homesTeleportCommandSmokeEffectThickness);
				}

				warmupPlayer.teleport(warmupLocation);
			}
		}, warmup);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_HOME_DESCRIPTION.toString();
	}

}
