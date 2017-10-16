package net.redstoneore.legacyfactions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.util.PermUtil;

/**
 * Permissions used by LegacyFactions
 */
public enum Permission {
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	MANAGE_SAFE_ZONE("managesafezone"),
	MANAGE_WAR_ZONE("managewarzone"),
	OWNERSHIP_BYPASS("ownershipbypass"),
	ADMIN("admin"),
	ADMIN_ANY("admin.any"),
	AHOME("ahome"),
	ANNOUNCE("announce"),
	AUTOCLAIM("autoclaim"),
	AUTOKICK("autokick"),
	AUTOKICK_OTHER("autokick.others"),
	AUTO_LEAVE_BYPASS("autoleavebypass"),
	BYPASS("bypass"),
	BAN("ban"),
	CHAT("chat"),
	CHATSPY("chatspy"),
	CLAIM("claim"),
	CLAIM_LINE("claim.line"),
	CLAIM_RADIUS("claim.radius"),
	COLEADER("coleader"),
	COLEADER_ANY("coleader.any"),
	CONFIG("config"),
	DEBUG("debug"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	EMBLEM("emblem"),
	EMBLEM_OTHERS("emblem.others"),
	FLAG("flag"),
	FLAG_LIST("flag.list"),
	FLAG_LIST_ANY("flag.list.any"),
	FLAG_SET("flag.set"),
	FLAG_SET_ANY("flag.set.any"),
	HELP("help"),
	HOME("home"),
	HOME_ANY("home.any"),
	INVITE("invite"),
	JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"),
	KICK("kick"),
	KICK_ANY("kick.any"),
	LANG("lang"),
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MAP("map"),
	MOD("mod"),
	MOD_ANY("mod.any"),
	MODIFY_POWER("modifypower"),
	MONEY_BALANCE("money.balance"),
	MONEY_BALANCE_ANY("money.balance.any"),
	MONEY_DEPOSIT("money.deposit"),
	MONEY_WITHDRAW("money.withdraw"),
	MONEY_WITHDRAW_ANY("money.withdraw.any"),
	MONEY_F2F("money.f2f"),
	MONEY_F2P("money.f2p"),
	MONEY_P2F("money.p2f"),
	MONITOR_LOGINS("monitorlogins"),
	OWNER("owner"),
	OWNERLIST("ownerlist"),
	SET_OPEN("set.open", Lists.newArrayList("open")),
	SET_PEACEFUL("set.peaceful", Lists.newArrayList("setpeaceful")),
	SET_PERMANENT("set.permanent", Lists.newArrayList("setpermanent")),
	SET_EXPLOSIONS("set.explosions", Lists.newArrayList("noboom")),
	SET_PERMANENTPOWER("setpermanentpower"),
	SHOW_INVITES("showinvites"),
	SHOW_BYPASSEXEMPT("show.bypassexempt"),
	POWERBOOST("powerboost"),
	POWER("power"),
	POWER_ANY("power.any"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SETHOME("sethome"),
	SETHOME_ANY("sethome.any"),
	SHOW("show"),
	STATUS("status"),
	STUCK("stuck"),
	TAG("tag"),
	TITLE("title"),
	TOGGLE_ALLIANCE_CHAT("togglealliancechat"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	SCOREBOARD("scoreboard"),
	SEECHUNK("seechunk"),
	WARP("warp"),
	SETWARP("warp.set"),
	WARPPASSWORD("warp.passwords"),
	TOP("top"),
	VAULT("vault"),
	SETMAXVAULTS("setmaxvaults"),
	STYLE("style"),
	STYLE_ANY("style.any"),
	
	// -------------------------------------------------- //
	// DEPRECATED PERMISSIONS
	// -------------------------------------------------- //
	
	@Deprecated
	NO_BOOM("noboom"),
	
	@Deprecated
	OPEN("open"),

	;
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private Permission(final String node) {
		this(node, new ArrayList<>());
	}
	private Permission(final String node, List<String> oldNames) {
		this.node = "factions." + node;
		this.oldNames = oldNames;
	}
	
	// -------------------------------------------------- //
 	// FIELDS
 	// -------------------------------------------------- //

	private final String node;
	private final List<String> oldNames;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getNode() {
		return this.node;
	}
	
	public boolean has(CommandSender sender, boolean informSenderIfNot) {
		if (this.oldNames.size() == 0) {
			return PermUtil.get().has(sender, this.node, informSenderIfNot);
		}

		if (!PermUtil.get().has(sender, this.node, false)) {
			int at = 1;
			
			for (String name : this.oldNames) {
				String alternativePermission = "factions." + name;
				
				if (at == this.oldNames.size()) {
					return PermUtil.get().has(sender, alternativePermission, informSenderIfNot);
				}
				
				if (PermUtil.get().has(sender, alternativePermission, false)) {
					return true;
				}
				at++;
			}
		}
		
		return false;
	}

	public boolean has(CommandSender sender) {
		return this.has(sender, false);
	}
	
	public boolean has(FPlayer fplayer) {
		return this.has(fplayer.getPlayer(), false);
	}
	
}
