package net.redstoneore.legacyfactions.config;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.expansion.chat.FactionsChatConfig;
import net.redstoneore.legacyfactions.expansion.fly.FactionsFlyConfig;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapConfig;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.struct.LandValue;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.cross.CrossColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;
import net.redstoneore.legacyfactions.util.cross.CrossColour.DefaultChatColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType.DefaultEntityType;

/**
 * This class represents the current server configuration. It is saved on load into config.js, so
 * the server operator will always find the latest configuration available.<br>
 * <br>
 * TODO: There is more to document in this class.
 */
public class Config {
	
	/**
	 * Version of this config, used for migrations.
	 */
	public static transient double version = 2.0;
	
	// -------------------------------------------------- //
	// MISC
	// -------------------------------------------------- //
	
	protected static String _sectionMisc = Lang.CONFIG_MISC_SECTION.name();
	
	protected static String _debug = Lang.CONFIG_MISC_DEBUG.name();
	public static boolean debug = false;
	
	protected static String _logStatistics = Lang.CONFIG_MISC_STATISTICS.name();
	public static boolean logStatistics = true;
	
	protected static String _disableFactionsInWorlds = Lang.CONFIG_MISC_DISABLEINWORLDS.name();
	public static Set<String> disableFactionsInWorlds = new HashSet<>();
		
	protected static String _hideConfigComments = Lang.CONFIG_MISC_HIDECONFIGCOMMENTS.name();
	public static boolean hideConfigComments = false;
	
	// -------------------------------------------------- //
	// Non-1.6 Features
	// -------------------------------------------------- //
	
	protected static String _sectionNon16 = Lang.CONFIG_NON16_SECTION.name();

	protected static String _enableTruces = Lang.CONFIG_NON16_TRUCES.name();
	public static boolean enableTruces = true;
	
	protected static String _enableColeaders = Lang.CONFIG_NON16_COLEADERS.name();
	public static boolean enableColeaders = true;
	
	protected static String _enableFlags = Lang.CONFIG_NON16_FLAGS.name();
	public static boolean enableFlags = false;
		
	// -------------------------------------------------- //
	// COMMANDS
	// -------------------------------------------------- //
	
	protected static String _sectionCommands = Lang.CONFIG_COMMANDS_SECTION.name();
	
	protected static String _allowNoSlashCommand = Lang.CONFIG_COMMANDS_ALLOWNOSLASH.name();
	public static boolean allowNoSlashCommand = true;

	// -------------------------------------------------- //
	// WARMUPS
	// -------------------------------------------------- //
	
	protected static String _sectionWarmups = Lang.CONFIG_WARMUPS_SECTION.name();

	protected static String _warmupWarp = Lang.CONFIG_WARMUPS_WARP.name();
	public static long warmupWarp = 0;
	
	protected static String _warmupHome = Lang.CONFIG_WARMUPS_HOME.name();
	public static long warmupHome = 0;
	
	// -------------------------------------------------- //
	// RELATIONS
	// -------------------------------------------------- //
	
	protected static String _sectionRelations = Lang.CONFIG_RELATIONS_SECTION.name();

	protected static String _maxRelations = Lang.CONFIG_RELATIONS_MAX.name();
	public static Map<Relation, Integer> maxRelations = MiscUtil.map(
		Relation.ALLY, -1,
		Relation.TRUCE, -1,
		Relation.NEUTRAL, -1,
		Relation.ENEMY, -1
	);
	
	protected static String _enforcedRelations = Lang.CONFIG_RELATIONS_ENFORCED.name();
	public static Map<Relation, Boolean> enforcedRelations = MiscUtil.newMap(
		Relation.ENEMY, false
	);
	
	protected static String _colorMember = Lang.CONFIG_RELATIONS_COLOURS.name();
	public static CrossColour colorMember = CrossColour.of(DefaultChatColour.GREEN);
	
	public static CrossColour colorAlly = CrossColour.of(DefaultChatColour.LIGHT_PURPLE);
	
	public static CrossColour colorTruce = CrossColour.of(DefaultChatColour.DARK_PURPLE);
	
	public static CrossColour colorNeutral = CrossColour.of(DefaultChatColour.WHITE);
	
	public static CrossColour colorEnemy = CrossColour.of(DefaultChatColour.RED);

	public static CrossColour colorPeaceful = CrossColour.of(DefaultChatColour.GOLD);
	
	// -------------------------------------------------- //
 	// TOOLTIPS
	// -------------------------------------------------- //
	
	protected static String _sectionTooltips = Lang.CONFIG_TOOLTIPS_SECTION.name();

	protected static String _tooltips = Lang.CONFIG_TOOLTIPS_TOOLTIPS.name();
	public static Map<String, List<String>> tooltips = MiscUtil.map(
		"list", Lists.newArrayList(
			"&6Leader: &f{leader}",
			"&6Claimed: &f{chunks}",
			"&6Raidable: &f{raidable}",
			"&6Warps: &f{warps}",
			"&6Power: &f{power}/{maxPower}",
			"&6Members: &f{online}/{members}"
		),
		"show", Lists.newArrayList(
			"&6Last Seen: &f{player-lastSeen}",
			"&6Power: &f{player-power}",
			"&6Rank: &f{player-group}",
			"&6Balance: &a${player-balance}"
		)
	);
	
	// -------------------------------------------------- //
	// EMBLEMS
	// -------------------------------------------------- //
	
