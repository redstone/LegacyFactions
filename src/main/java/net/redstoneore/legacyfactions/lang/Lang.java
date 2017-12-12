package net.redstoneore.legacyfactions.lang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.commons.io.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import net.redstoneore.legacyfactions.Factions;

/**
 * This language enum is use for in built messages throughout LegacyFactions. If you are creating
 * an add on plugin your should use your own language file.
 */
public enum Lang {
	
	// -------------------------------------------------- //
	// META
	// -------------------------------------------------- //

	/**
	 * The author of this locale 
	 */
	_AUTHOR("LegacyFactions"),
	
	/**
	 * The language name
	 */
	_LANGUAGE("English (Australian English)"),
	
	/**
	 * Encoding type
	 */
	_ENCODING("UTF-8"),
	
	/**
	 * Locale value
	 */
	_LOCALE("en_AU"),
	
	/**
	 * What does this extend? Set to none if it doesn't extend any existing language file.
	 */
	_EXTENDS("none"),
	
	/**
	 * Does this lang require unicode?
	 */
	_REQUIRESUNICODE("false"),
	
	/**
	 * Is this locale the default
	 */
	_DEFAULT("true"),
	
	/**
	 * The state of this translation
	 */
	_STATE(LangState.COMPLETE),
	
	// -------------------------------------------------- //
	// COMMAND TRANSLATIONS
	// -------------------------------------------------- //
	
	COMMAND_ERRORS_PLAYERNOTFOUND("<b>No player \"<p><name><b>\" could be found."),
	COMMAND_ERRORS_PLAYERORFACTIONNOTFOUND("<b>The faction or player \"<p><name><b>\" could not be found."),
	COMMAND_ERRORS_ONLYFACTIONADMIN("<b>Only the faction admin can do that."),
	COMMAND_ERRORS_MODERATORSCANT("<b>Moderators can't control each other..."),
	COMMAND_ERRORS_COLEADERSCANT("<b>Coleaders can't control each other..."),
	COMMAND_ERRORS_NOTMODERATOR("<b>You must be a faction moderator to do that."),
	COMMAND_ERRORS_NOTMEMBER("<b>You are not a member of any faction."),
	COMMAND_ERRORS_NOTSAME("<name> <b>is not in the same faction as you."),
	COMMAND_ERRORS_YOUMUSTBE("<b>You <h>must be <therole><b> to <theaction>."),
	COMMAND_ERRORS_ONLYMODERATORSCAN("<b>Only faction moderators can <theaction>."),
	COMMAND_ERRORS_ONLYCOLEADERSCAN("<b>Only faction coleaders can <theaction>."),
	COMMAND_ERRORS_ONLYADMINSCAN("<b>Only faction admins can <theaction>."),
	
	COMMAND_ERRORS_FACTIONSLOCKED("<b>Factions was locked by an admin. Please try again later."),
	COMMAND_ERRORS_ECONOMYDISABLED("<b>Faction economy features are disabled on this server."),
	COMMAND_ERRORS_BANKSDISABLED("<b>The faction bank system is disabled on this server."),
	
	COMMAND_ADMIN_NOTMEMBER("%1$s<i> is not a member in your faction."),
	COMMAND_ADMIN_NOTADMIN("<b>You are not the faction admin."),
	COMMAND_ADMIN_TARGETSELF("<b>The target player musn't be yourself."),
	COMMAND_ADMIN_DEMOTES("<i>You have demoted %1$s<i> from the position of faction admin."),
	COMMAND_ADMIN_DEMOTED("<i>You have been demoted from the position of faction admin by %1$s<i>."),
	COMMAND_ADMIN_PROMOTES("<i>You have promoted %1$s<i> to the position of faction admin."),
	COMMAND_ADMIN_PROMOTED("%1$s<i> gave %2$s<i> the leadership of %3$s<i>."),
	COMMAND_ADMIN_DESCRIPTION("Hand over your admin rights"),

	COMMAND_AHOME_DESCRIPTION("Send a player to their f home no matter what."),
	COMMAND_AHOME_NOHOME("%1$s doesn't have an f home."),
	COMMAND_AHOME_SUCCESS("$1%s was sent to their f home."),
	COMMAND_AHOME_OFFLINE("%1$s is offline."),
	COMMAND_AHOME_TARGET("You were sent to your f home."),

	COMMAND_ANNOUNCE_DESCRIPTION("Announce a message to players in faction."),
	COMMAND_ANNOUNCE_TEMPLATE("<green><tag> <yellow>[<gray><player><yellow>]<white> <message>"),

	COMMAND_AUTOCLAIM_ENABLED("<i>Now auto-claiming land for <h>%1$s<i>."),
	COMMAND_AUTOCLAIM_DISABLED("<i>Auto-claiming of land disabled."),
	COMMAND_AUTOCLAIM_REQUIREDRANK("<b>You must be <h>%1$s<b> to claim land."),
	COMMAND_AUTOCLAIM_OTHERFACTION("<b>You can't claim land for <h>%1$s<b>."),
	COMMAND_AUTOCLAIM_DESCRIPTION("Auto-claim land as you walk around"),

	COMMAND_AUTOKICK_DESCRIPTION("Set the autokick time in days for inactive players."),
	COMMAND_AUTOKICK_DAYSINVALID("<b>Please enter valid value for days, that is also under <amount>"),
	COMMAND_AUTOKICK_BADRANK("<b>You must be at least <i><rank> <b>to set the autokick time."),
	COMMAND_AUTOKICK_NOTOTHERS("<b>You don't have permission to set the autokick for other factions."),
	COMMAND_AUTOKICK_SET("<i>You set the autokick time to <days> days."),
	COMMAND_AUTOKICK_CLEARED("<i>You cleared the autokick time for this faction. It now uses the server default."),

	COMMAND_AUTOHELP_HELPFOR("Help for command \""),
	
	COMMAND_BAN_DESCRIPTION("Bans a player from the faction preventing them from rejoining."),
	COMMAND_BAN_YOUBANNED("<i>You have banned <name> from your faction."),
	COMMAND_BAN_SOMEONEBANNED("<i><someone> has banned <name> from your faction."),
	COMMAND_BAN_YOUBANKICKED("<i>You have banned and kicked <name> from your faction."),
	COMMAND_BAN_SOMEONEBANKICKED("<i><someone> has banned and kicked <name> from your faction."),
	COMMAND_BAN_CANT("<b>You are <your-rank> and <name> is <their-rank> so you can't ban them."),
	COMMAND_BAN_CANTYOURSELF("<b>You can't ban yourself!"),
	COMMAND_BAN_TOBAN("to ban someone from the faction"),
	COMMAND_BAN_FORBAN("for banning someone from the faction"),
	COMMAND_BAN_NOTFOUND("<b>Unknown player <player>, so we can't ban them."),

	COMMAND_UNBAN_NOTFOUND("<b>Unknown player <player>, so we can't unban them."),
	COMMAND_UNBAN_NOTBANNED("<b><player> is not banned."),
	COMMAND_UNBAN_TOUNBAN("to unban someone from the faction"),
	COMMAND_UNBAN_FORUNBAN("for unbanning someone from the faction"),
	COMMAND_UNBAN_YOUUNBAN("<i>You have unbanned <name> from your faction."),
	COMMAND_UNBAN_SOMEONEUNBAN("<i><someone> has unbanned <name> from your faction."),
	
	COMMAND_BOOM_PEACEFULONLY("<b>This command is only usable by factions which are specifically designated as peaceful."),
	COMMAND_BOOM_TOTOGGLE("to toggle explosions"),
	COMMAND_BOOM_FORTOGGLE("for toggling explosions"),
	COMMAND_BOOM_ENABLED("%1$s<i> has %2$s explosions in your faction's territory."),
	COMMAND_BOOM_DESCRIPTION("Toggle explosions (peaceful factions only)"),

	COMMAND_BYPASS_ENABLE("<i>You have enabled admin bypass mode. You will be able to build or destroy anywhere."),
	COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
	COMMAND_BYPASS_DISABLE("<i>You have disabled admin bypass mode."),
	COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
	COMMAND_BYPASS_DESCRIPTION("Enable admin bypass mode"),

	COMMAND_CHAT_DISABLED("<b>The built in chat channels are disabled on this server."),
	COMMAND_CHAT_INVALIDMODE("<b>Unrecognised chat mode. <i>Please enter either 'a','f' or 'p'"),
	COMMAND_CHAT_DESCRIPTION("Change chat mode"),

	COMMAND_CHAT_MODE_PUBLIC("<i>Public chat mode."),
	COMMAND_CHAT_MODE_ALLIANCE("<i>Alliance only chat mode."),
	COMMAND_CHAT_MODE_TRUCE("<i>Truce only chat mode."),
	COMMAND_CHAT_MODE_FACTION("<i>Faction only chat mode."),

	COMMAND_CHATSPY_ENABLE("<i>You have enabled chat spying mode."),
	COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
	COMMAND_CHATSPY_DISABLE("<i>You have disabled chat spying mode."),
	COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
	COMMAND_CHATSPY_DESCRIPTION("Enable admin chat spy mode"),

	COMMAND_CLAIM_INVALIDRADIUS("<b>If you specify a radius, it must be at least 1."),
	COMMAND_CLAIM_DENIED("<b>You do not have permission to claim in a radius."),
	COMMAND_CLAIM_DESCRIPTION("Claim land from where you are standing"),
	COMMAND_CLAIM_RADIUSAMOUNT("<player><i> claimed <teal><amount> <i>chunks in a radius of <teal><radius><i> from <teal><chunk>"),
	COMMAND_CLAIM_RADIUSMAX("<b>The maximum claim radius is <radius>"),

	COMMAND_CLAIMLINE_INVALIDRADIUS("<b>If you specify a distance, it must be at least 1."),
	COMMAND_CLAIMLINE_DENIED("<b>You do not have permission to claim in a line."),
	COMMAND_CLAIMLINE_DESCRIPTION("Claim land in a straight line."),
	COMMAND_CLAIMLINE_ABOVEMAX("<b>The maximum limit for claim line is <b>%s<b>."),
	COMMAND_CLAIMLINE_NOTVALID("%s<b> is not a cardinal direction. You may use <h>north<b>, <h>east<b>, <h>south <b>or <h>west<b>."),

    COMMAND_COLEADER_CANDIDATES("Players you can promote: "),
    COMMAND_COLEADER_CLICKTOPROMOTE("Click to promote "),
    COMMAND_COLEADER_NOTMEMBER("%1$s<b> is not a member in your faction."),
    COMMAND_COLEADER_NOTADMIN("<b>You are not the faction admin."),
    COMMAND_COLEADER_SELF("<b>The target player musn't be yourself."),
    COMMAND_COLEADER_TARGETISADMIN("<b>The target player is a faction admin. Demote them first."),
    COMMAND_COLEADER_REVOKES("<i>You have removed coleader status from %1$s<i>."),
    COMMAND_COLEADER_REVOKED("%1$s<i> is no longer coleader in your faction."),
    COMMAND_COLEADER_PROMOTES("%1$s<i> was promoted to coleader in your faction."),
    COMMAND_COLEADER_PROMOTED("<i>You have promoted %1$s<i> to coleader."),
    COMMAND_COLEADER_DESCRIPTION("Give or revoke coleader rights"),

