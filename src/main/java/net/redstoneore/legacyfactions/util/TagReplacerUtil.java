package net.redstoneore.legacyfactions.util;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.integration.vault.VaultEngine;
import net.redstoneore.legacyfactions.lang.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Link between config and in-game messages<br> Changes based on faction / player<br> Interfaces the config lists with
 * {} variables to plugin
 */
public enum TagReplacerUtil {

    /**
     * Fancy variables, used by f show
     */
    ALLIES_LIST(TagType.FANCY, "{allies-list}"),
    TRUCES_LIST(TagType.FANCY, "{truces-list}"),
    ONLINE_LIST(TagType.FANCY, "{online-list}"),
    ENEMIES_LIST(TagType.FANCY, "{enemies-list}"),
    OFFLINE_LIST(TagType.FANCY, "{offline-list}"),

    /**
     * Player variables, require a player
     */
    PLAYER_NAME(TagType.FACTION, "{name}"),
    PLAYER_GROUP(TagType.PLAYER, "{player-group}"),
    LAST_SEEN(TagType.PLAYER, "{player-lastSeen}"),
    PLAYER_BALANCE(TagType.PLAYER, "{player-balance}"),
    PLAYER_POWER(TagType.PLAYER, "{player-power}"),
    PLAYER_MAXPOWER(TagType.PLAYER, "{player-maxpower}"),
    PLAYER_KILLS(TagType.PLAYER, "{player-kills}"),
    PLAYER_DEATHS(TagType.PLAYER, "{player-deaths}"),

    /**
     * Faction variables, require at least a player
     */
    HOME_X(TagType.FACTION, "{home_x}"),
    HOME_Y(TagType.FACTION, "{home_y}"),
    HOME_Z(TagType.FACTION, "{home_z}"),
    CHUNKS(TagType.FACTION, "{chunks}"),
    WARPS(TagType.FACTION, "{warps}"),
    HEADER(TagType.FACTION, "{header}"),
    POWER(TagType.FACTION, "{power}"),
    MAX_POWER(TagType.FACTION, "{maxPower}"),
    POWER_BOOST(TagType.FACTION, "{power-boost}"),
    LEADER(TagType.FACTION, "{leader}"),
    JOINING(TagType.FACTION, "{joining}"),
    FACTION(TagType.FACTION, "{faction}"),
    HOME_WORLD(TagType.FACTION, "{world}"),
    RAIDABLE(TagType.FACTION, "{raidable}"),
    PEACEFUL(TagType.FACTION, "{peaceful}"),
    PERMANENT(TagType.FACTION, "permanent"), // no braces needed
    TIME_LEFT(TagType.FACTION, "{time-left}"),
    LAND_VALUE(TagType.FACTION, "{land-value}"),
    DESCRIPTION(TagType.FACTION, "{description}"),
    CREATE_DATE(TagType.FACTION, "{create-date}"),
    LAND_REFUND(TagType.FACTION, "{land-refund}"),
    BANK_BALANCE(TagType.FACTION, "{faction-balance}"),
    ALLIES_COUNT(TagType.FACTION, "{allies}"),
    TRUCES_COUNT(TagType.FACTION, "{truces}"),
    ENEMIES_COUNT(TagType.FACTION, "{enemies}"),
    ONLINE_COUNT(TagType.FACTION, "{online}"),
    OFFLINE_COUNT(TagType.FACTION, "{offline}"),
    FACTION_SIZE(TagType.FACTION, "{members}"),
    FACTION_KILLS(TagType.FACTION, "{faction-kills}"),
    FACTION_DEATHS(TagType.FACTION, "{faction-deaths}"),
    EMBLEM(TagType.FACTION, "{emblem}"),

    /**
     * General variables, require no faction or player
     */
    MAX_WARPS(TagType.GENERAL, "{max-warps}"),
    MAX_ALLIES(TagType.GENERAL, "{max-allies}"),
    MAX_TRUCES(TagType.GENERAL, "{max-truces}"),
    MAX_ENEMIES(TagType.GENERAL, "{max-enemies}"),
    FACTIONLESS(TagType.GENERAL, "{factionless}"),
    TOTAL_ONLINE(TagType.GENERAL, "{total-online}");

    private TagType type;
    private String tag;

    protected enum TagType {
        FANCY(0), PLAYER(1), FACTION(2), GENERAL(3);
        public int id;