	protected static String _sectionEmblems = Lang.CONFIG_EMBLEMS_SECTION.name();

	protected static String _emblemsEnabled = Lang.CONFIG_EMBLEMS_ENABLED.name();
	public static boolean emblemsEnabled = false;

	protected static String _emblemsMinLength = Lang.CONFIG_EMBLEMS_MINLENGTH.name();
	public static int emblemsMinLength = 2;
	
	protected static String _emblemsMaxLength = Lang.CONFIG_EMBLEMS_MAXLENGTH.name();
	public static int emblemsMaxLength = 4;

	protected static String _emblemsMaxLengthMinRole = Lang.CONFIG_EMBLEMS_MINROLE.name();
	public static Role emblemsMinRole = Role.COLEADER;

	// -------------------------------------------------- //
	// POWER
	// -------------------------------------------------- //
	
	protected static String _sectionPower = Lang.CONFIG_POWER_SECTION.name();

	protected static String _powerPlayerMax = Lang.CONFIG_POWER_MAX.name();
	public static double powerPlayerMax = 10.0;
	
	protected static String _powerPlayerMin = Lang.CONFIG_POWER_MIN.name();
	public static double powerPlayerMin = -10.0;
	
	protected static String _powerPlayerStarting = Lang.CONFIG_POWER_STARTING.name();
	public static double powerPlayerStarting = 0.0;
	
	protected static String _powerPerMinute = Lang.CONFIG_POWER_PERMINUTE.name();
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	
	protected static String _powerPerDeath = Lang.CONFIG_POWER_PERDEATH.name();
	public static double powerPerDeath = 4.0; 
	
	protected static String _powerRegenOffline = Lang.CONFIG_POWER_REGENOFFLINE.name();
	public static boolean powerRegenOffline = false; 
	
	protected static String _powerOfflineLossPerDay = Lang.CONFIG_POWER_OFFLINELOSS.name();
	public static double powerOfflineLossPerDay = 0.0;
	
	protected static String _powerOfflineLossLimit = Lang.CONFIG_POWER_OFFLINELOSSLIMIT.name();
	public static double powerOfflineLossLimit = 0.0;
	
	protected static String _powerFactionMax = Lang.CONFIG_POWER_FACTIONMAX.name();
	public static double powerFactionMax = 0.0; 
	
	// -------------------------------------------------- //
	// PLAYER PREFIX
	// -------------------------------------------------- //
	
	protected static String _sectionPlayerPrefix = Lang.CONFIG_PLAYERPREFIX_SECTION.name();

	protected static String _playerPrefixAdmin = Lang.CONFIG_PLAYERPREFIX_ADMIN.name();
	public static String playerPrefixAdmin = "***";
	
	protected static String _playerPrefixColeader = Lang.CONFIG_PLAYERPREFIX_COLEADER.name();
	public static String playerPrefixColeader = "**";
	
	protected static String _playerPrefixMod = Lang.CONFIG_PLAYERPREFIX_MOD.name();
	public static String playerPrefixMod = "*";

	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //
	
	protected static String _sectionFaction = Lang.CONFIG_FACTION_SECTION.name();

	protected static String _factionDefaultRelation = Lang.CONFIG_FACTION_DEFAULTRELATION.name();
	public static String factionDefaultRelation = "neutral";
	
	protected static String _factionTagLengthMin = Lang.CONFIG_FACTION_TAGMIN.name();
	public static int factionTagLengthMin = 3;
	
	protected static String _factionTagLengthMax = Lang.CONFIG_FACTION_TAGMAX.name();
	public static int factionTagLengthMax = 10;
		
	protected static String _factionTagForceUpperCase = Lang.CONFIG_FACTION_FORCEUPPER.name();
	public static boolean factionTagForceUpperCase = false;

	protected static String _factionDescriptionLengthMax = Lang.CONFIG_FACTION_DESCMAX.name();
	public static int factionDescriptionLengthMax = -1;
	
	protected static String _newFactionsDefaultOpen = Lang.CONFIG_FACTION_DEFAULTOPEN.name();
	public static boolean newFactionsDefaultOpen = false;

	protected static String _factionMemberLimit = Lang.CONFIG_FACTION_MEMBERLIMIT.name();
	public static int factionMemberLimit = -1;
	
	protected static String _factionMemberLimitPeaceful = Lang.CONFIG_FACTION_MEMBERLIMITPEACEFUL.name();
	public static int factionMemberLimitPeaceful = 0;
	
	protected static String _newPlayerStartingFactionID = Lang.CONFIG_FACTION_STARTINGID.name();
	public static String newPlayerStartingFactionID = "0";

	protected static String _showMapFactionKey = Lang.CONFIG_FACTION_MAPKEY.name();
	public static boolean showMapFactionKey = true;
	
	protected static String _showNeutralFactionsOnMap = Lang.CONFIG_FACTION_SHOWNEUTRAL.name();
	public static boolean showNeutralFactionsOnMap = true;
	
	protected static String _showEnemyFactionsOnMap = Lang.CONFIG_FACTION_SHOWENEMY.name();
	public static boolean showEnemyFactionsOnMap = true;

	protected static String _allowColourCodesInFactionTitle = Lang.CONFIG_FACTION_ALLOWCOLOURS.name();
	public static boolean allowColourCodesInFactionTitle = false;
	