	COMMAND_CONFIG_NOEXIST("<b>No configuration setting \"<h>%1$s<b>\" exists."),
	COMMAND_CONFIG_SET_TRUE("\" option set to true (enabled)."),
	COMMAND_CONFIG_SET_FALSE("\" option set to false (disabled)."),
	COMMAND_CONFIG_OPTIONSET("\" option set to "),
	COMMAND_CONFIG_COLOURSET("\" color option set to \""),
	COMMAND_CONFIG_INTREQUIRED("Cannot set \"%1$s\": An integer (whole number) value required."),
	COMMAND_CONFIG_LONGREQUIRED("Cannot set \"%1$s\": A long integer (whole number) value required."),
	COMMAND_CONFIG_DOUBLEREQUIRED("Cannot set \"%1$s\": A double (numeric) value required."),
	COMMAND_CONFIG_FLOATREQUIRED("Cannot set \"%1$s\": A float (numeric) value required."),
	COMMAND_CONFIG_INVALID_COLOUR("Cannot set \"%1$s\": \"%2$s\" is not a valid color."),
	COMMAND_CONFIG_INVALID_COLLECTION("\"%1$s\" is not a data collection type which can be modified with this command."),
	COMMAND_CONFIG_INVALID_MATERIAL("Cannot change \"%1$s\" set: \"%2$s\" is not a valid material."),
	COMMAND_CONFIG_INVALID_TYPESET("\"%1$s\" is not a data type set which can be modified with this command."),
	COMMAND_CONFIG_MATERIAL_ADDED("\"%1$s\" set: Material \"%2$s\" added."),
	COMMAND_CONFIG_MATERIAL_REMOVED("\"%1$s\" set: Material \"%2$s\" removed."),
	COMMAND_CONFIG_SET_ADDED("\"%1$s\" set: \"%2$s\" added."),
	COMMAND_CONFIG_SET_REMOVED("\"%1$s\" set: \"%2$s\" removed."),
	COMMAND_CONFIG_LOG(" (Command was run by %1$s.)"),
	COMMAND_CONFIG_ERROR_SETTING("Error setting configuration setting \"%1$s\" to \"%2$s\"."),
	COMMAND_CONFIG_ERROR_MATCHING("Configuration setting \"%1$s\" couldn't be matched, though it should be... please report this error."),
	COMMAND_CONFIG_ERROR_TYPE("'%1$s' is of type '%2$s', which cannot be modified with this command."),
	COMMAND_CONFIG_DESCRIPTION("Change a conf.json setting"),

	COMMAND_CONVERT_BACKEND_RUNNING("Already running that backend."),
	COMMAND_CONVERT_BACKEND_INVALID("Invalid backend"),
	COMMAND_CONVERT_BACKEND_DBCREDENTIALS("To convert to <type> you must provide database credentials."),
	COMMAND_CONVERT_DESCRIPTION("Convert the plugin backend"),
	
	COMMAND_CREATE_MUSTLEAVE("<b>You must leave your current faction first."),
	COMMAND_CREATE_INUSE("<b>That tag is already in use."),
	COMMAND_CREATE_TOCREATE("to create a new faction"),
	COMMAND_CREATE_FORCREATE("for creating a new faction"),
	COMMAND_CREATE_ERROR("<b>There was an internal error while trying to create your faction. Please try again."),
	COMMAND_CREATE_CREATED("%1$s<i> created a new faction %2$s"),
	COMMAND_CREATE_YOUSHOULD("<i>You should now: %1$s"),
	COMMAND_CREATE_CREATEDLOG(" created a new faction: "),
	COMMAND_CREATE_DESCRIPTION("Create a new faction"),

	COMMAND_DEBUG_DESCRIPTION("<b>Returns debug information about the server."),
	
	COMMAND_DEINVITE_CANDEINVITE("Players you can deinvite: "),
	COMMAND_DEINVITE_CLICKTODEINVITE("Click to revoke invite for %1$s"),
	COMMAND_DEINVITE_ALREADYMEMBER("%1$s<i> is already a member of %2$s"),
	COMMAND_DEINVITE_MIGHTWANT("<i>You might want to: %1$s"),
	COMMAND_DEINVITE_REVOKED("%1$s<i> revoked your invitation to <h>%2$s<i>."),
	COMMAND_DEINVITE_REVOKES("%1$s<i> revoked %2$s's<i> invitation."),
	COMMAND_DEINVITE_DESCRIPTION("Remove a pending invitation"),

	COMMAND_DELFWARP_DELETED("<i>Deleted warp <a>%1$s"),
	COMMAND_DELFWARP_INVALID("<i>Couldn't find warp <a>%1$s"),
	COMMAND_DELFWARP_TODELETE("to delete warp"),
	COMMAND_DELFWARP_FORDELETE("for deleting warp"),
	COMMAND_DELFWARP_DESCRIPTION("Delete a faction warp"),

	COMMAND_DESCRIPTION_CHANGES("You have changed the description for <h>%1$s<i> to:"),
	COMMAND_DESCRIPTION_CHANGED("<i>The faction %1$s<i> changed their description to:"),
	COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
	COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
	COMMAND_DESCRIPTION_TOOLONG("<b>The maximum length of your faction description is <max-length>, your description was <length>"),
	COMMAND_DESCRIPTION_DESCRIPTION("Change the faction description"),

	COMMAND_DISBAND_IMMUTABLE("<i>You cannot disband the Wilderness, SafeZone, or WarZone."),
	COMMAND_DISBAND_MARKEDPERMANENT("<i>This faction is designated as permanent, so you cannot disband it."),
	COMMAND_DISBAND_BROADCAST_YOURS("<h>%1$s<i> disbanded your faction."),
	COMMAND_DISBAND_BROADCAST_YOURSYOU("<h>You<i> disbanded your faction."),
	COMMAND_DISBAND_BROADCAST_NOTYOURS("<h>%1$s<i> disbanded the faction %2$s."),
	COMMAND_DISBAND_HOLDINGS("<i>You have been given the disbanded faction's bank, totaling %1$s."),
	COMMAND_DISBAND_DESCRIPTION("Disband a faction"),
	
	COMMAND_EMBLEM_LENGTHUNDERMIN("<b>Your emblem must be at least <minimum> characters."),
	COMMAND_EMBLEM_LENGTHOVERMAX("<b>Your emblem must be under <maximum> characters."),
	COMMAND_EMBLEM_CANTOTHERS("<b>You don't have permission to set the emblem of other factions."),
	COMMAND_EMBLEM_MINROLE("<b>You must be at least <role> to set emblems."),
	COMMAND_EMBLEM_TAKEN("<b>That emblem is already taken."),
	COMMAND_EMBLEM_SET("<i>You set your faction emblem to <h><emblem><i>."),
	COMMAND_EMBLEM_DESCRIPTION("Set your faction emblem"),
	
	COMMAND_FLAG_DESCRIPTION("Manage flags for a faction."),
	
	COMMAND_FLAGLIST_DESCRIPTION("List flags for a faction."),
	
	COMMAND_FLAGSET_DESCRIPTION("Set flags for a faction."),
	COMMAND_FLAGSET_INVALID("<h><flag><b> is not a valid flag."),
	COMMAND_FLAGSET_NOTYOURS("<b>You can't set flags for this faction."),
	COMMAND_FLAGSET_BADRANK("<b>You must be at least <h><role><b> to manage flags for your faction."),
	COMMAND_FLAGSET_SET("<h><flag><i> was set to <h><value>."),

	COMMAND_FWARP_CLICKTOWARP("Click to warp!"),
	COMMAND_FWARP_COMMANDFORMAT("<i>/f warp <warpname> [password]"),
	COMMAND_FWARP_WARPED("<i>Warped to <a>%1$s"),
	COMMAND_FWARP_INVALID("<i>Couldn't find warp <a>%1$s"),
	COMMAND_FWARP_INVALID_PASSWORD("<i>This warp requires a valid password."),
	COMMAND_FWARP_TOWARP("to warp"),
	COMMAND_FWARP_FORWARPING("for warping"),
	COMMAND_FWARP_WARPS("Warps: "),
	COMMAND_FWARP_DESCRIPTION("Teleport to a faction warp"),

	COMMAND_HELP_404("<b>This page does not exist"),
	COMMAND_HELP_NEXTCREATE("<i>Learn how to create a faction on the next page."),
	COMMAND_HELP_INVITATIONS("command.help.invitations", "<i>You might want to close it and use invitations:"),
	COMMAND_HELP_HOME("<i>And don't forget to set your home:"),
	COMMAND_HELP_BANK_1("<i>Your faction has a bank which is used to pay for certain"),
	COMMAND_HELP_BANK_2("<i>things, so it will need to have money deposited into it."),
	COMMAND_HELP_BANK_3("<i>To learn more, use the money command."),
	COMMAND_HELP_PLAYERTITLES("<i>Player titles are just for fun. No rules connected to them."),
	COMMAND_HELP_OWNERSHIP_1("<i>Claimed land with ownership set is further protected so"),
	COMMAND_HELP_OWNERSHIP_2("<i>that only the owner(s), faction admin, and possibly the"),
	COMMAND_HELP_OWNERSHIP_3("<i>faction moderators have full access."),
	COMMAND_HELP_RELATIONS_1("<i>Set the relation you WISH to have with another faction."),
	COMMAND_HELP_RELATIONS_2("<i>Your default relation with other factions will be neutral."),
	COMMAND_HELP_RELATIONS_3("<i>If BOTH factions choose \"ally\" you will be allies."),
	COMMAND_HELP_RELATIONS_4("<i>If ONE faction chooses \"enemy\" you will be enemies."),
	COMMAND_HELP_RELATIONS_5("<i>You can never hurt members or allies."),
	COMMAND_HELP_RELATIONS_6("<i>You can not hurt neutrals in their own territory."),
	COMMAND_HELP_RELATIONS_7("<i>You can always hurt enemies and players without faction."),
	COMMAND_HELP_RELATIONS_8(""),
	COMMAND_HELP_RELATIONS_9("<i>Damage from enemies is reduced in your own territory."),
	COMMAND_HELP_RELATIONS_10("<i>When you die you lose power. It is restored over time."),
	COMMAND_HELP_RELATIONS_11("<i>The power of a faction is the sum of all member power."),
	COMMAND_HELP_RELATIONS_12("<i>The power of a faction determines how much land it can hold."),
	COMMAND_HELP_RELATIONS_13("<i>You can claim land from factions with too little power."),
	COMMAND_HELP_PERMISSIONS_1("<i>Only faction members can build and destroy in their own"),
	COMMAND_HELP_PERMISSIONS_2("<i>territory. Usage of the following items is also restricted:"),
	COMMAND_HELP_PERMISSIONS_3("<i>Door, Chest, Furnace, Dispenser, Diode."),
	COMMAND_HELP_PERMISSIONS_4(""),
	COMMAND_HELP_PERMISSIONS_5("<i>Make sure to put pressure plates in front of doors for your"),
	COMMAND_HELP_PERMISSIONS_6("<i>guest visitors. Otherwise they can't get through. You can"),
	COMMAND_HELP_PERMISSIONS_7("<i>also use this to create member only areas."),
	COMMAND_HELP_PERMISSIONS_8("<i>As dispensers are protected, you can create traps without"),
	COMMAND_HELP_PERMISSIONS_9("<i>worrying about those arrows getting stolen."),
	COMMAND_HELP_ADMIN_1("<c>/f claim safezone <i>claim land for the Safe Zone"),
	COMMAND_HELP_ADMIN_2("<c>/f claim warzone <i>claim land for the War Zone"),
	COMMAND_HELP_ADMIN_3("<c>/f autoclaim [safezone|warzone] <i>take a guess"),
	COMMAND_HELP_MOAR_1("Finally some commands for the server admins:"),
	COMMAND_HELP_MOAR_2("<i>More commands for server admins:"),
	COMMAND_HELP_MOAR_3("<i>Even more commands for server admins:"),
	COMMAND_HELP_DESCRIPTION("Display a help page"),