        TagType(int id) {
            this.id = id;
        }
    }

    TagReplacerUtil(TagType type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    /**
     * Protected access to this generic server related variable
     *
     * @return value for this generic server related variable<br>
     */
    protected String getValue() {
        switch (this) {
            case TOTAL_ONLINE:
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            case FACTIONLESS:
                return String.valueOf(FactionColl.get().getWilderness().getWhereOnline(true).size());
            case MAX_ALLIES:
            	if (Config.maxRelations.containsKey(Relation.ALLY) && Config.maxRelations.get(Relation.ALLY) > -1) {
            		return String.valueOf(Config.maxRelations.get(Relation.ALLY));
            	}
                return Lang.GENERIC_INFINITY.toString();
            case MAX_TRUCES:
                if (Config.maxRelations.containsKey(Relation.TRUCE) && Config.maxRelations.get(Relation.TRUCE) > -1) {
                    return String.valueOf(Config.maxRelations.get(Relation.TRUCE));
                }
                return Lang.GENERIC_INFINITY.toString();
            case MAX_ENEMIES:
            	if (Config.maxRelations.containsKey(Relation.ENEMY) && Config.maxRelations.get(Relation.ENEMY) > -1) {
            		return String.valueOf(Config.maxRelations.get(Relation.ENEMY));
            	}

                return Lang.GENERIC_INFINITY.toString();
            case MAX_WARPS:
                return String.valueOf(Config.warpsMax);
            default:
            	return null;
        }
    }

    /**
     * Gets the value for this (as in the instance this is called from) variable!
     *
     * @param faction Target faction
     * @param fplayer  Target player (can be null)
     *
     * @return the value for this enum!
     */
    protected String getValue(Faction faction, FPlayer fplayer) {
        if (this.type == TagType.GENERAL) {
            return getValue();
        }

        if (fplayer != null) {
            switch (this) {
                case HEADER:
                    return TextUtil.get().titleize(faction.getTag(fplayer));
                case PLAYER_NAME:
                    return fplayer.getName();
                case FACTION:
                    return !faction.isWilderness() ? faction.getTag(fplayer) : Lang.GENERIC_FACTIONLESS.toString();
                case LAST_SEEN:
                    String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fplayer.getLastLoginTime(), true, true) + Lang.COMMAND_STATUS_AGOSUFFIX;
                    return fplayer.isOnline() ? ChatColor.GREEN + Lang.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fplayer.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
                case PLAYER_GROUP:
                    return VaultEngine.getUtils().getPrimaryGroup(Bukkit.getOfflinePlayer(UUID.fromString(fplayer.getId())));
                case PLAYER_BALANCE:
                    return (VaultEngine.isSetup() ? VaultEngine.getUtils().getFriendlyBalance(fplayer) : Lang.ECON_OFF.format("balance"));
                case PLAYER_POWER:
                    return String.valueOf(fplayer.getPowerRounded());
                case PLAYER_MAXPOWER:
                    return String.valueOf(fplayer.getPowerMaxRounded());
                case PLAYER_KILLS:
                    return String.valueOf(fplayer.getKills());
                case PLAYER_DEATHS:
                    return String.valueOf(fplayer.getDeaths());
                default:
            }
        }
        switch (this) {
            case DESCRIPTION:
                return faction.getDescription();
            case FACTION:
                return faction.getTag();
            case JOINING:
                return (faction.getFlag(Flags.OPEN) ? Lang.COMMAND_SHOW_UNINVITED.toString() : Lang.COMMAND_SHOW_INVITATION.toString());
            case PEACEFUL:
                return faction.getFlag(Flags.PEACEFUL) ? Config.colorNeutral + Lang.COMMAND_SHOW_PEACEFUL.toString() : "";
            case PERMANENT:
                return faction.getFlag(Flags.PERMANENT) ? "permanent" : "{notPermanent}";
            case CHUNKS:
                return String.valueOf(faction.getLandRounded());
            case POWER:
                return String.valueOf(faction.getPowerRounded());
            case MAX_POWER:
                return String.valueOf(faction.getPowerMaxRounded());
            case POWER_BOOST:
                double powerBoost = faction.getPowerBoost();
                return (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? Lang.COMMAND_SHOW_BONUS.toString() : Lang.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
            case LEADER:
                FPlayer fAdmin = faction.getOwner();
                return fAdmin == null ? "Server" : fAdmin.getName().substring(0, fAdmin.getName().length() > 14 ? 13 : fAdmin.getName().length());
            case WARPS:
                return String.valueOf(faction.warps().size());
            case CREATE_DATE:
                return Lang.sdf.format(faction.getFoundedDate());
            case RAIDABLE:
                boolean raid = Config.raidable && faction.getLandRounded() >= faction.getPowerRounded();
                return raid ? Lang.RAIDABLE_TRUE.toString() : Lang.RAIDABLE_FALSE.toString();
            case HOME_WORLD:
                return faction.hasHome() ? faction.getHome().getWorld().getName() : Config.showMinimal ? null : "{ig}";
            case HOME_X:
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockX()) : Config.showMinimal ? null : "{ig}";
            case HOME_Y:
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockY()) : Config.showMinimal ? null : "{ig}";
            case HOME_Z:
                return faction.hasHome() ? String.valueOf(faction.getHome().getBlockZ()) : Config.showMinimal ? null : "{ig}";
            case LAND_VALUE:
                return VaultEngine.getUtils().shouldBeUsed() ? VaultEngine.getUtils().moneyString(VaultEngine.getUtils().calculateTotalLandValue(faction.getLandRounded())) : Config.showMinimal ? null : Lang.ECON_OFF.format("value");
            case LAND_REFUND:
                return VaultEngine.getUtils().shouldBeUsed() ? VaultEngine.getUtils().moneyString(VaultEngine.getUtils().calculateTotalLandRefund(faction.getLandRounded())) : Config.showMinimal ? null : Lang.ECON_OFF.format("refund");
            case BANK_BALANCE:
                if (VaultEngine.getUtils().shouldBeUsed()) {
                    return Config.bankEnabled ? VaultEngine.getUtils().moneyString(VaultEngine.getUtils().getBalance(faction.getAccountId())) : Config.showMinimal ? null : Lang.ECON_OFF.format("balance");
                }
                return Config.showMinimal ? null : Lang.ECON_OFF.format("balance");
            case ALLIES_COUNT:
                return String.valueOf(faction.getRelationCount(Relation.ALLY));
            case TRUCES_COUNT:
                return String.valueOf(faction.getRelationCount(Relation.TRUCE));
            case ENEMIES_COUNT:
                return String.valueOf(faction.getRelationCount(Relation.ENEMY));
            case ONLINE_COUNT:
                return String.valueOf(faction.getOnlinePlayers().size());
            case OFFLINE_COUNT:
                return String.valueOf(faction.getMembers().size() - faction.getOnlinePlayers().size());
            case FACTION_SIZE:
                return String.valueOf(faction.getMembers().size());
            case FACTION_KILLS:
                return String.valueOf(faction.getKills());
            case FACTION_DEATHS:
                return String.valueOf(faction.getDeaths());
            case EMBLEM:
                return faction.getEmblem();
            default:
            	return null;
        }
    }

    /**
     * Returns a list of all the variables we can use for this type<br>
     *
     * @param type the type we want
     *
     * @return a list of all the variables with this type
     */
    protected static List<TagReplacerUtil> getByType(TagType type) {
        List<TagReplacerUtil> tagReplacers = new ArrayList<TagReplacerUtil>();
        for (TagReplacerUtil tagReplacer : TagReplacerUtil.values()) {
            if (type == TagType.FANCY) {
                if (tagReplacer.type == TagType.FANCY) {
                    tagReplacers.add(tagReplacer);
                }
            } else if (tagReplacer.type.id >= type.id) {
                tagReplacers.add(tagReplacer);
            }
        }
        return tagReplacers;
    }

    /**
     * @param original raw line with variables
     * @param value    what to replace var in raw line with
     *
     * @return the string with the new value
     */
    public String replace(String original, String value) {
        return original.replace(tag, value);
    }

    /**
     * @param toSearch raw line with variables
     *
     * @return if the raw line contains this enums variable
     */
    public boolean contains(String toSearch) {
        return toSearch.contains(tag);
    }

    /**
     * Gets the tag associated with this enum that we should replace
     *
     * @return the {....} variable that is located in config
     */
    public String getTag() {
        return this.tag;
    }
}