	protected static String _allowColourCodesInFactionDescription = Lang.CONFIG_FACTION_ALLOWCOLOURSDESC.name();
	public static boolean allowColourCodesInFactionDescription = false;

	protected static String _canLeaveWithNegativePower = Lang.CONFIG_FACTION_CANLEAVEWITHNEGATIVEPOWER.name();
	public static boolean canLeaveWithNegativePower = true;

	protected static String _disablePistonsInTerritory = Lang.CONFIG_FACTION_DISABLEPISTONS.name();
	public static boolean disablePistonsInTerritory = false;
	
	protected static String _broadcastTagChanges = Lang.CONFIG_FACTION_BROADCASTCHANGETAG.name();
	public static boolean broadcastTagChanges = false;

	protected static String _broadcastDescriptionChanges = Lang.CONFIG_FACTION_BROADCASTCHANGEDESC.name();
	public static boolean broadcastDescriptionChanges = false;

	protected static String _permanentFactionsDisableLeaderPromotion = Lang.CONFIG_FACTION_PERMNOPROMOTE.name();
	public static boolean permanentFactionsDisableLeaderPromotion = false;
	
	protected static String _autoKickRankMinimum = Lang.CONFIG_FACTION_AUTOKICK_MIN.name();
	public static Role autoKickRankMinimum = Role.COLEADER;

	protected static String _autoKickRankMax = Lang.CONFIG_FACTION_AUTOKICK_MAX.name();
	public static long autoKickCommandMax = 90;
	
	// -------------------------------------------------- //
	// FLAGS
	// -------------------------------------------------- //

	protected static String _sectionFlags = Lang.CONFIG_FLAGS_SECTION.name();

	protected static String _factionFlagMinRole = Lang.CONFIG_FLAGS_MINROLE.name();
	public static Role factionFlagMinRole = Role.COLEADER;

	protected static String _factionFlagToggleable = Lang.CONFIG_FLAGS_TOGGLEABLE.name();

	public static boolean factionFlagToggleablePermanent = false;
	public static boolean factionFlagToggleableExplosions = false;
	public static boolean factionFlagToggleablePeaceful = true;
	public static boolean factionFlagToggleableOpen = true;
	
	// -------------------------------------------------- //
	// DAMAGE MODIFIER
	// -------------------------------------------------- //

	protected static String _sectionDamageModifier = Lang.CONFIG_DAMAGEMODIFIER_SECTION.name();
	protected static String _damageModifierExplanation = Lang.CONFIG_DAMAGEMODIFIER_EXPLANATION.name();
	
	protected static String _damageModifierPercentRelationPlayer = Lang.CONFIG_DAMAGEMODIFIER_PERCENTRELATIONPLAYER.name();
	public static Map<Relation, Double> damageModifierPercentRelationPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	protected static String _damageModifierPercentRelationLocationByPlayer = Lang.CONFIG_DAMAGEMODIFIER_PERCENTRELATIONLAND.name();
	public static Map<Relation, Double> damageModifierPercentRelationLocationByPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	protected static String _damageModifierPercentRelationLocationByMob = Lang.CONFIG_DAMAGEMODIFIER_PERCENTRELATIONLANDBYMOB.name();
	public static Map<Relation, Double> damageModifierPercentRelationLocationByMob = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
		
	protected static String _damageModifierPercentWilderness = Lang.CONFIG_DAMAGEMODIFIER_GLOBALWILDERNESS.name();
	public static double damageModifierPercentWilderness = 100.0;
	
	protected static String _damageModifierPercentSafezone = Lang.CONFIG_DAMAGEMODIFIER_GLOBALSAFEZONE.name();
	public static double damageModifierPercentSafezone = 100.0;
	
	protected static String _damageModifierPercentWarzone = Lang.CONFIG_DAMAGEMODIFIER_GLOBALWARZONE.name();
	public static double damageModifierPercentWarzone = 100.0;
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsChat
	// -------------------------------------------------- //
	
	protected static String _sectionFactionChat = Lang.CONFIG_FACTIONCHAT_SECTION.name();
	
	public static FactionsChatConfig expansionsFactionsChat = new FactionsChatConfig();
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsFly
	// -------------------------------------------------- //
	
	protected static String _sectionFactionFly = Lang.CONFIG_FACTIONFLY_SECTION.name();
	
	public static FactionsFlyConfig expansionFactionsFly = new FactionsFlyConfig();
		
	// -------------------------------------------------- //
	// TASKS
	// -------------------------------------------------- //
	
	protected static String _sectionTasks = Lang.CONFIG_TASKS_SECTION.name();
	
	protected static String _saveToFileEveryXMinutes = Lang.CONFIG_TASKS_SAVETOFILEEVERYXMINUTES.name();
	public static double saveToFileEveryXMinutes = 30.0;

	protected static String _autoLeaveAfterDaysOfInactivity = Lang.CONFIG_TASKS_AUTOLEAVEAFTERDAYSOFINACTIVITY.name();
	public static long autoLeaveAfterDaysOfInactivity = 28;
	
	protected static String _autoLeaveRoutineRunsEveryXMinutes = Lang.CONFIG_TASKS_AUTOLEAVEROUTINEXMINUTES.name();
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	
	protected static String _autoLeaveRoutineMaxMillisecondsPerTick = Lang.CONFIG_TASKS_AUTOLEAVEROUTINEMAXMSPERTICK.name();
	public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;
	