	COMMAND_HELP_PAGES_TITLE("Factions Help (<current>/<total>)"),
	COMMAND_HELP_PAGES_NOPREV("<gray>No previous page."),
	COMMAND_HELP_PAGES_NONEXT("<gray>No next page."),
	COMMAND_HELP_PAGES_GOTO("<aqua>Go to page <number>."),
	COMMAND_HELP_PAGES_BTN_LEFT("[<]"),
	COMMAND_HELP_PAGES_BTN_RIGHT("[>]"),
	
	COMMAND_HOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
	COMMAND_HOME_FACTIONORPLAYER("<b>Please specify if it is a faction or player."),
	COMMAND_HOME_TELEPORTDISABLED("<b>Sorry, the ability to teleport to Faction homes is disabled on this server."),
	COMMAND_HOME_NOHOME("<b>Your faction does not have a home. "),
	COMMAND_HOME_INENEMY("<b>You cannot teleport to your faction home while in the territory of an enemy faction."),
	COMMAND_HOME_WRONGWORLD("<b>You cannot teleport to your faction home while in a different world."),
	COMMAND_HOME_NEARENEMY("<b>You cannot teleport to your faction home while an enemy is within <blocks> blocks of you."),
	COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
	COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
	COMMAND_HOME_DESCRIPTION("Teleport to the faction home"),

	COMMAND_INVITE_TOINVITE("to invite someone"),
	COMMAND_INVITE_FORINVITE("for inviting someone"),
	COMMAND_INVITE_CLICKTOJOIN("Click to join!"),
	COMMAND_INVITE_INVITEDYOU(" has invited you to join "),
	COMMAND_INVITE_INVITED("%1$s<i> invited %2$s<i> to your faction."),
	COMMAND_INVITE_ALREADYMEMBER("%1$s<i> is already a member of %2$s"),
	COMMAND_INVITE_DESCRIPTION("Invite a player to your faction"),

	COMMAND_JOIN_CANNOTFORCE("<b>You do not have permission to move other players into a faction."),
	COMMAND_JOIN_SYSTEMFACTION("<b>Players may only join normal factions. This is a system faction."),
	COMMAND_JOIN_ALREADYMEMBER_YOU("<b><player> are already a member of <faction>"),
	COMMAND_JOIN_ALREADYMEMBER_SOMEONE("<b><player> is already a member of <faction>"),
	COMMAND_JOIN_ATLIMIT(" <b>!<white> The faction %1$s is at the limit of %2$d members, so %3$s cannot currently join."),
	COMMAND_JOIN_INOTHERFACTION_YOU("<b><player> must leave your current faction first."),
	COMMAND_JOIN_INOTHERFACTION_SOMEONE("<b><player> must leave their current faction first."),
	COMMAND_JOIN_NEGATIVEPOWER("<b>%1$s cannot join a faction with a negative power level."),
	COMMAND_JOIN_REQUIRESINVITATION("<i>This faction requires invitation."),
	COMMAND_JOIN_ATTEMPTEDJOIN("%1$s<i> tried to join your faction."),
	COMMAND_JOIN_TOJOIN("to join a faction"),
	COMMAND_JOIN_FORJOIN("for joining a faction"),
	COMMAND_JOIN_SUCCESS("<i>%1$s successfully joined %2$s."),
	COMMAND_JOIN_MOVED("<i>%1$s moved you into the faction %2$s."),
	COMMAND_JOIN_JOINED("<i>%1$s joined your faction."),
	COMMAND_JOIN_JOINEDLOG("%1$s joined the faction %2$s."),
	COMMAND_JOIN_MOVEDLOG("%1$s moved the player %2$s into the faction %3$s."),
	COMMAND_JOIN_DESCRIPTION("Join a faction"),
	COMMAND_JOIN_NOT_PLAYER("<name><b> is not a player."),
	COMMAND_JOIN_ISBANNED("<b><name> is banned from this faction, they must be unbanned first."),
	COMMAND_JOIN_YOUBANNED("<b>You are banned from this faction and can not join it."),

	COMMAND_KICK_CANDIDATES("Players you can kick: "),
	COMMAND_KICK_CLICKTOKICK("Click to kick "),
	COMMAND_KICK_SELF("<b>You cannot kick yourself."),
	COMMAND_KICK_NONE("That player is not in a faction."),
	COMMAND_KICK_NOTMEMBER("%1$s<b> is not a member of %2$s"),
	COMMAND_KICK_INSUFFICIENTRANK("<b>Your rank is too low to kick this player."),
	COMMAND_KICK_NEGATIVEPOWER("<b>You cannot kick that member until their power is positive."),
	COMMAND_KICK_TOKICK("to kick someone from the faction"),
	COMMAND_KICK_FORKICK("for kicking someone from the faction"),
	COMMAND_KICK_FACTION("%1$s<i> kicked %2$s<i> from the faction! :O"), //message given to faction members
	COMMAND_KICK_KICKS("<i>You kicked %1$s<i> from the faction %2$s<i>!"), //kicker perspective
	COMMAND_KICK_KICKED("%1$s<i> kicked you from %2$s<i>! :O"), //kicked player perspective
	COMMAND_KICK_DESCRIPTION("Kick a player from the faction"),

	COMMAND_LIST_FACTIONLIST("Faction List "),
	COMMAND_LIST_TOLIST("to list the factions"),
	COMMAND_LIST_FORLIST("for listing the factions"),
	COMMAND_LIST_ONLINEFACTIONLESS("Online factionless: "),
	COMMAND_LIST_DESCRIPTION("See a list of the factions"),

	COMMAND_LOCK_LOCKED("<i>Factions is now locked"),
	COMMAND_LOCK_UNLOCKED("<i>Factions in now unlocked"),
	COMMAND_LOCK_DESCRIPTION("Lock all write stuff. Apparently."),

	COMMAND_LOGINS_TOGGLE("<i>Set login / logout notifications for Faction members to: <a>%s"),
	COMMAND_LOGINS_DESCRIPTION("Toggle(?) login / logout notifications for Faction members"),

	COMMAND_MAP_TOSHOW("to show the map"),
	COMMAND_MAP_FORSHOW("for showing the map"),
	COMMAND_MAP_UPDATE_ENABLED("<i>Map auto update <green>ENABLED."),
	COMMAND_MAP_UPDATE_DISABLED("<i>Map auto update <red>DISABLED."),
	COMMAND_MAP_DESCRIPTION("Show the territory map, and set optional auto update"),

	COMMAND_MOD_CANDIDATES("Players you can promote: "),
	COMMAND_MOD_CLICKTOPROMOTE("Click to promote "),
	COMMAND_MOD_NOTMEMBER("%1$s<b> is not a member in your faction."),
	COMMAND_MOD_NOTADMIN("<b>You are not the faction admin."),
	COMMAND_MOD_SELF("<b>The target player musn't be yourself."),
	COMMAND_MOD_TARGETISADMIN("<b>The target player is a faction admin. Demote them first."),
	COMMAND_MOD_REVOKES("<i>You have removed moderator status from %1$s<i>."),
	COMMAND_MOD_REVOKED("%1$s<i> is no longer moderator in your faction."),
	COMMAND_MOD_PROMOTES("%1$s<i> was promoted to moderator in your faction."),
	COMMAND_MOD_PROMOTED("<i>You have promoted %1$s<i> to moderator."),
	COMMAND_MOD_DESCRIPTION("Give or revoke moderator rights"),
	
	COMMAND_MODIFYPOWER_ADDED("<i>Added <a>%1$f <i>power to <a>%2$s. <i>New total rounded power: <a>%3$d"),
	COMMAND_MODIFYPOWER_DESCRIPTION("Modify the power of a faction/player"),

	COMMAND_MONEY_LONG("<i>The faction money commands."),
	COMMAND_MONEY_DESCRIPTION("Faction money commands"),

	COMMAND_MONEYBALANCE_SHORT("show faction balance"),
	COMMAND_MONEYBALANCE_DESCRIPTION("Show your factions current money balance"),

	COMMAND_MONEYDEPOSIT_DESCRIPTION("Deposit money"),
	COMMAND_MONEYDEPOSIT_DEPOSITED("%1$s deposited %2$s in the faction bank: %3$s"),

	COMMAND_MONEYTRANSFERFF_DESCRIPTION("Transfer f -> f"),
	COMMAND_MONEYTRANSFERFF_TRANSFER("%1$s transferred %2$s from the faction \"%3$s\" to the faction \"%4$s\""),

	COMMAND_MONEYTRANSFERFP_DESCRIPTION("Transfer f -> p"),
	COMMAND_MONEYTRANSFERFP_TRANSFER("%1$s transferred %2$s from the faction \"%3$s\" to the player \"%4$s\""),

	COMMAND_MONEYTRANSFERPF_DESCRIPTION("Transfer p -> f"),
	COMMAND_MONEYTRANSFERPF_TRANSFER("%1$s transferred %2$s from the player \"%3$s\" to the faction \"%4$s\""),

	COMMAND_MONEYWITHDRAW_DESCRIPTION("Withdraw money"),
	COMMAND_MONEYWITHDRAW_WITHDRAW("%1$s withdrew %2$s from the faction bank: %3$s"),

	COMMAND_OPEN_TOOPEN("to open or close the faction"),
	COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
	COMMAND_OPEN_OPEN("open"),
	COMMAND_OPEN_CLOSED("closed"),
	COMMAND_OPEN_CHANGES("%1$s<i> changed the faction to <h>%2$s<i>."),
	COMMAND_OPEN_CHANGED("<i>The faction %1$s<i> is now %2$s"),
	COMMAND_OPEN_DESCRIPTION("Switch if invitation is required to join"),

	COMMAND_OWNER_DISABLED("<b>Sorry, but owned areas are disabled on this server."),
	COMMAND_OWNER_LIMIT("<b>Sorry, but you have reached the server's <h>limit of %1$d <b>owned areas per faction."),
	COMMAND_OWNER_WRONGFACTION("<b>This land is not claimed by your faction, so you can't set ownership of it."),
	COMMAND_OWNER_NOTCLAIMED("<b>This land is not claimed by a faction. Ownership is not possible."),
	COMMAND_OWNER_NOTMEMBER("%1$s<i> is not a member of this faction."),
	COMMAND_OWNER_CLEARED("<i>You have cleared ownership for this claimed area."),
	COMMAND_OWNER_REMOVED("<i>You have removed ownership of this claimed land from %1$s<i>."),
	COMMAND_OWNER_TOSET("to set ownership of claimed land"),
	COMMAND_OWNER_FORSET("for setting ownership of claimed land"),
	COMMAND_OWNER_ADDED("<i>You have added %1$s<i> to the owner list for this claimed land."),
	COMMAND_OWNER_DESCRIPTION("Set ownership of claimed land"),

