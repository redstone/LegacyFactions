package net.redstoneore.legacyfactions.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapStyle;
import net.redstoneore.legacyfactions.util.MiscUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Conf {
	
	
	// -------------------------------------------------- //
	// MISC
	// -------------------------------------------------- //
	
	// Enable debug mode, this will help diagnose problems. 
	public static boolean debug = false;
	
	// Enable metrics, this will send useful statistics to MCStats or bstats.
	public static boolean enableMetrics = true;
	
	// -------------------------------------------------- //
	// COMMANDS
	// -------------------------------------------------- //
	
	// Allow no slash required for commands.
	public static boolean allowNoSlashCommand = true;


	// -------------------------------------------------- //
	// WARMUPS
	// -------------------------------------------------- //

	// Warm up in seconds for warps 
	public static long warmupWarp = 0;
	
	// Warm up in seconds for home command
	public static long warmupHome = 0;
	
	// -------------------------------------------------- //
	// MAX RELATIONS
	// -------------------------------------------------- //
	
	// max relations for each relationship type
	public static Map<Relation, Integer> maxRelations = MiscUtil.map(
		Relation.ALLY, -1,
		Relation.TRUCE, -1,
		Relation.NEUTRAL, -1,
		Relation.ENEMY, -1
	);
		
	// -------------------------------------------------- //
	// COLOURS
	// -------------------------------------------------- //

	public static ChatColor colorMember = ChatColor.GREEN;
	public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
	public static ChatColor colorTruce = ChatColor.DARK_PURPLE;
	public static ChatColor colorNeutral = ChatColor.WHITE;
	public static ChatColor colorEnemy = ChatColor.RED;

	public static ChatColor colorPeaceful = ChatColor.GOLD;
	public static ChatColor colorWar = ChatColor.DARK_RED;

	// -------------------------------------------------- //
 	// TOOLTIPS
	// -------------------------------------------------- //

	// Tooltip templates
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
	// POWER
	// -------------------------------------------------- //

	public static double powerPlayerMax = 10.0;
	public static double powerPlayerMin = -10.0;
	public static double powerPlayerStarting = 0.0;
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	public static double powerPerDeath = 4.0; // A death makes you lose 4 power
	public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
	public static double powerOfflineLossPerDay = 0.0;  // players will lose this much power per day offline
	public static double powerOfflineLossLimit = 0.0;  // players will no longer lose power from being offline once their power drops to this amount or less
	public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)

	// -------------------------------------------------- //
	// PREFIX
	// -------------------------------------------------- //

	public static String playerPrefixAdmin = "***";
	public static String playerPrefixColeader = "**";
	public static String playerPrefixMod = "*";

	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //

	public static String factionDefaultRelation = "neutral";
	
	public static int factionTagLengthMin = 3;
	public static int factionTagLengthMax = 10;
	public static boolean factionTagForceUpperCase = false;

	public static boolean newFactionsDefaultOpen = false;

	// when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
	public static int factionMemberLimit = 0;

	// what faction ID to start new players in when they first join the server; default is 0, "no faction"
	public static String newPlayerStartingFactionID = "0";

	public static boolean showMapFactionKey = true;
	public static boolean showNeutralFactionsOnMap = true;
	public static boolean showEnemyFactionsOnMap = true;

	// Allow colour codes in faction title.
	public static boolean allowColourCodesInFactionTitle = false;
	
	// Allow colour codes in faction description.
	public static boolean allowColourCodesInFactionDescription = false;

	// Disallow joining/leaving/kicking while power is negative
	public static boolean canLeaveWithNegativePower = true;

	// Configuration for faction-only chat
	public static boolean factionOnlyChat = true;
	
	// If a chat manager plugin supports it, should be route the faction chat command to the channel?
	public static boolean factionChatChannelRoute = false;
	
	// Configuration on the Faction tag in chat messages.
	public static boolean chatTagEnabled = true;
	public static transient boolean chatTagHandledByAnotherPlugin = false;
	public static boolean chatTagRelationColored = true;
	public static String chatTagReplaceString = "[FACTION]";
	public static String chatTagInsertAfterString = "";
	public static String chatTagInsertBeforeString = "";
	public static int chatTagInsertIndex = 0;
	public static boolean chatTagPadBefore = false;
	public static boolean chatTagPadAfter = true;
	
	public static String chatTagFormat = "<factions_player_role_prefix><factions_faction_name>" + ChatColor.WHITE;
	public static String chatTagFormatFactionless = "";
	
	// Set to true to enable public chat formatting 
	public static boolean enableChatFormatPublic = false;
	
	public static String chatFormatPublic = "<factions_player_role_prefix><factions_faction_name>:" + ChatColor.WHITE + " <fc_message>";
	public static String chatFormatFaction = ChatColor.GREEN + "<factions_player_role_prefix><factions_player_name>:" + ChatColor.WHITE + " <fc_message>";
	public static String chatFormatAlliance = ChatColor.LIGHT_PURPLE + "<factions_player_role_prefix><factions_player_name>:" + ChatColor.WHITE + " <fc_message>";
	public static String chatFormatTruce = ChatColor.DARK_PURPLE + "<factions_player_role_prefix><factions_player_name>:" + ChatColor.WHITE + " <fc_message>";

	// default public channel for chat plugins
	public static String chatModePublicChannel = "Global";
	
	public static boolean broadcastDescriptionChanges = false;
	public static boolean broadcastTagChanges = false;

	
	public static double saveToFileEveryXMinutes = 30.0;

	public static double autoLeaveAfterDaysOfInactivity = 28.0;
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;  // 1 server tick is roughly 50ms, so default max 10% of a tick
	public static boolean removePlayerDataWhenBanned = true;
	public static boolean autoLeaveDeleteFPlayerData = false; // Deletes all player data when they auto leave (odd feature?)

	public static boolean disablePistonsInTerritory = false;
	
	public static Map<Character.UnicodeScript, Boolean> enabledScriptSupport = MiscUtil.map(
		Character.UnicodeScript.ARABIC, false,
		Character.UnicodeScript.BALINESE, false,
		Character.UnicodeScript.HAN, false,
		Character.UnicodeScript.KHMER, false
	);
	
	// -------------------------------------------------- //
	// SERVER LOGGING
	// -------------------------------------------------- //
	
	public static boolean logFactionCreate = true;
	public static boolean logFactionDisband = true;
	public static boolean logFactionJoin = true;
	public static boolean logFactionKick = true;
	public static boolean logFactionLeave = true;
	public static boolean logLandClaims = true;
	public static boolean logLandUnclaims = true;
	public static boolean logMoneyTransactions = true;
	public static boolean logPlayerCommands = true;

	// ----------------------------------------
	// EXPLOIT
	// ----------------------------------------

	public static boolean handleExploitObsidianGenerators = true;
	public static boolean handleExploitEnderPearlClipping = true;
	public static boolean handleExploitInteractionSpam = true;
	public static boolean handleExploitTNTWaterlog = false;
	public static boolean handleExploitLiquidFlow = false;

	public static boolean findFactionsExploitLog = false;
	public static long findFactionsExploitCooldown = 2000;
	
	// -------------------------------------------------- //
	// PORTALS
	// -------------------------------------------------- //
	
	public static boolean portalsLimit = false;
	public static String portalsMinimumRelation = "MEMBER";
	
	// -------------------------------------------------- //
	// SCOREBOARD
	// -------------------------------------------------- //

	public static boolean scoreboardInChat = false;
	public static long scoreboardExpiresSecs = 6;
	
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
	
	public static boolean scoreboardFactionlessEnabled = false;
	public static List<String> scoreboardFactionless = Lists.newArrayList(
		"&6Factionless",
		"Join a faction!"
	);
	
	// ----------------------------------------
	// WARPS
	// ----------------------------------------
	
	public static int warpsMax = 5;
	public static Map<String, Double> warpCost = MiscUtil.map(
		"set", 5.0,
		"delete", 5.0,
		"use", 5.0
	);
	
	// -------------------------------------------------- //
	// HOMES
	// -------------------------------------------------- //

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

	public static boolean disablePVPBetweenNeutralFactions = false;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	public static boolean peacefulTerritoryDisablePVP = true;
	public static boolean peacefulTerritoryDisableMonsters = false;
	public static boolean peacefulTerritoryDisableBoom = false;
	public static boolean peacefulMembersDisablePowerLoss = true;

	public static boolean permanentFactionsDisableLeaderPromotion = false;


	public static boolean claimsCanBeOutsideBorder = false;
	public static boolean claimsMustBeConnected = false;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int claimedLandsMax = 0;
	public static int lineClaimLimit = 5;

	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public static int radiusClaimFailureLimit = 9;

	public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;

	public static int actionDeniedPainAmount = 1;

	// commands which will be prevented if the player is a member of a permanent faction
	public static Set<String> permanentFactionMemberDenyCommands = MiscUtil.linkedHashSet();
	
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
	
	public static Set<Material> territoryProtectedMaterials = EnumSet.of(
		Material.WOODEN_DOOR,
		Material.TRAP_DOOR,
		Material.FENCE_GATE,
		Material.DISPENSER,
		Material.CHEST,
		Material.FURNACE,
		Material.BURNING_FURNACE,
		Material.DIODE_BLOCK_OFF,
		Material.DIODE_BLOCK_ON,
		Material.JUKEBOX,
		Material.BREWING_STAND,
		Material.ENCHANTMENT_TABLE,
		Material.CAULDRON,
		Material.SOIL,
		Material.BEACON,
		Material.ANVIL,
		Material.TRAPPED_CHEST,
		Material.DROPPER,
		Material.HOPPER
	);
	
	public static Set<Material> territoryDenyUseageMaterials = EnumSet.of(
		Material.FIREBALL,
		Material.FLINT_AND_STEEL,
		Material.BUCKET,
		Material.WATER_BUCKET,
		Material.LAVA_BUCKET
	);
	
	public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.of(
		Material.WOODEN_DOOR,
		Material.TRAP_DOOR,
		Material.FENCE_GATE,
		Material.DISPENSER,
		Material.CHEST,
		Material.FURNACE,
		Material.BURNING_FURNACE,
		Material.DIODE_BLOCK_OFF,
		Material.DIODE_BLOCK_ON,
		Material.JUKEBOX,
		Material.BREWING_STAND,
		Material.ENCHANTMENT_TABLE,
		Material.CAULDRON,
		Material.SOIL,
		Material.BEACON,
		Material.ANVIL,
		Material.TRAPPED_CHEST,
		Material.DROPPER,
		Material.HOPPER
	);
	
	public static Set<Material> territoryDenyUseageMaterialsWhenOffline = EnumSet.of(
		Material.FIREBALL,
		Material.FLINT_AND_STEEL,
		Material.BUCKET,
		Material.WATER_BUCKET,
		Material.LAVA_BUCKET
	);
	
	public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.of(
		EntityType.BLAZE,
		EntityType.CAVE_SPIDER,
		EntityType.CREEPER,
		EntityType.ENDER_DRAGON,
		EntityType.ENDERMAN,
		EntityType.GHAST,
		EntityType.MAGMA_CUBE,
		EntityType.PIG_ZOMBIE,
		EntityType.SILVERFISH,
		EntityType.SKELETON,
		EntityType.SPIDER,
		EntityType.SLIME,
		EntityType.WITCH,
		EntityType.WITHER,
		EntityType.ZOMBIE
	);
	
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


	// -------------------------------------------------- //
	// INTEGRATION: WORLD GUARD
	// -------------------------------------------------- //

	public static boolean worldGuardChecking = false;
	public static boolean worldGuardBuildPriority = false;
	
	// -------------------------------------------------- //
	// INTEGRATION: DYNMAP
	// -------------------------------------------------- //

	// Should the dynmap intagration be used?
	public static boolean dynmapUse = false;

	// Name of the Factions layer
	public static String dynmapLayerName = "Factions";

	// Should the layer be visible per default
	public static boolean dynmapLayerVisible = true;

	// Ordering priority in layer menu (low goes before high - default is 0)
	public static int dynmapLayerPriority = 2;

	// (optional) set minimum zoom level before layer is visible (0 = default, always visible)
	public static int dynmapLayerMinimumZoom = 0;

	// Format for popup - substitute values for macros
	public static String dynmapDescription =
		"<div class=\"infowindow\">\n"
		  + "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
		  + "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
		  + "<br>\n"
		  + "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
		  + "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
		  + "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
		  + "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
		  + "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
		  + "</br>\n"
		  + "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
		  + "<br>\n"
	  + "</div>";

	// Enable the %money% macro. Only do this if you know your economy manager is thread-safe.
	public static boolean dynmapDescriptionMoney = false;

	// Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)
	public static boolean dynmapVisibilityByFaction = true;

	// Optional setting to limit which regions to show.
	// If empty all regions are shown.
	// Specify Faction either by name or UUID.
	// To show all regions on a given world, add 'world:<worldname>' to the list.
	public static Set<String> dynmapVisibleFactions = new HashSet<>();

	// Optional setting to hide specific Factions.
	// Specify Faction either by name or UUID.
	// To hide all regions on a given world, add 'world:<worldname>' to the list.
	public static Set<String> dynmapHiddenFactions = new HashSet<>();

	// Region Style
	public static final transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
	public static final transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
	public static final transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
	public static final transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
	public static final transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
	public static final transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
	public static final transient boolean DYNMAP_STYLE_BOOST = false;

	public static DynmapStyle dynmapDefaultStyle = new DynmapStyle()
			.setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
			.setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
			.setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
			.setFillColor(DYNMAP_STYLE_FILL_COLOR)
			.setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
			.setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
			.setBoost(DYNMAP_STYLE_BOOST);

	// Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
	// Specify Faction either by name or UUID.
	public static Map<String, DynmapStyle> dynmapFactionStyles = ImmutableMap.of(
			"SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
			"WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
	);


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
	// BUFFERS
	// -------------------------------------------------- //
	
	public static int bufferWorldBorder = 0;
	public static int bufferFactions = 0;
	
	// -------------------------------------------------- //
	// STUCK
	// -------------------------------------------------- //
	
	public static long stuckDelay = 0;
	public static int stuckRadius = 0;
	
 	// -------------------------------------------------- //
	// RAIDS
	// -------------------------------------------------- //
	
	public static boolean raidable = false;
	public static boolean raidableAllowOverclaim = true;
	public static int raidablePowerFreeze = 0;
	
	// -------------------------------------------------- //
	// VAULTS
	// -------------------------------------------------- //

	public static String vaultPrefix = "faction-%s";
	public static int defaultMaxVaults = 0;

	// -------------------------------------------------- //
	// BACKEND
	// -------------------------------------------------- //

	public static Backend backEnd = Backend.JSON;
		
	// -------------------------------------------------- //
	// MAP
	// -------------------------------------------------- //

	public static transient int mapHeight = 8;
	public static transient int mapWidth = 39;
	public static transient char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
	
	
	// -------------------------------------------------- //
	// HELP
	// -------------------------------------------------- //
	
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
	
	public static String listHeader = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
	public static String listFactionless = "<i>Factionless<i> {factionless} online";
	public static String listEntry = "<a>{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";
	
	public static List<String> listExempt = Lists.newArrayList("some-faction-tag");
	
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
	public static List<String> cmdAliasesHelp = Lists.newArrayList("help", "h", "?");
	public static List<String> cmdAliasesHome = Lists.newArrayList("home");
	public static List<String> cmdAliasesInvite = Lists.newArrayList("invite", "inv");
	public static List<String> cmdAliasesJoin = Lists.newArrayList("join");
	public static List<String> cmdAliasesKick = Lists.newArrayList("kick");
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
	public static List<String> cmdAliasesUnclaim = Lists.newArrayList("unclaim");
	public static List<String> cmdAliasesUnclaimAll = Lists.newArrayList("unclaimall");
	public static List<String> cmdAliasesVersion = Lists.newArrayList("version");
	public static List<String> cmdAliasesWarp = Lists.newArrayList("warp", "warps");
	public static List<String> cmdAliasesWarunclaimall = Lists.newArrayList("warunclaimall");
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	private static transient Conf i = new Conf();

	public static void load() {
		Factions.get().getPersist().loadOrSaveDefault(i, Conf.class, "conf");
	}

	public static void save() {
		Factions.get().getPersist().save(i);
	}

	public enum Backend {
		JSON,
		;
	}
}

