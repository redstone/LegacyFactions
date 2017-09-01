package net.redstoneore.legacyfactions.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.cli.doc.DocDescription;
import net.redstoneore.legacyfactions.cli.doc.DocSection;
import net.redstoneore.legacyfactions.entity.persist.Persist;
import net.redstoneore.legacyfactions.expansion.chat.FactionsChatConfig;
import net.redstoneore.legacyfactions.expansion.fly.FactionsFlyConfig;
import net.redstoneore.legacyfactions.util.MiscUtil;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType;
import net.redstoneore.legacyfactions.util.cross.CrossMaterial;
import net.redstoneore.legacyfactions.util.cross.CrossColour;
import net.redstoneore.legacyfactions.util.cross.CrossColour.DefaultChatColour;
import net.redstoneore.legacyfactions.util.cross.CrossEntityType.DefaultEntityType;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Conf {
	
	/**
	 * Version of this config, used for migrations.
	 */
	public static transient double version = 1.0;
	
	// -------------------------------------------------- //
	// MISC
	// -------------------------------------------------- //
	@DocSection(name = "Misc")
	
	@DocDescription(
		title = "Debug Mode",
		description = "If enabled, fine details will be sent to the console."
	)
	public static boolean debug = false;
		
	@DocDescription(
		title = "Metrics",
		description = "If enabled, useful statistics will be uploaded about your server. This helps with development and planning!"
	)
	public static boolean logStatistics = true;
	
	// -------------------------------------------------- //
	// Non-1.6 Features
	// -------------------------------------------------- //
	@DocSection(name = "Non 1.6 Features")
	
	@DocDescription(title = "Truces", description = "To disable truces, set to false.")
	public static boolean enableTruces = true;
	
	@DocDescription(title = "Coleaders", description = "To disable coleaders, set to false.")
	public static boolean enableColeaders = true;
	
	@DocDescription(title = "Flags", description = "To disable flags, set to false.")
	public static boolean enableFlags = false;
	
	// -------------------------------------------------- //
	// COMMANDS
	// -------------------------------------------------- //
	@DocSection(name = "Commands")
	
	@DocDescription(
		title = "Allow no slash commands",
		description = "If enabled it will allow using factions commands without the initial slash. <br>" + 
		              "For example typing 'f claim' instead of '/f claim'"
	)
	public static boolean allowNoSlashCommand = true;

	// -------------------------------------------------- //
	// WARMUPS
	// -------------------------------------------------- //
	@DocSection(name = "Warmups")
	
	@DocDescription(
		title = "Warp Warmup",
		description = "How long (in seconds) the warmup for warps should be"
	)
	public static long warmupWarp = 0;
	
	@DocDescription(
		title = "Home Warmup",
		description = "How long (in seconds) the warmup for homes should be"
	)
	public static long warmupHome = 0;
	
	// -------------------------------------------------- //
	// RELATIONS
	// -------------------------------------------------- //
	@DocSection(name = "Relations")

	@DocDescription(
		title = "Max Relations per Relationship",
		description = "The maximum relationship for each type, set to -1 to disable"
	)
	public static Map<Relation, Integer> maxRelations = MiscUtil.map(
		Relation.ALLY, -1,
		Relation.TRUCE, -1,
		Relation.NEUTRAL, -1,
		Relation.ENEMY, -1
	);
	
	@DocDescription(
		title = "Member Relation Colour",
		description = "The colour used to define the realtion member"
	)
	public static CrossColour colorMember = CrossColour.of(DefaultChatColour.GREEN);
	
	@DocDescription(
		title = "Ally Relation Colour",
		description = "The colour used to define the realtion elly"
	)
	public static CrossColour colorAlly = CrossColour.of(DefaultChatColour.LIGHT_PURPLE);
	
	@DocDescription(
		title = "truce Relation Colour",
		description = "The colour used to define the relation truce"
	)
	public static CrossColour colorTruce = CrossColour.of(DefaultChatColour.DARK_PURPLE);
	
	@DocDescription(
		title = "Neutral Relation Colour",
		description = "The colour used to define the relation neutral"
	)
	public static CrossColour colorNeutral = CrossColour.of(DefaultChatColour.WHITE);
	
	@DocDescription(
		title = "Enemy Relation Colour",
		description = "The colour used to define the relation enemy"
	)
	public static CrossColour colorEnemy = CrossColour.of(DefaultChatColour.RED);

	@DocDescription(
		title = "Peaceful Relation Colour",
		description = "The colour used to define the relation peaceful"
	)
	public static CrossColour colorPeaceful = CrossColour.of(DefaultChatColour.GOLD);
	
	// -------------------------------------------------- //
 	// TOOLTIPS
	// -------------------------------------------------- //
	@DocSection(name = "Tooltips")
	
	@DocDescription(
		title = "Tooltip Templates",
		description = "These are the templates for tooltips"
	)
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
	@DocSection(name = "Power")

	@DocDescription(
		title = "Max Player Power",
		description = "The maximum player power"
	)
	public static double powerPlayerMax = 10.0;
	
	@DocDescription(
		title = "Minimum Player Power",
		description = "The minimum player power"
	)
	public static double powerPlayerMin = -10.0;
	
	@DocDescription(
		title = "Default Starting Player Power",
		description = "The power a player starts on when they first join"
	)
	public static double powerPlayerStarting = 0.0;
	
	@DocDescription(
		title = "Power Regenerated Per Minute",
		description = "The amount of power regenerated pre minute. 0.2 = 5 minute to heal 1 power (aka 50 minutes for 10 power)"
	)
	public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
	
	@DocDescription(
		title = "Power Loss Per Death",
		description = "The amount of power lost on death"
	)
	public static double powerPerDeath = 4.0; 
	
	@DocDescription(
		title = "Power Regen Offline",
		description = "If enabled, players will regenerate power while they're offline."
	)
	public static boolean powerRegenOffline = false; 
	
	@DocDescription(
		title = "Offline Power Loss Per Day",
		description = "How much power a player will lose per day they are offline"
	)
	public static double powerOfflineLossPerDay = 0.0;
	
	@DocDescription(
		title = "Offline Power Loss Limit",
		description = "Offline power loss will not continue dropping once their drop drops to this amount or less"
	)
	public static double powerOfflineLossLimit = 0.0;
	
	@DocDescription(
		title = "Faction Max Power",
		description = "Set to 0 to disable. If greated than 0, this will be the maximum power a faction can have. Additional power will act as a 'buffer'."
	)
	public static double powerFactionMax = 0.0; 

	// -------------------------------------------------- //
	// PLAYER PREFIX
	// -------------------------------------------------- //
	@DocSection(name = "Player Prefix")

	@DocDescription(
		title = "Admin Prefix",
		description = "The prefix for faction admins"
	)
	public static String playerPrefixAdmin = "***";
	
	@DocDescription(
		title = "Coleader Prefix",
		description = "The prefix for faction coloeaders"
	)
	public static String playerPrefixColeader = "**";
	
	@DocDescription(
		title = "Coleader Prefix",
		description = "The prefix for faction moderators"
	)
	public static String playerPrefixMod = "*";

	// -------------------------------------------------- //
	// FACTION
	// -------------------------------------------------- //
	@DocSection(name = "Faction")

	@DocDescription(
		title = "Default Relation",
		description = "The default relation of factions"
	)
	public static String factionDefaultRelation = "neutral";
	
	@DocDescription(
		title = "Faction Tag Minimum Length",
		description = "The minimum length of a faction tag"
	)
	public static int factionTagLengthMin = 3;
	
	@DocDescription(
		title = "Faction Tag Maxmimum Length",
		description = "The maximum length of a faction tag"
	)
	public static int factionTagLengthMax = 10;
		
	@DocDescription(
		title = "Faction Tag Force Upper Case",
		description = "Force the tag to be uppercase."
	)
	public static boolean factionTagForceUpperCase = false;

	@DocDescription(
		title = "Faction Description Maxmimum Length",
		description = "The maximum length of a faction description. Set to -1 to disable."
	)
	public static int factionDescriptionLengthMax = -1;
	
	@DocDescription(
		title = "Default to Open",
		description = "If true, factions will default to open."
	)
	public static boolean newFactionsDefaultOpen = false;

	@DocDescription(
		title = "Member Limit",
		description = "Set to 0 to disable. If a faction hits this limit, players will no longer be able to join using `/f join`"
	)
	public static int factionMemberLimit = 0;
	
	@DocDescription(
		title = "New Player Starting Faction ID",
		description = "This is default set to 0 (no faction), but if you want players to automatically join a faction set this to that ID."
	)
	public static String newPlayerStartingFactionID = "0";

	@DocDescription(
		title = "Show Faction Keys on Map",
		description = "If true, faction names will show as keys on the faction map."
	)
	public static boolean showMapFactionKey = true;
	
	@DocDescription(
		title = "Show Neutral Factions On Map",
		description = "If true, neutral factions will be visible on the map."
	)
	public static boolean showNeutralFactionsOnMap = true;
	
	@DocDescription(
		title = "Show Enemy Factions On Map",
		description = "If true, enemy factions will be visible on the map."
	)
	public static boolean showEnemyFactionsOnMap = true;

	@DocDescription(
		title = "Allow Colour Codes In Faction Title",
		description = "If true, colour codes will be allowed in faction titles."
	)
	public static boolean allowColourCodesInFactionTitle = false;
	
	@DocDescription(
		title = "Allow Colour Codes In Faction Description",
		description = "If true, colour codes will be allowed in faction descriptions."
	)
	public static boolean allowColourCodesInFactionDescription = false;

	@DocDescription(
		title = "Disallow Leaving with negative power",
		description = "If false, players can not join, leave, or be kicekd while power is negative."
	)
	public static boolean canLeaveWithNegativePower = true;

	@DocDescription(
		title = "Disable Pistons In Territory",
		description = "If true, pistons will be disable in territories."
	)
	public static boolean disablePistonsInTerritory = false;

	@DocDescription(
		title = "Broadcast Description Changes",
		description = "If true, a broadcast will be sent when a faction description is changed."
	)
	public static boolean broadcastDescriptionChanges = false;
	
	@DocDescription(
		title = "Broadcast Tag Changes",
		description = "If true, a broadcast will be sent when a faction name tag is changed."
	)
	public static boolean broadcastTagChanges = false;

	// -------------------------------------------------- //
	// FLAGS
	// -------------------------------------------------- //
	@DocSection(name = "Flags")

	@DocDescription(
		title = "Minimum role to manage flags",
		description = "The minimum role to manage flags. Can be MEMBER, MODERATOR, COLEADER, or ADMIN"
	)
	public static Role factionFlagMinRole = Role.COLEADER;

	public static boolean factionFlagToggleablePermanent = false;
	public static boolean factionFlagToggleableExplosions = false;
	public static boolean factionFlagToggleablePeaceful = true;
	public static boolean factionFlagToggleableOpen = true;
	
	// -------------------------------------------------- //
	// DAMAGE MODIFIER
	// -------------------------------------------------- //
	@DocSection(name = "Damage Modifier")
	
	@DocDescription(
		title = "Damage Modifier For Relation To Player",
		description = "Damage modifier as a percent for each relation. 100 = default. 120 = 20% "+
		              "extra. These will be used in conjuction with wilderness/safezone/warzone " +
		              "damage modifiers"
	)
	public static Map<Relation, Double> damageModifierPercentRelationPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	@DocDescription(
		title = "Damage Modifier By Player For Relation To Location",
		description = "Damage modifier as a percent for each relation. 100 = default. 120 = 20% "+
		              "extra. These will be used in conjuction with wilderness/safezone/warzone " +
		              "damage modifiers"
	)
	public static Map<Relation, Double> damageModifierPercentRelationLocationByPlayer = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
	
	@DocDescription(
		title = "Damage Modifier By Mob For Relation To Location",
		description = "Damage modifier as a percent for each relation. 100 = default. 120 = 20% "+
		              "extra. These will be used in conjuction with wilderness/safezone/warzone " +
			          "damage modifiers"
	)
	public static Map<Relation, Double> damageModifierPercentRelationLocationByMob = MiscUtil.newMap(
		Relation.ALLY, 100.0,
		Relation.ENEMY, 100.0,
		Relation.MEMBER, 100.0,
		Relation.NEUTRAL, 100.0,
		Relation.TRUCE, 100.0
	);
		
		
	
	@DocDescription(
		title = "Damage Modifier For Wilderness",
		description = "Damage modifier as a percent for wilderness. 100 = default. 120 = 20% extra."+
		              " This will be used in conjunction with relation damage modifiers."
	)
	public static double damageModifierPercentWilderness = 100.0;
	
	@DocDescription(
		title = "Damage Modifier For Safezone",
		description = "Damage modifier as a percent for safezone. 100 = default. 120 = 20% extra."+
		              " This will be used in conjunction with relation damage modifiers."
	)
	public static double damageModifierPercentSafezone = 100.0;
	
	@DocDescription(
		title = "Damage Modifier For Warzone",
		description = "Damage modifier as a percent for warzone. 100 = default. 120 = 20% extra"+ 
		              " This will be used in conjunction with relation damage modifiers."
	)
	public static double damageModifierPercentWarzone = 100.0;
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsChat
	// -------------------------------------------------- // 
	@DocSection(name = "Expansion: FactionsChat")

	public static FactionsChatConfig expansionsFactionsChat = new FactionsChatConfig();
	
	// -------------------------------------------------- //
	// EXPANSION: FactionsFly
	// -------------------------------------------------- //
	@DocSection(name = "Expansion: FactionsFly")

	public static FactionsFlyConfig expansionFactionsFly = new FactionsFlyConfig();
		
	// -------------------------------------------------- //
	// TASKS
	// -------------------------------------------------- //
	@DocSection(name = "Tasks")

	@DocDescription(
		title = "Save Interval",
		description = "How often (in minutes) should we save files."
	)
	public static double saveToFileEveryXMinutes = 30.0;

	@DocDescription(
		title = "Inactivity Autoleave: how many days?",
		description = "After how many days of inactivty should a player autoleave their faction"
	)
	public static long autoLeaveAfterDaysOfInactivity = 28;
	
	@DocDescription(
		title = "Inactivity Autoleave: timer frequency",
		description = "How often (in minutes) should we run the autoleave task"
	)
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	
	@DocDescription(
		title = "Inactivity Autoleave: max ms per tick",
		description = "The max ms per tick that the task can run -  1 server tick is roughly 50ms, so the default (5) is 10% of a tick"
	)
	public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;
	
	@DocDescription(
		title = "Remove Player Data When Banned",
		description = "Removes all player data when they are banned. This is destructive, consider wisely."
	)
	public static boolean removePlayerDataWhenBanned = false;
	
	@DocDescription(
		title = "Remove Player From Autoleave",
		description = "Removes all player data when they autoleave from inactivty. This is destructive, consider wisely."
	)
	public static boolean autoLeaveDeleteFPlayerData = false; 
	
	// -------------------------------------------------- //
	// FORMAT
	// -------------------------------------------------- //
	@DocSection(name = "Format")

	@DocDescription(
			title = "Enabled Script Support",
			description = "Add script support for characters other than English. See: https://github.com/redstone/LegacyFactions/wiki/Multilingual-Script-Support"
		)
	public static Map<Character.UnicodeScript, Boolean> enabledScriptSupport = MiscUtil.map(
		Character.UnicodeScript.ARABIC, false,
		Character.UnicodeScript.BALINESE, false,
		Character.UnicodeScript.HAN, false,
		Character.UnicodeScript.KHMER, false
	);
	
	// -------------------------------------------------- //
	// SERVER LOGGING
	// -------------------------------------------------- //
	@DocSection(name = "Server Logging")

	@DocDescription(
			title = "Log Factions Create",
			description = "Log Factions Create Event"
		)
	public static boolean logFactionCreate = true;
	
	@DocDescription(
			title = "Log Factions Disband",
			description = "Log Factions Disband Event"
		)
	public static boolean logFactionDisband = true;
	
	@DocDescription(
			title = "Log Factions Join",
			description = "Log Factions Join Event"
		)
	public static boolean logFactionJoin = true;
	
	@DocDescription(
			title = "Log Factions Kick",
			description = "Log Factions Kick Event"
		)
	public static boolean logFactionKick = true;
	
	@DocDescription(
			title = "Log Factions Ban",
			description = "Log Factions Ban Event"
		)
	public static boolean logFactionBan = true;
	
	@DocDescription(
			title = "Log Factions Leave",
			description = "Log Factions Leave Event"
		)
	public static boolean logFactionLeave = true;
	
	@DocDescription(
			title = "Log Factions Claim Events",
			description = "Log Factions Claim Events"
		)
	public static boolean logLandClaims = true;
	
	@DocDescription(
			title = "Log Factions Unclaim Events",
			description = "Log Factions Unclaim Event"
		)
	public static boolean logLandUnclaims = true;
	
	@DocDescription(
			title = "Log Factions Money Transaction Events",
			description = "Log Factions Money Transaction Events"
		)
	public static boolean logMoneyTransactions = true;
	
	@DocDescription(
			title = "Log Player Commands",
			description = "Log Player Commands"
		)
	public static boolean logPlayerCommands = true;

	// -------------------------------------------------- //
	// EXPLOIT
	// -------------------------------------------------- //
	@DocSection(name = "Exploits")

	@DocDescription(
			title = "Obsidian Generators",
			description = "Block Obsidian Generators"
		)
	public static boolean handleExploitObsidianGenerators = true;
	
	@DocDescription(
			title = "Ender Pearl Clipping",
			description = "Block Ender Pearl Clipping"
		)
	public static boolean handleExploitEnderPearlClipping = true;
	
	@DocDescription(
			title = "Interaction Spam",
			description = "Block Interaction Spam"
		)
	public static boolean handleExploitInteractionSpam = true;
	
	@DocDescription(
			title = "TNT Waterlog",
			description = "Block TNT Waterlog"
		)
	public static boolean handleExploitTNTWaterlog = false;
	
	@DocDescription(
			title = "Liquid Flow",
			description = "Block liquid flow between territories"
		)
	public static boolean handleExploitLiquidFlow = false;

	@DocDescription(
			title = "Factions Find Exploit Log",
			description = "Log when a factions find exploit is triggered"
		)
	public static boolean findFactionsExploitLog = false;
	
	@DocDescription(
			title = "Factions Find Exploit Cooldown",
			description = "Cooldown between map updating"
		)
	public static long findFactionsExploitCooldownMils = 2000;
	
	// -------------------------------------------------- //
	// PORTALS
	// -------------------------------------------------- //
	@DocSection(name = "Portals")

	@DocDescription(
			title = "Portals Limit",
			description = "Limits exit portal creation to where they're allowed to based on relation"
		)
	public static boolean portalsLimit = false;
	
	@DocDescription(
			title = "Portals Limit Minimum Relation",
			description = "The minimum relation where we can create exit portals (if portalsLimit is true)"
		)
	public static String portalsMinimumRelation = "MEMBER";
	
	// -------------------------------------------------- //
	// SCOREBOARD
	// -------------------------------------------------- //
	@DocSection(name = "Scoreboard")

	@DocDescription(
			title = "Scoreboard In Chat",
			description = "Should we also show the scoreboard in chat?"
		)
	public static boolean scoreboardInChat = false;
	
	@DocDescription(
			title = "Scoreboard Expires",
			description = "After how many seconds does the scoreboard expire"
		)
	public static long scoreboardExpiresSecs = 6;
	
	@DocDescription(
			title = "Scoreboard Info",
			description = "Should we enable the info scoreboard?"
		)
	public static boolean scoreboardInfoEnabled = false;
	
	@DocDescription(
			title = "Scoreboard Info",
			description = "Layout of info scoreboard"
		)
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
	
	@DocDescription(
			title = "Scoreboard Default",
			description = "Is scoreobard default enabled?"
		)
	public static boolean scoreboardDefaultEnabled = false;
	
	@DocDescription(
			title = "Scoreboard Default Title",
			description = "Default title"
		)
	public static String scoreboardDefaultTitle = "Default Title";
	
	@DocDescription(
			title = "Scoreboard Update Interval",
			description = "How long (in seconds) between scoreboard updates?"
		)
	public static int scoreboardDefaultUpdateIntervalSecs = 2;
	
	@DocDescription(
			title = "Scoreboard Default Prefixes",
			description = "Should we enable the scoreboard default prefixes?"
		)
	public static boolean scoreboardDefaultPrefixes = true;
	
	@DocDescription(
			title = "Scoreboard Default",
			description = "Layout of default scoreboard"
		)
	public static List<String> scoreboardDefault = Lists.newArrayList(
		"&6Your Faction",
		"{faction}",
		"&3Your Power",
		"{player-power}",
		"&aBalance",
		"${player-balance}"
	);
	
	@DocDescription(
			title = "Scoreboard Factionless Enabled",
			description = "Should we enable the Factionless scoreboard?"
		)
	public static boolean scoreboardFactionlessEnabled = false;
	
	@DocDescription(title = "Scoreboard Factionless", description = "Layout of Factionless scoreboard")
	public static List<String> scoreboardFactionless = Lists.newArrayList(
		"&6Factionless",
		"Join a faction!"
	);
	
	// -------------------------------------------------- //
	// TITLES
	// -------------------------------------------------- //
	@DocSection(name = "Titles")

	// show territory title on land change
	@DocDescription(title = "Territory Titles Show", description = "Should titles be shown for territory changes?")
	public static boolean territoryTitlesShow = true;
	
	@DocDescription(title = "Territory Titles Header", description = "Header format for titles")
	public static String territoryTitlesHeader = "{factions_location_relation_colour}{factions_location_faction_name}";
	
	@DocDescription(title = "Territory Titles Footer", description = "Footer format for titles")
	public static String territoryTitlesFooter = "{factions_location_faction_description}";
	
	@DocDescription(title = "Territory Titles Fade In Time", description = "The time (in ticks) that it will take for a title to fade in.")
	public static int territoryTitlesTimeFadeInTicks = 20;
	
	@DocDescription(title = "Territory Titles Stay Time", description = "The time (in ticks) that it will take for a title to stay for.")
	public static int territoryTitlesTimeStayTicks = 20;
	
	@DocDescription(title = "Territory Titles Fade Out Time", description = "The time (in ticks) that it will take for a title to fade out.")
	public static int territoryTitlesTimeFadeOutTicks = 20;
	
	@DocDescription(title = "Hide Footer for Wilderness", description = "Should the footer be hidden for wilderness?")
	public static boolean hideFooterForWilderness = false;
	
	@DocDescription(title = "Hide Footer for Warzone", description = "Should the footer be hidden for warzone?")
	public static boolean hideFooterForWarzone = false;
	
	@DocDescription(title = "Hide Footer for Safezone", description = "Should the footer be hidden for safezone?")
	public static boolean hideFooterForSafezone = false;

	// -------------------------------------------------- //
	// TERRITORY CHANGE
	// -------------------------------------------------- //
	@DocSection(name = "Territory Change")
	
	@DocDescription(
		title = "Territory Change Text Enabled",
		description = "If set to false players will never receive land change messages when they move between territories."
	)
	public static boolean territoryChangeText = true;
	
	// -------------------------------------------------- //
	// WARPS
	// -------------------------------------------------- //
	@DocSection(name = "Warps")

	@DocDescription(title = "Warps Max", description = "The max amount of wars per faction")
	public static int warpsMax = 5;
	
	@DocDescription(title = "Warp Cost", description = "The cost oer set/delete/use of warps")
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
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	private static transient Conf i = new Conf();

	public static void load() {
		Persist.get().loadOrSaveDefault(i, Conf.class, "conf");
	}

	public static void save() {
		Persist.get().save(i);
	}

	public enum Backend {
		JSON,
		;
	}
}