	COMMAND_OWNERLIST_DISABLED("<b>Sorry, but owned areas are disabled on this server."),//dup->
	COMMAND_OWNERLIST_WRONGFACTION("<b>This land is not claimed by your faction."),//eq
	COMMAND_OWNERLIST_NOTCLAIMED("<i>This land is not claimed by any faction, thus no owners."),//eq
	COMMAND_OWNERLIST_NONE("<i>No owners are set here; everyone in the faction has access."),
	COMMAND_OWNERLIST_OWNERS("<i>Current owner(s) of this land: %1$s"),
	COMMAND_OWNERLIST_DESCRIPTION("List owner(s) of this claimed land"),

	COMMAND_PEACEFUL_DESCRIPTION("Set a faction to peaceful"),
	COMMAND_PEACEFUL_YOURS("%1$s has %2$s your faction"),
	COMMAND_PEACEFUL_OTHER("%s<i> has %s the faction '%s<i>'."),
	COMMAND_PEACEFUL_GRANT("granted peaceful status to"),
	COMMAND_PEACEFUL_REVOKE("removed peaceful status from"),

	COMMAND_PERMANENT_DESCRIPTION("Toggles a faction's permanence"), 
	COMMAND_PERMANENT_GRANT("added permanent status to"),
	COMMAND_PERMANENT_REVOKE("removed permanent status from"),
	COMMAND_PERMANENT_YOURS("%1$s has %2$s your faction"),
	COMMAND_PERMANENT_OTHER("%s<i> has %s the faction '%s<i>'."),

	COMMAND_PERMANENTPOWER_DESCRIPTION("Toggle faction power permanence"),
	COMMAND_PERMANENTPOWER_GRANT("added permanentpower status to"),
	COMMAND_PERMANENTPOWER_REVOKE("removed permanentpower status from"),
	COMMAND_PERMANENTPOWER_SUCCESS("<i>You %s <h>%s<i>."),
	COMMAND_PERMANENTPOWER_FACTION("%s<i> %s your faction"),

	COMMAND_POWER_TOSHOW("to show player power info"),
	COMMAND_POWER_FORSHOW("for showing player power info"),
	COMMAND_POWER_POWER("%1$s<a> - Power / Maxpower: <i>%2$d / %3$d %4$s"),
	COMMAND_POWER_BONUS(" (bonus: "),
	COMMAND_POWER_PENALTY(" (penalty: "),
	COMMAND_POWER_DESCRIPTION("Show player power info"),

