package net.redstoneore.legacyfactions.cmd;

import mkremins.fanciful.FancyMessage;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.event.EventFactionsWarpUse;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.WarmUpUtil;
import net.redstoneore.legacyfactions.warp.FactionWarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class CmdFactionsWarp extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsWarp instance = new CmdFactionsWarp();
	public static CmdFactionsWarp get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsWarp() {
		this.aliases.addAll(CommandAliases.cmdAliasesWarp);
		
		this.optionalArgs.put("warpname", "warpname");
		this.optionalArgs.put("password", "password");
		
		this.permission = Permission.WARP.getNode();
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
		if (args.size() == 0) {
			FancyMessage msg = new FancyMessage(Lang.COMMAND_FWARP_WARPS.toString()).color(ChatColor.GOLD);
			Collection<FactionWarp> warps = myFaction.warps().getAll();
			for (FactionWarp warp : warps) {
				msg.then(warp.getName() + " ").tooltip(Lang.COMMAND_FWARP_CLICKTOWARP.toString()).command("/" + CommandAliases.baseCommandAliases.get(0) + " warp " + warp.getName()).color(ChatColor.WHITE);
			}
			sendFancyMessage(msg);
			return;
			
		}
		if (args.size() > 2) {
			fme.sendMessage(Lang.COMMAND_FWARP_COMMANDFORMAT);
			return;
		}
		
		final String warpName = argAsString(0);
		
		String warpPassword = argAsString(1, null);
		if (warpPassword != null) {
			warpPassword = warpPassword.toLowerCase();
		}
		
		if (myFaction.warps().get(warpName).isPresent()) {
			FactionWarp warp = myFaction.warps().get(warpName).get();
			
			if (warp.hasPassword() && (warpPassword == null || !warp.isPassword(warpPassword))) {
				fme.sendMessage(Lang.COMMAND_FWARP_INVALID_PASSWORD);
				return;
			}
			
			EventFactionsWarpUse event = new EventFactionsWarpUse(fme, warp);
			event.call();
			if (event.isCancelled()) return;
			
			if (!transact(fme)) {
				return;
			}
			
			final FPlayer fPlayer = fme;
			final UUID uuid = fme.getPlayer().getUniqueId();
			this.doWarmUp(WarmUpUtil.Warmup.WARP, Lang.WARMUPS_NOTIFY_TELEPORT, warpName, new Runnable() {
				@Override
				public void run() {
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						player.teleport(fPlayer.getFaction().warps().get(warpName).get().getLocation());
						fPlayer.sendMessage(Lang.COMMAND_FWARP_WARPED, warpName);
					}
				}
			}, Config.warmupWarp);
		} else {
			fme.sendMessage(Lang.COMMAND_FWARP_INVALID, warpName);
		}
	}

	private boolean transact(FPlayer player) {
		return Config.warpCost.get("use") == 0 || player.isAdminBypassing() || payForCommand(Config.warpCost.get("use"), Lang.COMMAND_FWARP_TOWARP.toString(), Lang.COMMAND_FWARP_FORWARPING.toString());
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_FWARP_DESCRIPTION.toString();
	}
}