	protected static String _removePlayerDataWhenBanned = Lang.CONFIG_TASKS_REMOVEWHENBANNED.name();
	public static boolean removePlayerDataWhenBanned = false;
	
	protected static String _autoLeaveDeleteFPlayerData = Lang.CONFIG_TASKS_AUTOLEAVEDELETEFPLAYERDATA.name();
	public static boolean autoLeaveDeleteFPlayerData = false; 
	
	// -------------------------------------------------- //
	// FORMAT
	// -------------------------------------------------- //
	protected static String _sectionFormat = Lang.CONFIG_FORMAT_SECTION.name();

	protected static String _sectionFormatDesc = Lang.CONFIG_FORMAT_DESC.name();

	public static Map<Character.UnicodeScript, Boolean> enabledScriptSupport = MiscUtil.map(
		Character.UnicodeScript.ARABIC, false,
		Character.UnicodeScript.BALINESE, false,
		Character.UnicodeScript.HAN, false,
		Character.UnicodeScript.KHMER, false
	);
	
	// -------------------------------------------------- //
	// SERVER LOGGING
	// -------------------------------------------------- //
	
	protected static String _sectionServerLogging = Lang.CONFIG_SERVERLOGGING_SECTION.name();

	protected static String _sectionServerLoggingDesc = Lang.CONFIG_SERVERLOGGING_DESC.name();

	public static boolean logFactionCreate = true;
	
	public static boolean logFactionDisband = true;
	
	public static boolean logFactionJoin = true;
	
	public static boolean logFactionKick = true;
	
	public static boolean logFactionBan = true;
	
	public static boolean logFactionLeave = true;
	
	public static boolean logLandClaims = true;
	
	public static boolean logLandUnclaims = true;
	
	public static boolean logMoneyTransactions = true;
	
	public static boolean logPlayerCommands = true;

	// -------------------------------------------------- //
	// EXPLOIT
	// -------------------------------------------------- //
	
	protected static String _sectionExploits = Lang.CONFIG_EXPLOITS_SECTION.name();

	protected static String _handleExploitObsidianGenerators = Lang.CONFIG_EXPLOITS_OBSIDIANGENERATORS.name();
	public static boolean handleExploitObsidianGenerators = true;
	
	protected static String _handleExploitEnderPearlClipping = Lang.CONFIG_EXPLOITS_ENDERPEARL.name();
	public static boolean handleExploitEnderPearlClipping = true;
	
	protected static String _handleExploitInteractionSpam = Lang.CONFIG_EXPLOITS_INTERACTIONSPAM.name();
	public static boolean handleExploitInteractionSpam = true;
	
	protected static String _handleExploitTNTWaterlog = Lang.CONFIG_EXPLOITS_WATERLOG.name();
	public static boolean handleExploitTNTWaterlog = false;
	
	protected static String _handleExploitLiquidFlow = Lang.CONFIG_EXPLOITS_LIQUIDFLOW.name();
	public static boolean handleExploitLiquidFlow = false;

	protected static String _findFactionsExploitLog = Lang.CONFIG_EXPLOITS_FINDEXPLOIT.name();
	public static boolean findFactionsExploitLog = false;
	
	public static long findFactionsExploitCooldownMils = 2000;
	
	// -------------------------------------------------- //
	// PORTALS
	// -------------------------------------------------- //
	
	protected static String _sectionPortals = Lang.CONFIG_PORTALS_SECTION.name();

	protected static String _sectionPortalsDesc = Lang.CONFIG_PORTALS_DESC.name();

	public static boolean portalsLimit = false;
	
	public static String portalsMinimumRelation = "MEMBER";
	
	// -------------------------------------------------- //
	// SCOREBOARD
	// -------------------------------------------------- //
	
	protected static String _sectionScoreboard = Lang.CONFIG_SCOREBOARD_SECTION.name();

	protected static String _sectionScoreboardDesc = Lang.CONFIG_SCOREBOARD_DESC.name();

	protected static String _scoreboardInChat = Lang.CONFIG_SCOREBOARD_SCOREBOARDINCHAT.name();
	public static boolean scoreboardInChat = false;
	
	protected static String _scoreboardExpiresSecs = Lang.CONFIG_SCOREBOARD_EXPIRES.name();
	public static long scoreboardExpiresSecs = 6;
	
	protected static String _scoreboardInfoEnabled = Lang.CONFIG_SCOREBOARD_INFOENABLED.name();
	public static boolean scoreboardInfoEnabled = false;
	
	public static List<String> scoreboardInfo = Lists.newArrayList(
		"&6Power",
		"{power}",
		"&3Members",
		"{online}/{members}",
		"&4Leader",
		"{leader}",
		"&bTerritory",
		"{chunks}"
	);
	
	protected static String _scoreboardDefaultEnabled = Lang.CONFIG_SCOREBOARD_DEFAULTENABLED.name();

	public static boolean scoreboardDefaultEnabled = false;
	
	public static String scoreboardDefaultTitle = "Default Title";
	
	public static int scoreboardDefaultUpdateIntervalSecs = 2;
	
	public static boolean scoreboardDefaultPrefixes = true;
	
	public static List<String> scoreboardDefault = Lists.newArrayList(
		"&6Your Faction",
		"{faction}",
		"&3Your Power",
		"{player-power}",
		"&aBalance",
		"${player-balance}"
	);
	
	protected static String _scoreboardFactionlessEnabled = Lang.CONFIG_SCOREBOARD_FACTIONLESSENABLED.name();