	COMMAND_POWERBOOST_HELP_1("<b>You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
	COMMAND_POWERBOOST_HELP_2("<b>ex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
	COMMAND_POWERBOOST_INVALIDNUM("<b>You must specify a valid numeric value for the power bonus/penalty amount."),
	COMMAND_POWERBOOST_PLAYER("Player \"%1$s\""),
	COMMAND_POWERBOOST_FACTION("Faction \"%1$s\""),
	COMMAND_POWERBOOST_BOOST("<i>%1$s now has a power bonus/penalty of %2$d to min and max power levels."),
	COMMAND_POWERBOOST_BOOSTLOG("%1$s has set the power bonus/penalty for %2$s to %3$d."),
	COMMAND_POWERBOOST_DESCRIPTION("Apply permanent power bonus/penalty to specified player or faction"),

	COMMAND_RELATIONS_INVALID_TARGET("<b>Invalid target faction."),
	COMMAND_RELATIONS_INVALID_NOTNORMAL("<b>Nope! You can't."),
	COMMAND_RELATIONS_INVALID_SELF("<b>Nope! You can't declare a relation to yourself :)"),
	COMMAND_RELATIONS_INVALID_ALREADYINRELATIONSHIP("<b>You already have that relation wish set with %1$s."),
	COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
	COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
	COMMAND_RELATIONS_MUTUAL("<i>Your faction is now %1$s<i> to %2$s"),
	COMMAND_RELATIONS_PEACEFUL("<i>This will have no effect while your faction is peaceful."),
	COMMAND_RELATIONS_PEACEFULOTHER("<i>This will have no effect while their faction is peaceful."),
	COMMAND_RELATIONS_DESCRIPTION("Set relation wish to another faction"),
	COMMAND_RELATIONS_EXCEEDS_ME("<i>Failed to set relation wish. You can only have %1$s %2$s."),
	COMMAND_RELATIONS_EXCEEDS_THEY("<i>Failed to set relation wish. They can only have %1$s %2$s."),

	COMMAND_RELATIONS_PROPOSAL_1("%1$s<i> wishes to be your %2$s"),
	COMMAND_RELATIONS_PROPOSAL_2("<i>Type <c>/%1$s %2$s %3$s<i> to accept."),
	COMMAND_RELATIONS_PROPOSAL_SENT("%1$s<i> were informed that you wish to be %2$s"),

	COMMAND_RELOAD_TIME("<i>Reloaded <h>all configuration files <i>from disk, took <h>%1$d ms<i>."),
	COMMAND_RELOAD_DESCRIPTION("Reload data file(s) from disk"),

	COMMAND_SAFEUNCLAIMALL_DESCRIPTION("Unclaim all safezone land"),
	COMMAND_SAFEUNCLAIMALL_UNCLAIMED("<i>You unclaimed ALL safe zone land."),
	COMMAND_SAFEUNCLAIMALL_UNCLAIMEDIN("<i>You unclaimed ALL safe zone land in <world>."),
	COMMAND_SAFEUNCLAIMALL_LOG("<who> unclaimed all safe zones."),
	COMMAND_SAFEUNCLAIMALL_LOGWORLD("<who> unclaimed all safe zones in <world>."),

	COMMAND_SAVEALL_SUCCESS("<i>Factions saved to disk!"),
	COMMAND_SAVEALL_DESCRIPTION("Save all data to disk"),

	COMMAND_SCOREBOARD_DESCRIPTION("Scoreboardy things"),

	COMMAND_SETFWARP_NOTCLAIMED("<i>You can only set warps in your faction territory."),
	COMMAND_SETFWARP_LIMIT("<i>Your Faction already has the max amount of warps set <a>(%1$d)."),
	COMMAND_SETFWARP_SET("<i>Set warp <a>%1$s <i>to your location."),
	COMMAND_SETFWARP_TOSET("to set warp"),
	COMMAND_SETFWARP_FORSET("for setting warp"),
	COMMAND_SETFWARP_DESCRIPTION("Set a faction warp"),
	COMMAND_SETFWARP_NOPASSWORD("<b>Sorry, you don't have permission to create warps with passwords."),

	COMMAND_SETHOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
	COMMAND_SETHOME_NOTCLAIMED("<b>Sorry, your faction home can only be set inside your own claimed territory."),
	COMMAND_SETHOME_TOSET("to set the faction home"),
	COMMAND_SETHOME_FORSET("for setting the faction home"),
	COMMAND_SETHOME_SET("%1$s<i> set the home for your faction. You can now use:"),
	COMMAND_SETHOME_SETOTHER("<b>You have set the home for the %1$s<i> faction."),
	COMMAND_SETHOME_DESCRIPTION("Set the faction home"),

	COMMAND_SETMAXVAULTS_DESCRIPTION("Set max vaults for a Faction."),
	COMMAND_SETMAXVAULTS_SUCCESS("&aSet max vaults for &e%s &ato &b%d"),

	COMMAND_VAULT_DESCRIPTION("/f vault <number> to open one of your Faction's vaults."),
	COMMAND_VAULT_TOOHIGH("&cYou tried to open vault %d but your Faction only has %d vaults."),

	COMMAND_SHOW_NOFACTION_SELF("You are not in a faction"),
	COMMAND_SHOW_NOFACTION_OTHER("That's not a faction"),
	COMMAND_SHOW_TOSHOW("to show faction information"),
	COMMAND_SHOW_FORSHOW("for showing faction information"),
	COMMAND_SHOW_DESCRIPTION("<a>Description: <i>%1$s"),
	COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
	COMMAND_SHOW_PERMANENT("<a>This faction is permanent, remaining even with no members."),
	COMMAND_SHOW_JOINING("<a>Joining: <i>%1$s "),
	COMMAND_SHOW_INVITATION("invitation is required"),
	COMMAND_SHOW_UNINVITED("no invitation is needed"),
	COMMAND_SHOW_NOHOME("n/a"),
	COMMAND_SHOW_POWER("<a>Land / Power / Maxpower: <i> %1$d/%2$d/%3$d %4$s."),
	COMMAND_SHOW_BONUS(" (bonus: "),
	COMMAND_SHOW_PENALTY(" (penalty: "),
	COMMAND_SHOW_DEPRECIATED("(%1$s depreciated)"), //This is spelled correctly.
	COMMAND_SHOW_LANDVALUE("<a>Total land value: <i>%1$s %2$s"),
	COMMAND_SHOW_BANKCONTAINS("<a>Bank contains: <i>%1$s"),
	COMMAND_SHOW_ALLIES("Allies: "),
	COMMAND_SHOW_ENEMIES("Enemies: "),
	COMMAND_SHOW_MEMBERSONLINE("Members online: "),
	COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
	COMMAND_SHOW_COMMANDDESCRIPTION("Show faction information"),
	COMMAND_SHOW_DEATHS_TIL_RAIDABLE("<i>DTR: %1$d"),
	COMMAND_SHOW_EXEMPT("<b>This faction is exempt and cannot be seen."),

	COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
	COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %1$s"),
	COMMAND_SHOWINVITES_DESCRIPTION("Show pending faction invites"),

	COMMAND_STATUS_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
	COMMAND_STATUS_ONLINE("Online"),
	COMMAND_STATUS_AGOSUFFIX(" ago."),
	COMMAND_STATUS_DESCRIPTION("Show the status of a player"),

	COMMAND_STUCK_TIMEFORMAT("m 'minutes', s 'seconds.'"),
	COMMAND_STUCK_CANCELLED("<a>Teleport cancelled because you were damaged"),
	COMMAND_STUCK_OUTSIDE("<a>Teleport cancelled because you left <i>%1$d <a>block radius"),
	COMMAND_STUCK_EXISTS("<a>You are already teleporting, you must wait <i>%1$s"),
	COMMAND_STUCK_START("<a>Teleport will commence in <i>%s<a>. Don't take or deal damage. "),
	COMMAND_STUCK_TELEPORT("<a>Teleported safely to %1$d, %2$d, %3$d."),
	COMMAND_STUCK_TOSTUCK("to safely teleport %1$s out"),
	COMMAND_STUCK_FORSTUCK("for %1$s initiating a safe teleport out"),
	COMMAND_STUCK_DESCRIPTION("Safely teleports you out of enemy faction"),

	COMMAND_TAG_TAKEN("<b>That tag is already taken"),
	COMMAND_TAG_TOCHANGE("to change the faction tag"),
	COMMAND_TAG_FORCHANGE("for changing the faction tag"),
	COMMAND_TAG_FACTION("%1$s<i> changed your faction tag to %2$s"),
	COMMAND_TAG_CHANGED("<i>The faction %1$s<i> changed their name to %2$s."),
	COMMAND_TAG_DESCRIPTION("Change the faction tag"),

	COMMAND_TITLE_TOCHANGE("to change a players title"),
	COMMAND_TITLE_FORCHANGE("for changing a players title"),
	COMMAND_TITLE_CHANGED("%1$s<i> changed a title: %2$s"),
	COMMAND_TITLE_DESCRIPTION("Set or remove a players title"),

	COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION("Toggles whether or not you will see alliance chat"),
	COMMAND_TOGGLEALLIANCECHAT_IGNORE("Alliance chat is now ignored"),
	COMMAND_TOGGLEALLIANCECHAT_UNIGNORE("Alliance chat is no longer ignored"),

	COMMAND_TOGGLESB_DISABLED("You can't toggle scoreboards while they are disabled."),

	COMMAND_TOP_DESCRIPTION("Sort Factions to see the top of some criteria."),
	COMMAND_TOP_TOP("Top Factions by %s. Page %d/%d"),
	COMMAND_TOP_LINE("%d. &6%s: &c%s"), // Rank. Faction: Value
	COMMAND_TOP_INVALID("Could not sort by %s. Try balance, online, members, power or land."),
	COMMAND_TOP_INVALID_NONE("Provide a sort criteria. Try:"),
	COMMAND_TOP_TOOLTIP_MONEY("Top factions by money"),
	COMMAND_TOP_TOOLTIP_MEMBERS("Top factions by members"),
	COMMAND_TOP_TOOLTIP_ONLINE("Top factions by online"),
	COMMAND_TOP_TOOLTIP_ALLIES("Top factions by allies"),
	COMMAND_TOP_TOOLTIP_ENEMIES("Top factions by enemies"),
	COMMAND_TOP_TOOLTIP_POWER("Top factions by power"),
	COMMAND_TOP_TOOLTIP_LAND("Top factions by land"),
	
	COMMAND_UNCLAIM_SAFEZONE_SUCCESS("<i>Safe zone was unclaimed."),
	COMMAND_UNCLAIM_SAFEZONE_NOPERM("<b>This is a safe zone. You lack permissions to unclaim."),
	COMMAND_UNCLAIM_WARZONE_SUCCESS("<i>War zone was unclaimed."),
	COMMAND_UNCLAIM_WARZONE_NOPERM("<b>This is a war zone. You lack permissions to unclaim."),
	COMMAND_UNCLAIM_UNCLAIMED("%1$s<i> unclaimed some of your land."),
	COMMAND_UNCLAIM_UNCLAIMS("<i>You unclaimed this land."),
	COMMAND_UNCLAIM_LOG("%1$s unclaimed land at (%2$s) from the faction: %3$s"),
	COMMAND_UNCLAIM_WRONGFACTION("<b>You don't own this land."),
	COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
	COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
	COMMAND_UNCLAIM_FACTIONUNCLAIMED("%1$s<i> unclaimed some land."),
	COMMAND_UNCLAIM_FACTIONUNCLAIMEDAMOUNT("<player><i> unclaimed <teal><amount> <i>chunks in a radius of <teal><radius> <i>from <teal><chunk>"),
	COMMAND_UNCLAIM_DESCRIPTION("Unclaim the land where you are standing"),

	COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
	COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
	COMMAND_UNCLAIMALL_UNCLAIMED("%1$s<i> unclaimed ALL of your faction's land."),
	COMMAND_UNCLAIMALL_LOG("%1$s unclaimed everything for the faction: %2$s"),
	COMMAND_UNCLAIMALL_DESCRIPTION("Unclaim all of your factions land"),

	COMMAND_VERSION_VERSION("<i>You are running %1$s"),
	COMMAND_VERSION_DESCRIPTION("Show plugin and translation version information"),

	COMMAND_WARUNCLAIMALL_DESCRIPTION("Unclaim all warzone land"),
	COMMAND_WARUNCLAIMALL_SUCCESS("<i>You unclaimed ALL war zone land."),
	COMMAND_WARUNCLAIMALL_UNCLAIMEDIN("<i>You unclaimed ALL war zone land in <world>."),
	COMMAND_WARUNCLAIMALL_LOGALL("<who> unclaimed all war zones."),
	COMMAND_WARUNCLAIMALL_LOGWORLD("<who> unclaimed all war zones in <world>."),

	COMMAND_STYLE_DESCRIPTION("Set a factions styles"),
	COMMAND_STYLE_ARG_CHARACTER("character"),
	COMMAND_STYLE_ARG_COLOUR("color"),
	COMMAND_STYLE_ARG_VALUE("value"),
	COMMAND_STYLE_INVALIDSTYLE("<b>Invalid type, you can only set color or character!"),	
	COMMAND_STYLE_INVALIDCOLOUR("<b>Invalid colour!"),	
	COMMAND_STYLE_COLOURUPDATED("<i>Forced map colour set to <colour><i>."),	
	COMMAND_STYLE_CHARACTERUPDATED("<i>Forced map character set to <character><i>."),	
	
	// -------------------------------------------------- //
	// LEAVING
	// -------------------------------------------------- //
	
	LEAVE_PASSADMIN("<b>You must give the admin role to someone else first."),
	LEAVE_NEGATIVEPOWER("<b>You cannot leave until your power is positive."),
	LEAVE_TOLEAVE("to leave your faction."),
	LEAVE_FORLEAVE("for leaving your faction."),
	LEAVE_LEFT("%s<i> left faction %s<i>."),
	LEAVE_DISBANDED("<i>%s<i> was disbanded."),
	LEAVE_DISBANDEDLOG("The faction %s (%s) was disbanded due to the last player (%s) leaving."),
	LEAVE_DESCRIPTION("Leave your faction"),
	LEAVE_NEWADMINPROMOTED_PLAYER("<i>Faction admin <h><player><i> has been removed. <new-admin><i> has been promoted as the new faction admin."),
	LEAVE_NEWADMINPROMOTED_UNKNOWN("<i>Faction admin has been removed. <new-admin><i> has been promoted as the new faction admin."),
	
	// -------------------------------------------------- //
	// CLAIMING
	// -------------------------------------------------- //

	CLAIM_PROTECTED("<b>This land is protected"),
	CLAIM_DISABLED("<b>Sorry, this world has land claiming disabled."),
	CLAIM_CANTCLAIM("<b>You can't claim land for <h>%s<b>."),
	CLAIM_ALREADYOWN("%s<i> already own this land."),
	CLAIM_MUSTBE("<b>You must be <h>%s<b> to claim land."),
	CLAIM_MEMBERS("Factions must have at least <h>%s<b> members to claim land."),
	CLAIM_SAFEZONE("<b>You can not claim a Safe Zone."),
	CLAIM_WARZONE("<b>You can not claim a War Zone."),
	CLAIM_POWER("<b>You can't claim more land! You need more power!"),
	CLAIM_LIMIT("<b>Limit reached. You can't claim more land!"),
	CLAIM_ALLY("<b>You can't claim the land of your allies."),
	CLAIM_CONTIGIOUS("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!"),
	CLAIM_FACTIONCONTIGUOUS("<b>You can only claim additional land which is connected to your first claim!"),
	CLAIM_PEACEFUL("%s<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions."),
	CLAIM_PEACEFULTARGET("%s<i> owns this land, and is a peaceful faction. You cannot claim land from them."),
	CLAIM_THISISSPARTA("%s<i> owns this land and is strong enough to keep it."),
	CLAIM_BORDER("<b>You must start claiming land at the border of the territory."),
	CLAIM_TOCLAIM("to claim this land"),
	CLAIM_FORCLAIM("for claiming this land"),
	CLAIM_TOOVERCLAIM("to overclaim this land"),
	CLAIM_FOROVERCLAIM("for over claiming this land"),
	CLAIM_CLAIMED("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>."),
	CLAIM_CLAIMEDLOG("%s claimed land at (%s) for the faction: %s"),
	CLAIM_OVERCLAIM_DISABLED("<i>Over claiming is disabled on this server."),
	CLAIM_TOOCLOSETOOTHERFACTION("<i>Your claim is too close to another Faction. Buffer required is %d"),
	CLAIM_OUTSIDEWORLDBORDER("<i>Your claim is outside the border."),
	CLAIM_OUTSIDEBORDERBUFFER("<i>Your claim is outside the border. %d chunks away world edge required."),
	
	// -------------------------------------------------- //
	// GENERIC
	// -------------------------------------------------- //

	GENERIC_YOU("you"),
	GENERIC_YOURFACTION("your faction"),
	GENERIC_NOPERMISSION("<b>You don't have permission to %1$s."),
	GENERIC_DOTHAT("do that"),  //Ugh nuke this from high orbit
	GENERIC_NOPLAYERMATCH("<b>No player match found for \"<p>%1$s<b>\"."),
	GENERIC_NOPLAYERFOUND("<b>No player \"<p>%1$s<b>\" could not be found."),
	GENERIC_NOWORLDFOUND("<b>No World \"<p><world><b>\" could not be found."),
	GENERIC_ARGS_TOOFEW("<b>Too few arguments. <i>Use like this:"),
	GENERIC_ARGS_TOOMANY("<b>Strange argument \"<p>%1$s<b>\". <i>Use the command like this:"),
	GENERIC_DEFAULTDESCRIPTION("Default faction description :("),
	GENERIC_OWNERS("Owner(s): %1$s"),
	GENERIC_PUBLICLAND("Public faction land."),
	GENERIC_FACTIONLESS("factionless"),
	GENERIC_SERVERADMIN("A server admin"),
	GENERIC_DISABLED("disabled"),
	GENERIC_ENABLED("enabled"),
	GENERIC_INFINITY("∞"),
	GENERIC_CONSOLEONLY("This command cannot be run as a player."),
	GENERIC_PLAYERONLY("<b>This command can only be used by ingame players."),
	GENERIC_ASKYOURLEADER("<i> Ask your leader to:"),
	GENERIC_YOUSHOULD("<i>You should:"),
	GENERIC_YOUMAYWANT("<i>You may want to: "),
	GENERIC_TRANSLATION_VERSION("Translation: %1$s(%2$s,%3$s) State: %4$s"),
	GENERIC_TRANSLATION_CONTRIBUTORS("Translation contributors: %1$s"),
	GENERIC_FACTIONTAG_TOOSHORT("<i>The faction tag can't be shorter than <h>%1$s<i> chars."),
	GENERIC_FACTIONTAG_TOOLONG("<i>The faction tag can't be longer than <h>%s<i> chars."),
	GENERIC_FACTIONTAG_ALPHANUMERIC("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed."),
	GENERIC_PLACEHOLDER("<This is a placeholder for a message you should not see>"),
	GENERIC_HOMEREMOVED("<b>Your faction home has been un-set since it is no longer in your territory."),
	GENERIC_NULLPLAYER("null player"),

	// -------------------------------------------------- //
	// COMPASS
	// -------------------------------------------------- //

	COMPASS_SHORT_NORTH("N"),
	COMPASS_SHORT_EAST("E"),
	COMPASS_SHORT_SOUTH("S"),
	COMPASS_SHORT_WEST("W"),
	
	// -------------------------------------------------- //
	// CHAT MODES
	// -------------------------------------------------- //

	CHAT_FACTION("faction chat"),
	CHAT_ALLIANCE("alliance chat"),
	CHAT_TRUCE("truce chat"),
	CHAT_PUBLIC("public chat"),

	// -------------------------------------------------- //
	// ECONOMY
	// -------------------------------------------------- //
	
	ECON_OFF("no %s"), // no balance, no value, no refund, etc
	ECON_TRANSFER_CANTAFFORD("<h><from><b> can't afford to transfer <h><amount><b> to <target><b>."), 
	ECON_TRANSFER_UNABLE("Unable to transfer <amount><b> to <h><target><b> from <h><from><b>."), 
	ECON_BALANCE("<a><player>'s<i> balance is <h><amount><i>."), 
	ECON_CANTAFFORD("<h><player><i> can't afford <h><amount><i> <forwhat>."), 
	ECON_ERROR_ONE("Economy integration is enabled, but the plugin \"Vault\" is not installed."),
	ECON_ERROR_TWO("Economy integration is disabled, but the plugin \"Vault\" is not installed."),
	ECON_ERROR_THREE("Economy integration is enabled, but the plugin \"Vault\" is not hooked into an economy plugin."),
	ECON_ERROR_FOUR("Economy integration is disabled, but the plugin \"Vault\" is not hooked into an economy plugin."),
	ECON_LACKSCONTROL("<h><player><i> lacks permission to control <h><target>'s<i> money"),
	
	// -------------------------------------------------- //
	// RELATIONS
	// -------------------------------------------------- //

	RELATION_MEMBER_SINGULAR("member"),
	RELATION_MEMBER_PLURAL("members"),
	RELATION_ALLY_SINGULAR("ally"),
	RELATION_ALLY_PLURAL("allies"),
	RELATION_TRUCE_SINGULAR("truce"),
	RELATION_TRUCE_PLURAL("truces"),
	RELATION_NEUTRAL_SINGULAR("neutral"),
	RELATION_NEUTRAL_PLURAL("neutrals"),
	RELATION_ENEMY_SINGULAR("enemy"),
	RELATION_ENEMY_PLURAL("enemies"),

	// -------------------------------------------------- //
	// ROLES
	// -------------------------------------------------- //

	ROLE_ADMIN("admin"),
	ROLE_COLEADER("coleader"),
	ROLE_MODERATOR("moderator"),
	ROLE_NORMAL("normal member"),

	// -------------------------------------------------- //
	// ROLE CHANGE TITLES
	// -------------------------------------------------- //

	ROLETITLES_HEADER("<green>New <rank>"),
	ROLETITLES_FOOTER("<gold><player> was promoted to <rank>"),
	
	// -------------------------------------------------- //
	// REGION TYPES
	// -------------------------------------------------- //

	REGION_SAFEZONE("safezone"),
	REGION_WARZONE("warzone"),
	REGION_WILDERNESS("wilderness"),

	REGION_PEACEFUL("peaceful territory"),

	// -------------------------------------------------- //
	// PLAYER EVENTS
	// -------------------------------------------------- //

	PLAYER_CANTHURT("<i>You may not harm other players in %s"),
	PLAYER_SAFEAUTO("<i>This land is now a safe zone."),
	PLAYER_WARAUTO("<i>This land is now a war zone."),
	PLAYER_OUCH("<b>Ouch, that is starting to hurt. You should give it a rest."),
	PLAYER_USE_WILDERNESS("<b>You can't use <h>%s<b> in the wilderness."),
	PLAYER_USE_SAFEZONE("<b>You can't use <h>%s<b> in a safe zone."),
	PLAYER_USE_WARZONE("<b>You can't use <h>%s<b> in a war zone."),
	PLAYER_USE_TERRITORY("<b>You can't <h>%s<b> in the territory of <h>%s<b>."),
	PLAYER_USE_OWNED("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>."),
	PLAYER_COMMAND_WARZONE("<b>You can't use the command '%s' in warzone."),
	PLAYER_COMMAND_NEUTRAL("<b>You can't use the command '%s' in neutral territory."),
	PLAYER_COMMAND_ENEMY("<b>You can't use the command '%s' in enemy territory."),
	PLAYER_COMMAND_PERMANENT("<b>You can't use the command '%s' because you are in a permanent faction."),
	PLAYER_COMMAND_ALLY("<b>You can't use the command '%s' in ally territory."),
	PLAYER_COMMAND_TRUCE("<b>You can't use the command '%s' in truce territory."),
	PLAYER_COMMAND_WILDERNESS("<b>You can't use the command '%s' in the wilderness."),

	PLAYER_POWER_NOLOSS_PEACEFUL("<i>You didn't lose any power since you are in a peaceful faction."),
	PLAYER_POWER_NOLOSS_WORLD("<i>You didn't lose any power due to the world you died in."),
	PLAYER_POWER_NOLOSS_WILDERNESS("<i>You didn't lose any power since you were in the wilderness."),
	PLAYER_POWER_NOLOSS_WARZONE("<i>You didn't lose any power since you were in a war zone."),
	PLAYER_POWER_LOSS_WARZONE("<b>The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\n<i>Your power is now <h>%d / %d"),
	PLAYER_POWER_NOW("<i>Your power is now <h>%d / %d"),

	PLAYER_PVP_LOGIN("<i>You can't hurt other players for %d seconds after logging in."),
	PLAYER_PVP_REQUIREFACTION("<i>You can't hurt other players until you join a faction."),
	PLAYER_PVP_FACTIONLESS("<i>You can't hurt players who are not currently in a faction."),
	PLAYER_PVP_PEACEFUL("<i>Peaceful players cannot participate in combat."),
	PLAYER_PVP_NEUTRAL("<i>You can't hurt neutral factions. Declare them as an enemy."),
	PLAYER_PVP_CANTHURT("<i>You can't hurt %s<i>."),

	PLAYER_PVP_NEUTRALFAIL("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy."),
	PLAYER_PVP_TRIED("%s<i> tried to hurt you."),
	
	PLAYER_PAINFUL_FACTION_BUILD("<b>It is painful to try to build in the territory of <name>"),
	PLAYER_PAINFUL_FACTION_DESTROY("<b>It is painful to try to destroy in the territory of <name>"),
	PLAYER_PAINFUL_FACTION_USE("<b>It is painful to try to use in the territory of <name>"),
	PLAYER_PAINFUL_FACTION_FROSTWALK("<b>It is painful to try to frost walk in the territory of <name>"),
	PLAYER_PAINFUL_FACTION_PLACEPAINTING("<b>It is painful to try to place paintings in the territory of <name>"),
	PLAYER_PAINFUL_FACTION_BREAKPAINTING("<b>It is painful to try to break paintings in the territory of <name>"),
	
	PLAYER_CANT_FACTION_BUILD("<b>You can't build in the territory of <name>"),
	PLAYER_CANT_FACTION_DESTROY("<b>You can't destroy in the territory of <name>"),
	PLAYER_CANT_FACTION_USE("<b>You can't use this in the territory of <name>"),
	PLAYER_CANT_FACTION_FROSTWALK("<b>You can't frost walk in the territory of <name>"),
	PLAYER_CANT_FACTION_PLACEPAINTING("<b>You can't place paintings in the territory of <name>"),
	PLAYER_CANT_FACTION_BREAKPAINTING("<b>You can't break paintings in the territory of <name>"),

	PLAYER_PAINFUL_OWNED_BUILD("<b>It is painful to try to build in this territory, it is owned by: <who>"),
	PLAYER_PAINFUL_OWNED_DESTROY("<b>It is painful to try to destroy in this territory, it is owned by: <who>"),
	PLAYER_PAINFUL_OWNED_USE("<b>It is painful to try to use this in this territory, it is owned by: <who>"),
	PLAYER_PAINFUL_OWNED_FROSTWALK("<b>It is painful to try to frost walk in this territory, it is owned by: <who>"),
	PLAYER_PAINFUL_OWNED_PLACEPAINTING("<b>It is painful to try to place paintings in this territory, it is owned by: <who>"),
	PLAYER_PAINFUL_OWNED_BREAKPAINTING("<b>It is painful to try to break paintings in this territory, it is owned by: <who>"),

	PLAYER_CANT_OWNED_BUILD("<b>You can't build in this territory, it is owned by <who>"),
	PLAYER_CANT_OWNED_DESTROY("<b>You can't destroy in this territory, it is owned by <who>"),
	PLAYER_CANT_OWNED_USE("<b>You can't use this in this territory, it is owned by <who>"),
	PLAYER_CANT_OWNED_FROSTWALK("<b>You can't frost walk in this territory, it is owned by <who>"),
	PLAYER_CANT_OWNED_PLACEPAINTING("<b>You can't place paintings in this territory, it is owned by <who>"),
	PLAYER_CANT_OWNED_BREAKPAINTING("<b>You can't break paintings in this territory, it is owned by <who>"),
	
	PLAYER_CANT_SAFEZONE_BUILD("<b>You can't build in the wilderness."),
	PLAYER_CANT_SAFEZONE_DESTROY("<b>You can't destroy in the wilderness."),
	PLAYER_CANT_SAFEZONE_USE("<b>You can't use this in the wilderness."),
	PLAYER_CANT_SAFEZONE_FROSTWALK("<b>You can't frostwalk in the wilderness."),
	PLAYER_CANT_SAFEZONE_PLACEPAINTING("<b>You can't place paintings in the wilderness."),
	PLAYER_CANT_SAFEZONE_BREAKPAINTING("<b>You can't break paintings in the wilderness."),

	PLAYER_CANT_WILDERNESS_BUILD("<b>You can't build in the wilderness."),
	PLAYER_CANT_WILDERNESS_DESTROY("<b>You can't destroy in the wilderness."),
	PLAYER_CANT_WILDERNESS_USE("<b>You can't use this in the wilderness."),
	PLAYER_CANT_WILDERNESS_FROSTWALK("<b>You can't frostwalk in the wilderness."),
	PLAYER_CANT_WILDERNESS_PLACEPAINTING("<b>You can't place paintings in the wilderness."),
	PLAYER_CANT_WILDERNESS_BREAKPAINTING("<b>You can't break paintings in the wilderness."),

	PLAYER_CANT_WARZONE_BUILD("<b>You can't build in a warzone."),
	PLAYER_CANT_WARZONE_DESTROY("<b>You can't destroy in a warzone."),
	PLAYER_CANT_WARZONE_USE("<b>You can't use this in a warzone."),
	PLAYER_CANT_WARZONE_FROSTWALK("<b>You can't frostwalk in a warzone."),
	PLAYER_CANT_WARZONE_PLACEPAINTING("<b>You can't place paintings in a warzone."),
	PLAYER_CANT_WARZONE_BREAKPAINTING("<b>You can't break paintings in a warzone."),
	
	// -------------------------------------------------- //
	// PAGES
	// -------------------------------------------------- //

	NOPAGES("<i>Sorry. No Pages available."),
	INVALIDPAGE("<i>Invalid page. Must be between 1 and %1$d"),

	// -------------------------------------------------- //
	// UNCATEGORISED
	// -------------------------------------------------- //

	TITLE("title", "&bFactions &0|&r"),
	WILDERNESS("wilderness", "&2Wilderness"),
	WILDERNESS_DESCRIPTION("wilderness-description", ""),
	WARZONE("warzone", "&4Warzone"),
	WARZONE_DESCRIPTION("warzone-description", "Not the safest place to be."),
	SAFEZONE("safezone", "&6Safezone"),
	SAFEZONE_DESCRIPTION("safezone-description", "Free from pvp and monsters."),
	TOGGLE_SB("toggle-sb", "You now have scoreboards set to {value}"),
	FACTION_LEAVE("faction-leave", "<a>Leaving %1$s, <a>Entering %2$s"),
	DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}] &r"),
	FACTION_LOGIN("faction-login", "&e%1$s &9logged in."),
	FACTION_LOGOUT("faction-logout", "&e%1$s &9logged out.."),
	NOFACTION_PREFIX("nofactions-prefix", "&6[&a4-&6]&r"),
	DATE_FORMAT("date-format", "MM/d/yy h:ma"), // 3/31/15 07:49AM

