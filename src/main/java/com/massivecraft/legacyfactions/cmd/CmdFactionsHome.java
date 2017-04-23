package com.massivecraft.legacyfactions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.legacyfactions.*;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.FPlayer;
import com.massivecraft.legacyfactions.entity.FPlayerColl;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.entity.FactionColl;
import com.massivecraft.legacyfactions.integration.essentials.EssentialsEngine;
import com.massivecraft.legacyfactions.integration.essentials.EssentialsIntegration;
import com.massivecraft.legacyfactions.util.SmokeUtil;
import com.massivecraft.legacyfactions.util.WarmUpUtil;

import java.util.ArrayList;
import java.util.List;


public class CmdFactionsHome extends FCommand {

	public CmdFactionsHome() {
		super();
		this.aliases.add("home");

		this.optionalArgs.put("who", "you");
		this.optionalArgs.put("type", "faction/player");

		this.permission = Permission.HOME.node;
		this.disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		if (!Conf.homesEnabled) {
			fme.msg(TL.COMMAND_HOME_DISABLED);
			return;
		}
		
		Faction faction = fme.getFaction();
		String other = this.argAsString(0, null);
		if (other != null && (Permission.HOME_ANY.has(me) || fme.isAdminBypassing())) {
			String type = this.argAsString(1, null);
			if (type == null) {
				fme.msg("<b>Please specify if it is a faction or player");
				fme.msg(this.getUseageTemplate());
				return;
			}
			type = type.toLowerCase();
			
			if (type.startsWith("p")) {
				faction = FPlayerColl.get(other).getFaction();
			} else if (type.startsWith("f")) {
				faction = FactionColl.get(other);
			} else {
				fme.msg("<b>Please specify if it is a faction or player");
				fme.msg(this.getUseageTemplate());
				return;
			}
		}
		
		if (!Conf.homesTeleportCommandEnabled) {
			fme.msg(TL.COMMAND_HOME_TELEPORTDISABLED);
			return;
		}

		if (!faction.hasHome()) {
			fme.msg(TL.COMMAND_HOME_NOHOME.toString() + (fme.getRole().value < Role.MODERATOR.value ? TL.GENERIC_ASKYOURLEADER.toString() : TL.GENERIC_YOUSHOULD.toString()));
			fme.sendMessage(Factions.get().cmdBase.cmdSethome.getUseageTemplate());
			return;
		}

		if (!Conf.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory() && !fme.isAdminBypassing()) {
			fme.msg(TL.COMMAND_HOME_INENEMY);
			return;
		}

		if (!Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != faction.getHome().getWorld().getUID() && !fme.isAdminBypassing()) {
			fme.msg(TL.COMMAND_HOME_WRONGWORLD);
			return;
		}

		Faction factionAt = Board.getInstance().getFactionAt(new FLocation(me.getLocation()));
		final Location loc = me.getLocation().clone();

		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if (!fme.isAdminBypassing() &&
				Conf.homesTeleportAllowedEnemyDistance > 0 &&
					!factionAt.isSafeZone() &&
						(!fme.isInOwnTerritory() || (fme.isInOwnTerritory() && !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory))) {
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : me.getServer().getOnlinePlayers()) {
				if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w) {
					continue;
				}

				FPlayer fp = FPlayerColl.get(p);
				if (fme.getRelationTo(fp) != Relation.ENEMY || fp.isVanished()) {
					continue;
				}

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Conf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max) {
					continue;
				}

				fme.msg(TL.COMMAND_HOME_ENEMYNEAR, String.valueOf(Conf.homesTeleportAllowedEnemyDistance));
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostHome, TL.COMMAND_HOME_TOTELEPORT.toString(), TL.COMMAND_HOME_FORTELEPORT.toString())) {
			return;
		}

		// if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
		if (EssentialsIntegration.get().isEnabled()) {
			if (EssentialsEngine.handleTeleport(me, faction.getHome())) {
				return;
			}
		}

		// calculate warmup
		long warmup = Factions.get().getConfig().getLong("warmups.f-home", 0);
		
		if (fme.isAdminBypassing()) {
			warmup = 0;
		}
		
		final Location warmupLocation = faction.getHome();
		final Player warmupPlayer = me;
		
		this.doWarmUp(WarmUpUtil.Warmup.HOME, TL.WARMUPS_NOTIFY_TELEPORT, "Home", new Runnable() {
			@Override
			public void run() {
				// Create a smoke effect
				if (Conf.homesTeleportCommandSmokeEffectEnabled) {
					List<Location> smokeLocations = new ArrayList<Location>();
					smokeLocations.add(loc);
					smokeLocations.add(loc.add(0, 1, 0));
					smokeLocations.add(CmdFactionsHome.this.myFaction.getHome());
					smokeLocations.add(CmdFactionsHome.this.myFaction.getHome().clone().add(0, 1, 0));
					SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
				}

				warmupPlayer.teleport(warmupLocation);
			}
		}, warmup);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_HOME_DESCRIPTION;
	}

}