	public static boolean scoreboardFactionlessEnabled = false;
	
	public static List<String> scoreboardFactionless = Lists.newArrayList(
		"&6Factionless",
		"Join a faction!"
	);
	
	// -------------------------------------------------- //
	// TERRITORY TITLES
	// -------------------------------------------------- //

	protected static String _sectionTerritoryTitles = Lang.CONFIG_TERRITORYTITLES_SECTION.name();

	protected static String _sectionTerritoryTitlesDesc = Lang.CONFIG_TERRITORYTITLES_DESC.name();
	
	// show territory title on land change
	public static boolean territoryTitlesShow = true;
	
	public static String territoryTitlesHeader = "{factions_location_relation_colour}{factions_location_faction_name}";
	
	public static String territoryTitlesFooter = "{factions_location_faction_description}";
	
	public static int territoryTitlesTimeFadeInTicks = 20;
	
	public static int territoryTitlesTimeStayTicks = 20;
	
	public static int territoryTitlesTimeFadeOutTicks = 20;
	
	public static boolean hideFooterForWilderness = false;
	
	public static boolean hideFooterForWarzone = false;
	
	public static boolean hideFooterForSafezone = false;

	public static boolean rankChangeTitles = false;

	// -------------------------------------------------- //
	// TELEPORT TO SPAWN ON LOGOUT 
	// -------------------------------------------------- //
	
	protected static String _sectionTeleportToSpawn = Lang.CONFIG_TELEPROTTOSPAWN_SECTION.name();
	
	public static boolean teleportToSpawnOnLogoutInRelationEnabled = false;
	
	public static List<String> teleportToSpawnOnLogoutInRelationWorlds = Lists.newArrayList(
		"world",
		"world_nether"
	);

	public static List<Relation> teleportToSpawnOnLogoutInRelation = Lists.newArrayList(
		Relation.ENEMY
	);
	
	// -------------------------------------------------- //
	// TERRITORY
	// -------------------------------------------------- //

	protected static String _sectionTerritory = Lang.CONFIG_TERRITORY_SECTION.name();
	
	public static boolean territoryChangeText = true;
	
	public static boolean territoryChangePermissionGroups = true;
	
	// commands which will be prevented when in claimed territory of another faction
	public static Set<String> territoryNeutralDenyCommands = MiscUtil.linkedHashSet();
	public static Set<String> territoryEnemyDenyCommands = MiscUtil.linkedHashSet(
		"home",
		"sethome",
		"spawn",
		"tpahere",
		"tpaaccept",
		"tpa"
	);
	public static Set<String> territoryAllyDenyCommands = MiscUtil.linkedHashSet();
	public static Set<String> territoryTruceDenyCommands = MiscUtil.linkedHashSet();
	public static Set<String> warzoneDenyCommands = MiscUtil.linkedHashSet();
	public static Set<String> wildernessDenyCommands = MiscUtil.linkedHashSet();

	public static boolean territoryDenyBuild = true;
	public static boolean territoryDenyBuildWhenOffline = true;
	public static boolean territoryPainBuild = false;
	public static boolean territoryPainBuildWhenOffline = false;
	public static boolean territoryDenyUseage = true;
	public static boolean territoryEnemyDenyBuild = true;
	public static boolean territoryEnemyDenyBuildWhenOffline = true;
	public static boolean territoryEnemyPainBuild = false;
	public static boolean territoryEnemyPainBuildWhenOffline = false;
	public static boolean territoryEnemyDenyUseage = true;
	public static boolean territoryEnemyProtectMaterials = true;
	public static boolean territoryAllyDenyBuild = true;
	public static boolean territoryAllyDenyBuildWhenOffline = true;
	public static boolean territoryAllyPainBuild = false;
	public static boolean territoryAllyPainBuildWhenOffline = false;
	public static boolean territoryAllyDenyUseage = true;
	public static boolean territoryAllyProtectMaterials = true;
	public static boolean territoryTruceDenyBuild = true;
	public static boolean territoryTruceDenyBuildWhenOffline = true;
	public static boolean territoryTrucePainBuild = false;
	public static boolean territoryTrucePainBuildWhenOffline = false;
	public static boolean territoryTruceDenyUseage = true;
	public static boolean territoryTruceProtectMaterials = true;
	public static boolean territoryBlockCreepers = false;
	public static boolean territoryBlockCreepersWhenOffline = false;
	public static boolean territoryBlockFireballs = false;
	public static boolean territoryBlockFireballsWhenOffline = false;
	public static boolean territoryBlockTNT = false;
	public static boolean territoryBlockTNTWhenOffline = false;
	public static boolean territoryDenyEndermanBlocks = true;
	public static boolean territoryDenyEndermanBlocksWhenOffline = true;

	public static boolean safeZoneDenyBuild = true;
	public static boolean safeZoneDenyUseage = true;
	public static boolean safeZoneBlockTNT = true;
	public static boolean safeZonePreventAllDamageToPlayers = false;
	public static boolean safeZoneDenyEndermanBlocks = true;

	public static boolean warZoneDenyBuild = true;
	public static boolean warZoneDenyUseage = true;
	public static boolean warZoneBlockCreepers = false;
	public static boolean warZoneBlockFireballs = false;
	public static boolean warZoneBlockTNT = true;
	public static boolean warZonePowerLoss = true;
	public static boolean warZoneFriendlyFire = false;
	public static boolean warZoneDenyEndermanBlocks = true;