	// -------------------------------------------------- //
	// RAIDABLE
	// -------------------------------------------------- //

	RAIDABLE_TRUE("raidable-true", "true"),
	RAIDABLE_FALSE("raidable-false", "false"),

	// -------------------------------------------------- //
	// WARMUPS
	// -------------------------------------------------- //

	WARMUPS_NOTIFY_TELEPORT("&eYou will teleport to &d%1$s &ein &d%2$d &eseconds."),
	WARMUPS_ALREADY("&cYou are already warming up."),
	WARMUPS_CANCELLED("&cYou have cancelled your warmup."),

	// -------------------------------------------------- //
	// INTEGRATION: DYNMAP
	// -------------------------------------------------- //
	
	DYNMAP_TITLE("<h>Dynmap Integration: <i>"),
	
	// -------------------------------------------------- //
	// EXPANSION: FACTIONSFLY
	// -------------------------------------------------- //
	
	EXPANSION_FACTIONSFLY_NO_ENDERPEARL("<b>You can't use enderpearl while flying."),
	EXPANSION_FACTIONSFLY_NO_CHORUSFRUIT("<b>You can't use chorus fruit while flying."),
	EXPANSION_FACTIONSFLY_NOT_HERE("<b>You can't fly here."),
	EXPANSION_FACTIONSFLY_ENABLED("<i>Flight enabled."),
	EXPANSION_FACTIONSFLY_DISABLED("<i>Flight disabled."),
	
