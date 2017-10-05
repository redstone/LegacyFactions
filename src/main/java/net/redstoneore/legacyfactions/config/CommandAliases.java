package net.redstoneore.legacyfactions.config;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.entity.persist.Persist;

import java.util.List;


public class CommandAliases {
	
	// -------------------------------------------------- //
	// COMMAND ALIASES
	// -------------------------------------------------- //

	// Base command for Factions 'f' is default, you can add more or changed it.
	public static List<String> baseCommandAliases = Lists.newArrayList("f");
	
	// Customise any alias for all commands
	public static List<String> cmdAliasesAdmin = Lists.newArrayList("admin", "setadmin", "leader", "setleader");
	public static List<String> cmdAliasesAnnounce = Lists.newArrayList("announce", "ann");
	public static List<String> cmdAliasesAutoclaim = Lists.newArrayList("autoclaim");
	public static List<String> cmdAliasesAutohelp = Lists.newArrayList("?", "h", "help");
	public static List<String> cmdAliasesAutokick = Lists.newArrayList("autokick");
	public static List<String> cmdAliasesBoom = Lists.newArrayList("noboom", "explosions", "toggleexplosions");
	public static List<String> cmdAliasesBypass = Lists.newArrayList("bypass");
	public static List<String> cmdAliasesChat = Lists.newArrayList("chat", "c");
	public static List<String> cmdAliasesChatspy = Lists.newArrayList("chatspy");
	public static List<String> cmdAliasesClaim = Lists.newArrayList("claim");
	public static List<String> cmdAliasesClaimLine = Lists.newArrayList("claimline", "cl");
	public static List<String> cmdAliasesColeader = Lists.newArrayList("coleader", "setcoleader");
	public static List<String> cmdAliasesConfig = Lists.newArrayList("config");
	public static List<String> cmdAliasesConvert = Lists.newArrayList("convert");
	public static List<String> cmdAliasesDebug = Lists.newArrayList("debug");
	public static List<String> cmdAliasesCreate = Lists.newArrayList("create");
	public static List<String> cmdAliasesDeinvite = Lists.newArrayList("deinvite", "deinv");
	public static List<String> cmdAliasesDelwarp = Lists.newArrayList("delwarp", "deletewarp", "dw");
	public static List<String> cmdAliasesDescription = Lists.newArrayList("desc", "description");
	public static List<String> cmdAliasesDisband = Lists.newArrayList("disband");
	public static List<String> cmdAliasesEmblem = Lists.newArrayList("emblem");
	public static List<String> cmdAliasesFlag = Lists.newArrayList("flag", "f");
	public static List<String> cmdAliasesFlagSet = Lists.newArrayList("set", "s");
	public static List<String> cmdAliasesFlagList = Lists.newArrayList("list", "l");
	public static List<String> cmdAliasesHelp = Lists.newArrayList("help", "h", "?");
	public static List<String> cmdAliasesHome = Lists.newArrayList("home");
	public static List<String> cmdAliasesInvite = Lists.newArrayList("invite", "inv");
	public static List<String> cmdAliasesJoin = Lists.newArrayList("join");
	public static List<String> cmdAliasesKick = Lists.newArrayList("kick");
	public static List<String> cmdAliasesBan = Lists.newArrayList("ban");
	public static List<String> cmdAliasesLang = Lists.newArrayList("lang");
	public static List<String> cmdAliasesLeave = Lists.newArrayList("leave");
	public static List<String> cmdAliasesList = Lists.newArrayList("list", "ls");
	public static List<String> cmdAliasesLock = Lists.newArrayList("lock");
	public static List<String> cmdAliasesLogins = Lists.newArrayList("login", "logins", "logout", "logouts");
	public static List<String> cmdAliasesMap = Lists.newArrayList("map");
	public static List<String> cmdAliasesMod = Lists.newArrayList("mod", "setmod", "officer", "setofficer");
	public static List<String> cmdAliasesModifyPower = Lists.newArrayList("modifypower", "modpower", "mp", "pm");
	public static List<String> cmdAliasesMoney = Lists.newArrayList("money");
	public static List<String> cmdAliasesMoneyBalance = Lists.newArrayList("balance", "bal", "b");
	public static List<String> cmdAliasesMoneyDeposit = Lists.newArrayList("deposit", "d");
	public static List<String> cmdAliasesMoneyTransferFf = Lists.newArrayList("ff");
	public static List<String> cmdAliasesMoneyTransferFp = Lists.newArrayList("fp");
	public static List<String> cmdAliasesMoneyTransferPf = Lists.newArrayList("pf");
	public static List<String> cmdAliasesMoneyWithdraw = Lists.newArrayList("withdraw", "w");
	public static List<String> cmdAliasesOpen = Lists.newArrayList("open");
	public static List<String> cmdAliasesOwner = Lists.newArrayList("owner");
	public static List<String> cmdAliasesOwnerList = Lists.newArrayList("ownerlist");
	public static List<String> cmdAliasesPeaceful = Lists.newArrayList("peaceful");
	public static List<String> cmdAliasesPermanent = Lists.newArrayList("permanent");
	public static List<String> cmdAliasesPermanentPower = Lists.newArrayList("permanentpower");
	public static List<String> cmdAliasesPower = Lists.newArrayList("power", "pow");
	public static List<String> cmdAliasesPowerBoost = Lists.newArrayList("powerboost");
	public static List<String> cmdAliasesRelationAlly = Lists.newArrayList("ally");
	public static List<String> cmdAliasesRelationTruce = Lists.newArrayList("truce");
	public static List<String> cmdAliasesRelationEnemy = Lists.newArrayList("enemy");
	public static List<String> cmdAliasesRelationNeutral = Lists.newArrayList("neutral");
	public static List<String> cmdAliasesReload = Lists.newArrayList("reload");
	public static List<String> cmdAliasesSafeunclaimall = Lists.newArrayList("safeunclaimall");
	public static List<String> cmdAliasesSaveAll = Lists.newArrayList("saveall", "save");
	public static List<String> cmdAliasesScoreboard = Lists.newArrayList("scoreboard", "sb");
	public static List<String> cmdAliasesSeeChunk = Lists.newArrayList("seechunk", "sc");
	public static List<String> cmdAliasesSethome = Lists.newArrayList("sethome");
	public static List<String> cmdAliasesSetwarp = Lists.newArrayList("setwarp");
	public static List<String> cmdAliasesShow = Lists.newArrayList("show", "who");
	public static List<String> cmdAliasesShowInvites = Lists.newArrayList("showinvites");
	public static List<String> cmdAliasesStatus = Lists.newArrayList("status", "s");
	public static List<String> cmdAliasesStuck = Lists.newArrayList("stuck");
	public static List<String> cmdAliasesTag = Lists.newArrayList("tag", "rename");
	public static List<String> cmdAliasesTitle = Lists.newArrayList("title");
	public static List<String> cmdAliasesToggleAllianceChat = Lists.newArrayList("ac", "togglealiancechat", "tac");
	public static List<String> cmdAliasesTop = Lists.newArrayList("top");
	public static List<String> cmdAliasesUnban = Lists.newArrayList("unban");
	public static List<String> cmdAliasesUnclaim = Lists.newArrayList("unclaim");
	public static List<String> cmdAliasesUnclaimAll = Lists.newArrayList("unclaimall");
	public static List<String> cmdAliasesVersion = Lists.newArrayList("version");
	public static List<String> cmdAliasesWarp = Lists.newArrayList("warp", "warps");
	public static List<String> cmdAliasesWarunclaimall = Lists.newArrayList("warunclaimall");
	public static List<String> cmdAliasesStyle = Lists.newArrayList("style");	
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	private static transient CommandAliases i = new CommandAliases();

	public static void load() {
		Persist.get().loadOrSaveDefault(i, CommandAliases.class, "commandAliases");
	}

	public static void save() {
		Persist.get().save(i);
	}
}