	public static boolean wildernessDenyBuild = false;
	public static boolean wildernessDenyUseage = false;
	public static boolean wildernessBlockCreepers = false;
	public static boolean wildernessBlockFireballs = false;
	public static boolean wildernessBlockTNT = false;
	public static boolean wildernessPowerLoss = true;
	public static boolean wildernessDenyEndermanBlocks = false;

	// for claimed areas where further faction-member ownership can be defined
	public static boolean ownedAreasEnabled = true;
	public static int ownedAreasLimitPerFaction = 0;
	public static boolean ownedAreasModeratorsCanSet = false;
	public static boolean ownedAreaModeratorsBypass = true;
	public static boolean ownedAreaDenyBuild = true;
	public static boolean ownedAreaPainBuild = false;
	public static boolean ownedAreaProtectMaterials = true;
	public static boolean ownedAreaDenyUseage = true;

	public static boolean ownedMessageOnBorder = true;
	public static boolean ownedMessageInsideTerritory = true;
	public static boolean ownedMessageByChunk = false;

	public static boolean pistonProtectionThroughDenyBuild = true;
	
	public static Set<CrossMaterial> territoryProtectedMaterials = EnumSet.of(
		CrossMaterial.WOODEN_DOOR,
		CrossMaterial.TRAP_DOOR,
		CrossMaterial.FENCE_GATE,
		CrossMaterial.DISPENSER,
		CrossMaterial.CHEST,
		CrossMaterial.FURNACE,
		CrossMaterial.BURNING_FURNACE,
		CrossMaterial.DIODE_BLOCK_OFF,
		CrossMaterial.DIODE_BLOCK_ON,
		CrossMaterial.JUKEBOX,
		CrossMaterial.BREWING_STAND,
		CrossMaterial.ENCHANTMENT_TABLE,
		CrossMaterial.CAULDRON,
		CrossMaterial.SOIL,
		CrossMaterial.BEACON,
		CrossMaterial.ANVIL,
		CrossMaterial.TRAPPED_CHEST,
		CrossMaterial.DROPPER,
		CrossMaterial.HOPPER
	);
	
	public static Set<CrossMaterial> territoryDenyUseageMaterials = EnumSet.of(
		CrossMaterial.FIREBALL,
		CrossMaterial.FLINT_AND_STEEL,
		CrossMaterial.BUCKET,
		CrossMaterial.WATER_BUCKET,
		CrossMaterial.LAVA_BUCKET
	);
	
	public static Set<CrossMaterial> territoryProtectedMaterialsWhenOffline = EnumSet.of(
		CrossMaterial.WOODEN_DOOR,
		CrossMaterial.TRAP_DOOR,
		CrossMaterial.FENCE_GATE,
		CrossMaterial.DISPENSER,
		CrossMaterial.CHEST,
		CrossMaterial.FURNACE,
		CrossMaterial.BURNING_FURNACE,
		CrossMaterial.DIODE_BLOCK_OFF,
		CrossMaterial.DIODE_BLOCK_ON,
		CrossMaterial.JUKEBOX,
		CrossMaterial.BREWING_STAND,
		CrossMaterial.ENCHANTMENT_TABLE,
		CrossMaterial.CAULDRON,
		CrossMaterial.SOIL,
		CrossMaterial.BEACON,
		CrossMaterial.ANVIL,
		CrossMaterial.TRAPPED_CHEST,
		CrossMaterial.DROPPER,
		CrossMaterial.HOPPER
	);
	
	public static Set<CrossMaterial> territoryDenyUseageMaterialsWhenOffline = EnumSet.of(
		CrossMaterial.FIREBALL,
		CrossMaterial.FLINT_AND_STEEL,
		CrossMaterial.BUCKET,
		CrossMaterial.WATER_BUCKET,
		CrossMaterial.LAVA_BUCKET
	);
	
	public static transient Set<CrossEntityType> safeZoneNerfedCreatureTypes = Sets.newHashSet(
		CrossEntityType.of(DefaultEntityType.BLAZE),
		CrossEntityType.of(DefaultEntityType.CAVE_SPIDER),
		CrossEntityType.of(DefaultEntityType.CREEPER),
		CrossEntityType.of(DefaultEntityType.ENDER_DRAGON),
		CrossEntityType.of(DefaultEntityType.ENDERMAN),
		CrossEntityType.of(DefaultEntityType.GHAST),
		CrossEntityType.of(DefaultEntityType.MAGMA_CUBE),
		CrossEntityType.of(DefaultEntityType.PIG_ZOMBIE),
		CrossEntityType.of(DefaultEntityType.SILVERFISH),
		CrossEntityType.of(DefaultEntityType.SKELETON),
		CrossEntityType.of(DefaultEntityType.SPIDER),
		CrossEntityType.of(DefaultEntityType.SLIME),
		CrossEntityType.of(DefaultEntityType.WITCH),
		CrossEntityType.of(DefaultEntityType.WITHER),
		CrossEntityType.of(DefaultEntityType.ZOMBIE)
	);
	
	// -------------------------------------------------- //
	// WARPS
	// -------------------------------------------------- //

	protected static String _sectionWarps = Lang.CONFIG_WARPS_SECTION.name();
	
	public static int warpsMax = 5;
	