	// -------------------------------------------------- //
	// CONFIG DESCRIPTIONS
	// -------------------------------------------------- //
	
	CONFIG_MISC_SECTION("******************** [ MISC SETTINGS ] ********************"),
	CONFIG_MISC_DEBUG("* Debug Mode\r\n   * If enabled, fine details will be sent to console."),
	CONFIG_MISC_STATISTICS("* Log Statistics\r\n   * If enabled, we will log useful statistics."),
	CONFIG_MISC_DISABLEINWORLDS("* Disable Factions in worlds\r\n   * If worlds are added here, factions commands and events won't work in these worlds."),
	CONFIG_MISC_HIDECONFIGCOMMENTS("* If you want to hide comments (like what you are reading now!) set this to true!"),
	
	CONFIG_NON16_SECTION("******************** [ NON 1.6 FEATURES ] ********************"),
	CONFIG_NON16_TRUCES("* Enable Truces\r\n   * To disable truces, set this to false."),
	CONFIG_NON16_COLEADERS("* Enable Coleaders\r\n   * To disable coleaders, set to false."),
	CONFIG_NON16_FLAGS("* Enable Flags\r\n   * To disable flags, set to false."),
	
	CONFIG_COMMANDS_SECTION("******************** [ COMMANDS ] ********************"),
	CONFIG_COMMANDS_ALLOWNOSLASH("* Allow /f commands to be executed without the initial slash."),

	CONFIG_WARMUPS_SECTION("******************** [ WARMUPS ] ********************"),
	CONFIG_WARMUPS_WARP("* How long (in seconds) the warmup for warps should be."),
	CONFIG_WARMUPS_HOME("* How long (in seconds) the warmup for homes should be."),

	CONFIG_RELATIONS_SECTION("******************** [ RELATIONS ] ********************"),
	CONFIG_RELATIONS_MAX("* Maximum amount of each relationship. NEUTRAL should not be changed if\r\n   * it is the default relationship type."),
	CONFIG_RELATIONS_ENFORCED("* Relationships require both sides to set the relationship. If you want certaion relationships to automatically be enforced, set it here.\r\n   * A better way to understand this, if you set enemy to true and either has enemy set - then enemy will be the enforced relation."),
	CONFIG_RELATIONS_COLOURS("* Relation Colours\r\n   * The colour of each relationship."),
	
	CONFIG_TOOLTIPS_SECTION("******************** [ TOOLTIPS ] ********************"),
	CONFIG_TOOLTIPS_TOOLTIPS("* The tooltips for each of these can be configured using placeholders."),
	
	CONFIG_EMBLEMS_SECTION("******************** [ EMBLEMS ] ********************"),
	CONFIG_EMBLEMS_ENABLED("* Enable emblems.\r\n   * To disable emblems, set this to false."),
	CONFIG_EMBLEMS_MINLENGTH("* Minimum length of emblems.\r\n   * This minimum length will be enforced, set to -1 to disable."),
	CONFIG_EMBLEMS_MAXLENGTH("* Maximum length of emblems.\r\n   * This maximum length will be enforced, set to -1 to disable."),
	CONFIG_EMBLEMS_MINROLE("* Minimum role to set emblems."),
	
	CONFIG_POWER_SECTION("******************** [ POWER ] ********************"),
	CONFIG_POWER_MAX("* The maximum power per player."),
	CONFIG_POWER_MIN("* The minimum power per player."),
	CONFIG_POWER_STARTING("* The amount of power a player starts with."),
	CONFIG_POWER_PERMINUTE("* The amount of power per minute.\r\n   * With the default, players get 0.2 per minute. Which means it takes 5 minutes to recover 1 power."),
	CONFIG_POWER_PERDEATH("* The amount of power lost per death."),
	CONFIG_POWER_REGENOFFLINE("* Should players regen power while offline? Set to true to enable power regeneration while offline."),
	CONFIG_POWER_OFFLINELOSS("* Should players lose power while offline?\r\n   * Set to 0 to disable. Otherwise, they will lose this amount per day while offline."),
	CONFIG_POWER_OFFLINELOSSLIMIT("* The limit for power loss while offline. By default, it is set to 0 so they don't go negative."),
	CONFIG_POWER_FACTIONMAX("* The maximum power per faction."),
	
	CONFIG_PLAYERPREFIX_SECTION("******************** [ PLAYER PREFIX ] ********************"),
	CONFIG_PLAYERPREFIX_ADMIN("* The prefix for the admin role."),
	CONFIG_PLAYERPREFIX_COLEADER("* The prefix for the coleader role."),
	CONFIG_PLAYERPREFIX_MOD("* The prefix for the mod role."),
	
	CONFIG_FACTION_SECTION("******************** [ FACTION ] ********************"),
	CONFIG_FACTION_DEFAULTRELATION("* The default relation of factions when they are created."),
	CONFIG_FACTION_TAGMIN("* The minimum tag length."),
	CONFIG_FACTION_TAGMAX("* The maximum tag length."),
	CONFIG_FACTION_FORCEUPPER("* Should we for the faction name to be all UPPERCASE?"),
	CONFIG_FACTION_DESCMAX("* Set the maximum description length. Set to -1 to disable."),
	CONFIG_FACTION_DEFAULTOPEN("* Should factions default to open?"),
	CONFIG_FACTION_MEMBERLIMIT("* Member limit for factions. Set to -1 to disable."),
	CONFIG_FACTION_MEMBERLIMITPEACEFUL("* Member limit for peaceful factions. Defaults to previous value if disabled."),
	CONFIG_FACTION_STARTINGID("* The default starting ID for factions. You shouldn't need to change this."),
	CONFIG_FACTION_MAPKEY("* Should the key on the faction map. This is the faction names."),
	CONFIG_FACTION_SHOWNEUTRAL("* Should we show neutral relation factions on the map?"),
	CONFIG_FACTION_SHOWENEMY("* Should we show enemy relation factions on the map?"),
	CONFIG_FACTION_ALLOWCOLOURS("* Should we allow colour codes in the faction name?"),
	CONFIG_FACTION_ALLOWCOLOURSDESC("* Should we allow colour codes in the faction description?"),
	CONFIG_FACTION_CANLEAVEWITHNEGATIVEPOWER("* Should players be able to leave with negative power?"),
	CONFIG_FACTION_DISABLEPISTONS("* Should pistons be disable in faction territories?"),
	CONFIG_FACTION_BROADCASTCHANGETAG("* Should LegacyFactions broadcast faction name tag changes?"),
	CONFIG_FACTION_BROADCASTCHANGEDESC("* Should LegacyFactions broadcast faction description changes?"),
	CONFIG_FACTION_PERMNOPROMOTE("* Should permanent factions auto promote a leader when the leader leaves?"),
	CONFIG_FACTION_AUTOKICK_MIN("* The minimum role required to use the `/f autokick` command"),
	CONFIG_FACTION_AUTOKICK_MAX("* The maximum value a faction can set their autokick to."),
	
	CONFIG_FLAGS_SECTION("******************** [ FLAGS ] ********************"),
	CONFIG_FLAGS_MINROLE("* Minimum role to set flags."),
	CONFIG_FLAGS_TOGGLEABLE("* These next few variables allow you to set which flags can be toggled by players.\r\n   * If set to false, only a player in admin bypass mode can toggle them."),
	
	CONFIG_DAMAGEMODIFIER_SECTION("******************** [ DAMAGE MODIFIERS ] ********************"),
	CONFIG_DAMAGEMODIFIER_EXPLANATION("* Damage modifiers are based on a percent. 100 = 100% = the default damage (no change!).\r\n   * If you set a modifier to 120 (120%) then an additional 20% damage will be applied for that condition. \r\n   * If you set them to 80 (80%), 20% less damage will be applied."),
	CONFIG_DAMAGEMODIFIER_PERCENTRELATIONPLAYER("* Damage from a player based on the relation to that player."),
	CONFIG_DAMAGEMODIFIER_PERCENTRELATIONLAND("* Damage from a player based on the relation to the land we are standing in."),
	CONFIG_DAMAGEMODIFIER_PERCENTRELATIONLANDBYMOB("* Damage from a mob based on the relation to the land we are standing in."),
	CONFIG_DAMAGEMODIFIER_GLOBALWILDERNESS("* Damage from anything in the wilderness."),
	CONFIG_DAMAGEMODIFIER_GLOBALSAFEZONE("* Damage from anything in the safezone."),
	CONFIG_DAMAGEMODIFIER_GLOBALWARZONE("* Damage from anything in the warzone."),
	
	CONFIG_FACTIONCHAT_SECTION("******************** [ EXPANSION: FACTION CHAT ] ********************"),
	CONFIG_FACTIONCHAT_ENABLED("* Enable FactionChat expansion."),
	CONFIG_FACTIONCHAT_ALLIANCECHAT("* Enabled alliance chat."),
	CONFIG_FACTIONCHAT_TRUCECHAT("* Enable truce chat."),
	CONFIG_FACTIONCHAT_PUBLICFORMAT("* Enable formatting for public chat."),
	CONFIG_FACTIONCHAT_FORMATS("* These are the formats you can set for each chat type."),
	CONFIG_FACTIONCHAT_CHATPLUGINCHANNEL("* If supported, we will integrate with chat plugins and switch to their channels instead of handling it ourselves."),
	CONFIG_FACTIONCHAT_CHATPLUGINGLOBALCHANNEL("* The global chat channel. Set to blank if you want LegacyFactions to work it out ourselves."),
	CONFIG_FACTIONCHAT_CHATTAGENABLED("* Injected Chat Tag will cancel the Chat event and send it to players on its own, but allows for relational colours."),
	CONFIG_FACTIONCHAT_CHATTAGRELATIONALOVERRIDE("* Enable relational override. If you want to use relational placeholders and your chat plugin doesn't support it we must cancel and handle the chat ourselves. "),
	CONFIG_FACTIONCHAT_CHATTAGPLACEHOLDER("* Chat tag placeholder. "),
	CONFIG_FACTIONCHAT_CHATTAGFORMATDEFAULT("* Chat Tag format. "),
	CONFIG_FACTIONCHAT_CHATTAGFORMATFACTIONLESS("* Chat Tag format for factionless. "),
	
