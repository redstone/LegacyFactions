package net.redstoneore.legacyfactions.config;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.expansion.chat.FactionsChatConfig;
import net.redstoneore.legacyfactions.expansion.fly.FactionsFlyConfig;
import net.redstoneore.legacyfactions.integration.dynmap.DynmapConfig;
import net.redstoneore.legacyfactions.struct.LandValue;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.cross.CrossColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;
import net.redstoneore.legacyfactions.util.cross.CrossColour.DefaultChatColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType.DefaultEntityType;

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
	// POWER
	// -------------------------------------------------- //
	
	protected static String _sectionPower = Lang.CONFIG_POWER_SECTION.name();

	public static double powerPlayerMax = 10.0;
	
	public static double powerPlayerMin = -10.0;
	
	public static double powerPlayerStarting = 0.0;
	
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	
	public static double powerPerDeath = 4.0; 
	
	public static boolean powerRegenOffline = false; 
	
	public static double powerOfflineLossPerDay = 0.0;
	
	public static double powerOfflineLossLimit = 0.0;
	
	public static double powerFactionMax = 0.0; 
	
	// -------------------------------------------------- //
	// PLAYER PREFIX
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

	public static int factionDescriptionLengthMax = -1;
	
	public static boolean newFactionsDefaultOpen = false;

	public static int factionMemberLimit = 0;
	
	public static int factionMemberLimitPeaceful = 0;
	
	public static String newPlayerStartingFactionID = "0";

	public static boolean showMapFactionKey = true;
	
	public static boolean showNeutralFactionsOnMap = true;
	
	public static boolean showEnemyFactionsOnMap = true;

	public static boolean allowColourCodesInFactionTitle = false;
	
	public static boolean allowColourCodesInFactionDescription = false;

	public static boolean canLeaveWithNegativePower = true;

	public static boolean disablePistonsInTerritory = false;

	public static boolean broadcastDescriptionChanges = false;
	
	public static boolean broadcastTagChanges = false;
	
	public static boolean permanentFactionsDisableLeaderPromotion = false;
	
	public static Role autoKickRankMinimum = Role.COLEADER;

	public static long autoKickCommandMax = 90;
	// -------------------------------------------------- //
	// FLAGS
	// -------------------------------------------------- //

	public static Role factionFlagMinRole = Role.COLEADER;

	public static boolean factionFlagToggleablePermanent = false;
	public static boolean factionFlagToggleableExplosions = false;
	public static boolean factionFlagToggleablePeaceful = true;
	public static boolean factionFlagToggleableOpen = true;
	
	// -------------------------------------------------- //
	// DAMAGE MODIFIER
	// -------------------------------------------------- //

	public static Map<Relation, Double> damageModifierPercentRelationPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	public static Map<Relation, Double> damageModifierPercentRelationLocationByPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	public static Map<Relation, Double> damageModifierPercentRelationLocationByMob = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
		
	public static double damageModifierPercentWilderness = 100.0;
	
	public static double damageModifierPercentSafezone = 100.0;
	
	public static double damageModifierPercentWarzone = 100.0;
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsChat
	// -------------------------------------------------- // 
	public static FactionsChatConfig expansionsFactionsChat = new FactionsChatConfig();
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsFly
	// -------------------------------------------------- //
	public static FactionsFlyConfig expansionFactionsFly = new FactionsFlyConfig();
		
	// -------------------------------------------------- //
	// TASKS
	// -------------------------------------------------- //
	public static double saveToFileEveryXMinutes = 30.0;

	public static long autoLeaveAfterDaysOfInactivity = 28;
	
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	
	public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;
	
	public static boolean removePlayerDataWhenBanned = false;
	
	public static boolean autoLeaveDeleteFPlayerData = false; 
	
	// -------------------------------------------------- //
	// FORMAT
	// -------------------------------------------------- //
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
	
	public static boolean logFactionBan = true;
	
	public static boolean logFactionLeave = true;
	
	public static boolean logLandClaims = true;
	
	public static boolean logLandUnclaims = true;
	
	public static boolean logMoneyTransactions = true;
	
	public static boolean logPlayerCommands = true;

	// -------------------------------------------------- //
	// EXPLOIT
	// -------------------------------------------------- //
	public static boolean handleExploitObsidianGenerators = true;
	
	public static boolean handleExploitEnderPearlClipping = true;
	
	public static boolean handleExploitInteractionSpam = true;
	
	public static boolean handleExploitTNTWaterlog = false;
	
	public static boolean handleExploitLiquidFlow = false;

	public static boolean findFactionsExploitLog = false;
	
	public static long findFactionsExploitCooldownMils = 2000;
	
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
	
	// -------------------------------------------------- //
	// TITLES
	// -------------------------------------------------- //

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
	public static boolean teleportToSpawnOnLogoutInRelationEnabled = false;
	
	public static List<String> teleportToSpawnOnLogoutInRelationWorlds = Lists.newArrayList(
		"world",
		"world_nether"
	);

	public static List<Relation> teleportToSpawnOnLogoutInRelation = Lists.newArrayList(
		Relation.ENEMY
	);
	
	// -------------------------------------------------- //
	// TERRITORY CHANGE
	// -------------------------------------------------- //

	public static boolean territoryChangeText = true;
	
	public static boolean territoryChangePermissionGroups = true;
	
	// -------------------------------------------------- //
	// WARPS
	// -------------------------------------------------- //

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

	// -------------------------------------------------- //
	// PVP SETTINGS
	// -------------------------------------------------- //

	public static boolean disablePVPBetweenNeutralFactions = false;
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

	public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

	// -------------------------------------------------- //
	// PEACEFUL FACTIONS
	// -------------------------------------------------- //

	public static boolean peacefulTerritoryDisablePVP = true;
	public static boolean peacefulTerritoryDisableMonsters = false;
	public static boolean peacefulTerritoryDisableBoom = false;
	public static boolean peacefulMembersDisablePowerLoss = true;
	
	// -------------------------------------------------- //
	// CLAIMS
	// -------------------------------------------------- //

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
	
	public static DynmapConfig dynmap = new DynmapConfig();
	
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

	public static PersistType backEnd = PersistType.JSON;
		
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