	public static Map<String, Double> warpCost = MiscUtil.map(
		"set", 5.0,
		"delete", 5.0,
		"use", 5.0
	);
	
	// -------------------------------------------------- //
	// HOMES
	// -------------------------------------------------- //

	protected static String _sectionHomes = Lang.CONFIG_HOMES_SECTION.name();

	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportToOnDeath = true;
	public static boolean homesRespawnFromNoPowerLossWorlds = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportCommandEssentialsIntegration = true;
	public static boolean homesTeleportCommandSmokeEffectEnabled = true;
	public static float homesTeleportCommandSmokeEffectThickness = 3f;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;

	// -------------------------------------------------- //
	// PVP SETTINGS
	// -------------------------------------------------- //

	protected static String _sectionPVPSettings = Lang.CONFIG_PVPSETTINGS_SECTION.name();

	public static boolean disablePVPBetweenNeutralFactions = false;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	// -------------------------------------------------- //
	// PEACEFUL FACTIONS
	// -------------------------------------------------- //

	protected static String _sectionPeaceful = Lang.CONFIG_PEACEFUL_SECTION.name();

	public static boolean peacefulTerritoryDisablePVP = true;
	public static boolean peacefulTerritoryDisableMonsters = false;
	public static boolean peacefulTerritoryDisableBoom = false;
	public static boolean peacefulMembersDisablePowerLoss = true;
	
	// -------------------------------------------------- //
	// CLAIMS
	// -------------------------------------------------- //

	protected static String _sectionClaims = Lang.CONFIG_CLAIMS_SECTION.name();

	public static boolean claimsCanBeOutsideBorder = false;
	public static boolean claimsMustBeConnected = false;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int claimedLandsMax = 0;
	public static int lineClaimLimit = 5;

	public static int maxClaimRadius = 5;

	
	public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;

	public static int actionDeniedPainAmount = 1;

	// commands which will be prevented if the player is a member of a permanent faction
	public static Set<String> permanentFactionMemberDenyCommands = MiscUtil.linkedHashSet();
	
	// -------------------------------------------------- //
	// ECONOMY
	// -------------------------------------------------- //
	
	protected static String _sectionEconomy = Lang.CONFIG_ECONOMY_SECTION.name();
	
	// Economy settings
	public static boolean econEnabled = false;
	public static String econUniverseAccount = "universe-" + UUID.randomUUID();
	public static double econCostClaimWilderness = 30.0;
	public static double econCostClaimFromFactionBonus = 30.0;
	public static double econOverclaimRewardMultiplier = 0.0;
	public static double econClaimAdditionalMultiplier = 0.5;
	public static double econClaimRefundMultiplier = 0.7;
	public static double econClaimUnconnectedFee = 0.0;
	public static double econCostCreate = 100.0;
	public static double econCostOwner = 15.0;
	public static double econCostSethome = 30.0;
	public static double econCostJoin = 0.0;
	public static double econCostLeave = 0.0;
	public static double econCostKick = 0.0;
	public static double econCostBan = 0.0;
	public static double econCostUnban = 0.0;
	public static double econCostInvite = 0.0;
	public static double econCostHome = 0.0;
	public static double econCostTag = 0.0;
	public static double econCostDesc = 0.0;
	public static double econCostTitle = 0.0;
	public static double econCostList = 0.0;
	public static double econCostMap = 0.0;
	public static double econCostPower = 0.0;
	public static double econCostShow = 0.0;
	public static double econCostStuck = 0.0;
	public static double econCostOpen = 0.0;
	public static double econCostAlly = 0.0;
	public static double econCostTruce = 0.0;
	public static double econCostEnemy = 0.0;
	public static double econCostNeutral = 0.0;
	public static double econCostNoBoom = 0.0;

	public static boolean econAdditionalLandValueEnabled = false;
	public static List<LandValue> econAdditionalLandValue = Lists.newArrayList(
		LandValue.create("-2", 5, 10),
		LandValue.create("-2", 10, 10)	
	);

	// -------------------------------------------------- //
	// INTEGRATION: WORLD GUARD
	// -------------------------------------------------- //

	protected static String _sectionWorldGuard = Lang.CONFIG_WORLDGUARD_SECTION.name();

	public static boolean worldGuardChecking = false;
	public static boolean worldGuardBuildPriority = false;

	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public static boolean bankEnabled = true;
	public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

	// mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
	public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

	public static Set<String> worldsNoClaiming = new LinkedHashSet<>();
	public static Set<String> worldsNoPowerLoss = new LinkedHashSet<>();
	public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();
	public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();

	// -------------------------------------------------- //
	// INTEGRATION: DYNMAP
	// -------------------------------------------------- //
	
	protected static String _sectionDynmap = Lang.CONFIG_DYNMAP_SECTION.name();

	public static DynmapConfig dynmap = new DynmapConfig();
	
	// -------------------------------------------------- //
	// BUFFERS
	// -------------------------------------------------- //
	
	protected static String _sectionBuffers = Lang.CONFIG_BUFFERS_SECTION.name();

	public static int bufferWorldBorder = 0;
	public static int bufferFactions = 0;
	
	// -------------------------------------------------- //
	// STUCK
	// -------------------------------------------------- //
	
	protected static String _sectionStuck = Lang.CONFIG_STUCK_SECTION.name();

	public static long stuckDelay = 0;
	public static int stuckRadius = 0;
	
 	// -------------------------------------------------- //
	// RAIDS
	// -------------------------------------------------- //
	