	CONFIG_FACTIONFLY_SECTION("******************** [ EXPANSION: FACTION FLY ] ********************"),
	CONFIG_FACTIONFLY_ENABLED("* Enable FactionFly expansion."),
	CONFIG_FACTIONFLY_ENDERPEARL("* Disable Enderpearl when factions fly is enabled "),
	CONFIG_FACTIONFLY_CHORUSFRUIT("* Disable Chorus Fruit when factions fly is enabled "),
	CONFIG_FACTIONFLY_MAXY("* Max fly height. Set to -1 to disable."),
	CONFIG_FACTIONFLY_NOFALLDAMAGE("* This will make a player not obtain fall damage when fly is disabled"),
	CONFIG_FACTIONFLY_FLOORTELEPORT("* This will make a player teleport to floor when disabled"),
	
	CONFIG_TASKS_SECTION("******************** [ TASKS ] ********************"),
	CONFIG_TASKS_SAVETOFILEEVERYXMINUTES("* How frequently should we save to file."),
	CONFIG_TASKS_AUTOLEAVEAFTERDAYSOFINACTIVITY("* After how many days should a player autoleave? "),
	CONFIG_TASKS_AUTOLEAVEROUTINEXMINUTES("* How frequently should we run the autoleave task?"),
	CONFIG_TASKS_AUTOLEAVEROUTINEMAXMSPERTICK("* This is used to throttle the autoleave task."),
	CONFIG_TASKS_REMOVEWHENBANNED("* Remove player data when banned."),
	CONFIG_TASKS_AUTOLEAVEDELETEFPLAYERDATA("* Delete player data when they autoleave?"),
	
	CONFIG_FORMAT_SECTION("******************** [ FORMAT ] ********************"),
	CONFIG_FORMAT_DESC("* LegacyFactions support multiple script formats. By default, all english characters are supported."
			+ "\r\n   * You can add support for any other script support that Java allows."
			+ "\r\n   * See: https://github.com/redstone/LegacyFactions/wiki/Multilingual-Script-Support"),
	
	CONFIG_SERVERLOGGING_SECTION("******************** [ SERVER LOGGING ] ********************"),
	CONFIG_SERVERLOGGING_DESC("* LegacyFactions logs certain actions to the server console. You can change those here."),
	
	CONFIG_EXPLOITS_SECTION("******************** [ EXPLOITS ] ********************"),
	CONFIG_EXPLOITS_OBSIDIANGENERATORS("* Should we attempt to block a certain kind of obsidian generator exploit?"),
	CONFIG_EXPLOITS_ENDERPEARL("* Should we attempt to block a certain ender pearl clipping exploit?"),
	CONFIG_EXPLOITS_INTERACTIONSPAM("* Should we attempt to block a certain interaction spam exploit?"),
	CONFIG_EXPLOITS_WATERLOG("* TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior." + 
			"\r\n   * But this optional change below provides workaround for waterwalling providing perfect protection,\n" + 
			"\r\n   * and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots."),
	CONFIG_EXPLOITS_LIQUIDFLOW("* Should we attempt to block a certain kind of liquid flow exploit? "),
	CONFIG_EXPLOITS_FINDEXPLOIT("* The find exploit refers to an exploit where the map is exploited."),
	
	CONFIG_PORTALS_SECTION("******************** [ PORTALS ] ********************"),
	CONFIG_PORTALS_DESC("* Portals can be limited with LegacyFactions."),
	
	CONFIG_SCOREBOARD_SECTION("******************** [ SCOREBOARD ] ********************"),
	CONFIG_SCOREBOARD_DESC("* LegacyFactions introduces a basic scoreboard integration that you can use."
			+ "\r\n   * For more advanced uses you may want to look into a plugin like ScoreboardStats or Featherboard."),
	CONFIG_SCOREBOARD_SCOREBOARDINCHAT("* On land change, should we show the scoreboard in chat?"),
	CONFIG_SCOREBOARD_EXPIRES("* After how long (in seconds) should we revert to the normal scoreboard."),
	CONFIG_SCOREBOARD_INFOENABLED("* Info Scoreboard: this scoreboard is for displaying the faction information."),
	CONFIG_SCOREBOARD_DEFAULTENABLED("* Default Scoreboard: this is the scoreboard displayed by default."),
	CONFIG_SCOREBOARD_FACTIONLESSENABLED("* Factionless Scoreboard: this is the scoreboard disaplyed by default, to players without a faction."),
	
	CONFIG_TERRITORYTITLES_SECTION("******************** [ TERRITORY TITLES ] ********************"),
	CONFIG_TERRITORYTITLES_DESC("Territory titles are shown when moving between land, or"
			+ "\r\n   * when ranks are changed (if enabled)."),
	
	CONFIG_TELEPROTTOSPAWN_SECTION("******************** [ TELEPORT TO SPAWN ON LOGOUT ] ********************"),
	
	CONFIG_TERRITORY_SECTION("******************** [ TERRITORY ] ********************"),
	
	CONFIG_WARPS_SECTION("******************** [ WARPS ] ********************"),
	
	CONFIG_HOMES_SECTION("******************** [ HOMES ] ********************"),
	
	CONFIG_PVPSETTINGS_SECTION("******************** [ PVP SETTINGS ] ********************"),
	
	CONFIG_PEACEFUL_SECTION("******************** [ PEACEFUL FACTIONS ] ********************"),
	
	CONFIG_CLAIMS_SECTION("******************** [ CLAIMS ] ********************"),
	
	CONFIG_ECONOMY_SECTION("******************** [ ECONOMY ] ********************"),
	
	CONFIG_WORLDGUARD_SECTION("******************** [ WORLDGUARD ] ********************"),
	
	CONFIG_DYNMAP_SECTION("******************** [ DYNMAP ] ********************"),
	
	CONFIG_BUFFERS_SECTION("******************** [ BUFFERS ] ********************"),
	
	CONFIG_STUCK_SECTION("******************** [ STUCK ] ********************"),
	
	CONFIG_RAIDS_SECTION("******************** [ RAIDS ] ********************"),
	CONFIG_RAIDS_DESC("* A faction is determined raidable if their land count is larger than their power rounded."
			+ "\r\n   * Raidable indicates that other factions can use items, break/place blocks, open chests, etc."),
	
	CONFIG_VAULTS_SECTION("******************** [ VAULTS ] ********************"),
	
	CONFIG_MAP_SECTION("******************** [ MAP COMMAND ] ********************"),
	
	CONFIG_HELP_SECTION("******************** [ HELP COMMAND ] ********************"),
	CONFIG_HELP_DESC("* In LegacyFactions there are multiple help command modes you can pick between:"
			+ "\r\n   * useOldHelp - This is the vanilla Factions 1.6 help."
			+ "\r\n   * useCustomHelp - This is the custom help, you can specify the help pages yourself."
			+ "\r\n   * default - If both options are set to false we use the new JSON help menu. This comes with buttons, and is automatically generated."),
	
	CONFIG_LIST_SECTION("******************** [ LIST COMMAND ] ********************"),
	
	CONFIG_SHOW_SECTION("******************** [ SHOW COMMAND ] ********************"),
	
	CONFIG_BACKEND_SECTION("******************** [ BACKEND ] ********************"),
	CONFIG_BACKEND_DESC("* You shouldn't change the backend here. Instead, use the /f convert command."),
	
	;
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private String path;
	private String value;
	private static YamlConfiguration langConfig;
	public static SimpleDateFormat sdf;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	/**
	 * Lang enum constructor.
	 *
	 * @param path  The string path.
	 * @param value The value of this string.
	 */
	Lang(String path, String value) {
		this.path = path;
		this.value = value;
	}

	/**
	 * Lang enum constructor. Use this when your desired path simply exchanges '_' for '.'
	 *
	 * @param start The default string.
	 */
	Lang(String start) {
		this.path = this.name().replace('_', '.');
		if (this.path.startsWith(".")) {
			path = "root" + path;
		}
		this.value = start;
	}

	/**
	 * Set the {@code YamlConfiguration} to use.
	 *
	 * @param config The config to set.
	 */
	public static void setFile(YamlConfiguration config) {
		langConfig = config;
		sdf = new SimpleDateFormat(DATE_FORMAT.toString());
	}
	
	/**
	 * Format the string.
	 * @param args
	 * @return
	 */
	public String format(Object... args) {
		return String.format(toString(), args);
	}

	/**
	 * Get the default value of the path.
	 *
	 * @return The default value of the path.
	 */
	public String getDefault() {
		return this.value;
	}

	/**
	 * Get the path to the string.
	 *
	 * @return The path to the string.
	 */
	public String getPath() {
		return this.path;
	}
	
	public LangBuilder getBuilder() {
		return new LangBuilder(this);
	}
	
	public static void reload(String resourcePath) {
		File lang = new File(Factions.get().getDataFolder(), "locale.yml");
		InputStream defLangStream = null;
		if (resourcePath != null) {
			defLangStream = Factions.get().getResource(resourcePath);
		}
		
		Boolean langFailed = false;
		
		OutputStream out = null;
		if (!lang.exists()) {
			try {
				Factions.get().getDataFolder().mkdir();
				lang.createNewFile();
				if (defLangStream != null) {
					out = new FileOutputStream(lang);
					int read;
					byte[] bytes = new byte[1024];

					while ((read = defLangStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					
					YamlConfiguration defaultConfig = new YamlConfiguration();
					try {
						defaultConfig.load(new InputStreamReader(defLangStream, Charsets.UTF_8));
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
					
					Lang.setFile(defaultConfig);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Factions.get().warn("Couldn't create language file: Failed to save locale.yml");
				langFailed = true;
			} finally {
				if (defLangStream != null) {
					try {
						defLangStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

		if (!langFailed) {
			// Now we're going to populate a new YamlConfiguration so old Lang values are removed.
			YamlConfiguration currentLang;
			if (defLangStream != null) {
				currentLang = YamlConfiguration.loadConfiguration(new InputStreamReader(defLangStream, Charsets.UTF_8));
			} else {
				currentLang = YamlConfiguration.loadConfiguration(lang);
			}
			
			YamlConfiguration newLang = new YamlConfiguration();
			
			for (Lang item : Lang.values()) {
				if (currentLang.getString(item.getPath()) != null) {
					newLang.set(item.getPath(), currentLang.getString(item.getPath()));
				} else {
					newLang.set(item.getPath(), item.getDefault());					
				}
			}
			
			// Migrations
			if (currentLang.getString(Lang.COMMAND_SHOW_POWER.getPath(), "").contains("%5$s")) {
				newLang.set(Lang.COMMAND_SHOW_POWER.getPath(), Lang.COMMAND_SHOW_POWER.getDefault());
			}
			
			// Set the lang to use the new one
			Lang.setFile(newLang);
			
			try {
				// Save it
				newLang.save(lang);
			} catch (IOException e) {
				Factions.get().warn("Failed to save locale.yml.");
				e.printStackTrace();
			}
		} else {
			Factions.get().warn("Lang failed");
		}
	}
	
	@Override
	public String toString() {
		return this == TITLE ? ChatColor.translateAlternateColorCodes('&', langConfig.getString(this.path, value)) + " " : ChatColor.translateAlternateColorCodes('&', langConfig.getString(this.path, value));
	}
	
}