	protected static String _sectionRaids = Lang.CONFIG_RAIDS_SECTION.name();

	protected static String _sectionRaidsDesc = Lang.CONFIG_RAIDS_DESC.name();

	public static boolean raidable = false;
	public static boolean raidableAllowOverclaim = false;
	public static int raidablePowerFreeze = 0;
	
	// -------------------------------------------------- //
	// VAULTS
	// -------------------------------------------------- //

	protected static String _sectionVaults = Lang.CONFIG_VAULTS_SECTION.name();

	public static String vaultPrefix = "faction-%s";
	public static int defaultMaxVaults = 0;
	
	// -------------------------------------------------- //
	// MAP
	// -------------------------------------------------- //

	protected static String _sectionMap = Lang.CONFIG_MAP_SECTION.name();
	
	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
	
	
	// -------------------------------------------------- //
	// HELP
	// -------------------------------------------------- //
	
	protected static String _sectionHelp = Lang.CONFIG_HELP_SECTION.name();
	protected static String _sectionHelpDesc = Lang.CONFIG_HELP_DESC.name();

	public static Boolean useOldHelp = false;
	public static Boolean useCustomHelp = false;
	public static Map<String, List<String>> helpPages = MiscUtil.map(
		"1", Lists.newArrayList(
			"&e&m----------------------------------------------",
			"				  &c&lFactions Help			   ",
			"&e&m----------------------------------------------",
			"&3/f create  &e>>  &7Create your own faction",
			"&3/f who	  &e>>  &7Show factions info",
			"&3/f tag	  &e>>  &7Change faction tag",
			"&3/f join	 &e>>  &7Join faction",
			"&3/f list	  &e>>  &7List all factions",
			"&e&m--------------&r &2/f help 2 for more &e&m--------------"
		),
		"2", Lists.newArrayList(
			"&e&m------------------&r&c&l Page 2 &e&m--------------------",
			"&3/f home	 &e>>  &7Teleport to faction home",
			"&3/f sethome &e>>  &7Set your faction home",
			"&3/f leave	&e>>  &7Leave your faction",
			"&3/f invite	&e>>  &7Invite a player to your faction",
			"&3/f deinvite &e>>  &7Revoke invitation to player",
			"&e&m--------------&r &2/f help 3 for more &e&m--------------"
		),
		"3", Lists.newArrayList(
			"&e&m------------------&r&c&l Page 3 &e&m--------------------",
			"&3/f claim	 &e>>  &7Claim land",
			"&3/f unclaim  &e>>  &7Unclaim land",
			"&3/f kick	  &e>>  &7Kick player from your faction",
			"&3/f mod	  &e>>  &7Set player role in faction",
			"&3/f coleader	  &e>>  &7Set player role in faction",
			"&e&m--------------&r &2/f help 4 for more &e&m--------------"
		),
		"4", Lists.newArrayList(
			"&e&m------------------&r&c&l Page 4 &e&m--------------------",
			"&3/f chat	 &e>>  &7Switch to faction chat",
			"&3/f version &e>>  &7Display version information",
			"&e&m--------------&r&2 End of /f help &e&m-----------------"
		)
	);
	
	// -------------------------------------------- //
	// LIST COMMAND
	// -------------------------------------------- //
		
	protected static String _sectionList = Lang.CONFIG_LIST_SECTION.name();

	public static List<String> showLines = Lists.newArrayList(
		"{header}",
		"<a>Description: <i>{description}",
		"<a>Joining: <i>{joining}	{peaceful}",
		"<a>Land / Power / Maxpower: <i> {chunks}/{power}/{maxPower}",
		"<a>Founded: <i>{create-date}",
		"<a>This faction is permanent, remaining even with no members.", /* only shows if faction is permanent*/
		"<a>Land value: <i>{land-value} {land-refund}",
		"<a>Balance: <i>{player-balance}",
		"<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list} ",
		"<a>Truces(<i>{truces}<a>/<i>{max-truces}<a>): {truces-list} ",
		"<a>Online: (<i>{online}<a>/<i>{members}<a>): {online-list}",
		"<a>Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}"
	);
	
	public static Boolean showMinimal = false;
	public static List<String> showExempt = Lists.newArrayList("some-faction-tag");
	
	// -------------------------------------------- //
	// SHOW COMMAND
	// -------------------------------------------- //
	
	protected static String _sectionShow = Lang.CONFIG_SHOW_SECTION.name();

	public static String listHeader = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
	public static String listFactionless = "<i>Factionless<i> {factionless} online";
	public static String listEntry = "<a>{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";
	
	public static List<String> listExempt = Lists.newArrayList("some-faction-tag");
	
	// -------------------------------------------------- //
	// BACKEND
	// -------------------------------------------------- //

	protected static String _sectionBackend = Lang.CONFIG_BACKEND_SECTION.name();
	protected static String _sectionBackendDesc = Lang.CONFIG_BACKEND_DESC.name();

	public static PersistType backEnd = PersistType.JSON;
	
	// -------------------------------------------- //
	// PERSISTANCE
	// -------------------------------------------- //
	
	private static transient Config instance = new Config();

	public static void load() {
		Persist.get().loadOrSaveDefault(instance, Config.class, "config");
	}

	public static void save() {
		Persist.get().save(instance, "config");
	}
	
	public static void loadSave() {
		load();
		save();
	}
	
}
