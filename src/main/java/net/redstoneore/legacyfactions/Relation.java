package net.redstoneore.legacyfactions;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.lang.Lang;

/**
 * Each faction has a relation to each other which can change how they interact.<br>
 *  - Member: they are a member to the faction<br>
 *  - Ally: they are an ally to the faction<br>
 *  - Truce: the factions are in a truce<br>
 *  - Neutral: default, there is no set relationship<br>
 *  - Enemy: these factions are enemies<br>
 *  <br>
 * Based on configuration, each relationship has a different result.<br>
 */
public enum Relation {
	
	// -------------------------------------------------- //
	// ENUN
	// -------------------------------------------------- //
	
	MEMBER(4, "member"),
	ALLY(3, "ally"),
	TRUCE(2, "truce"),
	NEUTRAL(1, "neutral"),
	ENEMY(0, "enemy"),
	;

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private Relation(final int value, final String nicename) {
		this.value = value;
		this.nicename = nicename;
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	public final int value;
	public final String nicename;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the nice name
	 * @return the nice name
	 */
	public String toNiceName() {
		return this.nicename;
	}
	
	/**
	 * Convert a string to a Relation object, defaults to NEUTRAL if unknown
	 * @param str Relation string
	 * @return relation
	 */
	public static Relation fromString(String str) {
		switch (str.toLowerCase()) {
		case "member":
			return MEMBER;
		case "ally":
			return ALLY;
		case "truce":
			if (!Config.enableTruces) {
				return NEUTRAL;
			}
			return TRUCE;
		case "enemy":
			return ENEMY;
		default:
			return NEUTRAL;
		}
	}
	
	/**
	 * Get the translation value for this relation
	 * @return translated value
	 */
	public String getTranslation() {
		try {
			return Lang.valueOf("RELATION_" + name() + "_SINGULAR").toString();
		} catch (IllegalArgumentException e) {
			return toString();
		}
	}

	/**
	 * Get the plural translation value for this relation
	 * @return translated value
	 */

	public String getPluralTranslation() {
		for (Lang t : Lang.values()) {
			if (t.name().equalsIgnoreCase("RELATION_" + name() + "_PLURAL")) {
				return t.toString();
			}
		}
		return toString();
	}

	/**
	 * Returns true if is member
	 * @return true if is member
	 */
	public boolean isMember() {
		return this == MEMBER;
	}

	/**
	 * Returns true if is ally
	 * @return true if is ally
	 */
	public boolean isAlly() {
		return this == ALLY;
	}

	/**
	 * Returns true if is truce
	 * @return true if is truce
	 */

	public boolean isTruce() {
		return this == TRUCE;
	}

	/**
	 * Returns true if is neutral
	 * @return true if is neutral
	 */
	public boolean isNeutral() {
		return this == NEUTRAL;
	}

	/**
	 * Returns true if is enemy
	 * @return true if is enemy
	 */
	public boolean isEnemy() {
		return this == ENEMY;
	}

	/**
	 * Returns true if they are at least a relation. Order of enemy to member
	 * @return true if is at least
	 */
	public boolean isAtLeast(Relation relation) {
		return this.value >= relation.value;
	}
	
	/**
	 * Returns true if they are at most a relation. Order of enemy to member
	 * @return true if is at most
	 */
	public boolean isAtMost(Relation relation) {
		return this.value <= relation.value;
	}

	/**
	 * Returns the colour for this relation
	 * @return ChatColor for this relation 
	 */
	public ChatColor getColor() {
		if (this == MEMBER) {
			return Config.colorMember.toColor();
		} else if (this == ALLY) {
			return Config.colorAlly.toColor();
		} else if (this == NEUTRAL) {
			return Config.colorNeutral.toColor();
		} else if (this == TRUCE) {
			return Config.colorTruce.toColor();
		} else {
			return Config.colorEnemy.toColor();
		}
	}

	/** 
	 * return appropriate Conf setting for DenyBuild based on this relation and their online status
	 * @param online Status of target faction
	 * @return appropriate Conf setting for DenyBuild based on this relation and their online status
	 */
	public Boolean confDenyBuild(boolean online) {
		if (isMember()) {
			return false;
		}

		if (online) {
			if (isEnemy()) {
				return Config.territoryEnemyDenyBuild;
			} else if (isAlly()) {
				return Config.territoryAllyDenyBuild;
			} else if (isTruce()) {
				return Config.territoryTruceDenyBuild;
			} else {
				return Config.territoryDenyBuild;
			}
		} else {
			if (isEnemy()) {
				return Config.territoryEnemyDenyBuildWhenOffline;
			} else if (isAlly()) {
				return Config.territoryAllyDenyBuildWhenOffline;
			} else if (isTruce()) {
				return Config.territoryTruceDenyBuildWhenOffline;
			} else {
				return Config.territoryDenyBuildWhenOffline;
			}
		}
	}

	/**
	 * return appropriate Conf setting for PainBuild based on this relation and their online status
	 * @param online Online status of target faction
	 * @return appropriate Conf setting for PainBuild based on this relation and their online status
	 */
	public Boolean confPainBuild(boolean online) {
		if (isMember()) {
			return false;
		}

		if (online) {
			if (isEnemy()) {
				return Config.territoryEnemyPainBuild;
			} else if (isAlly()) {
				return Config.territoryAllyPainBuild;
			} else if (isTruce()) {
				return Config.territoryTrucePainBuild;
			} else {
				return Config.territoryPainBuild;
			}
		} else {
			if (isEnemy()) {
				return Config.territoryEnemyPainBuildWhenOffline;
			} else if (isAlly()) {
				return Config.territoryAllyPainBuildWhenOffline;
			} else if (isTruce()) {
				return Config.territoryTrucePainBuildWhenOffline;
			} else {
				return Config.territoryPainBuildWhenOffline;
			}
		}
	}

	/**
	 * return appropriate Conf setting for DenyUseage based on this relation
	 * @return appropriate Conf setting for DenyUseage based on this relation
	 */
	public Boolean confDenyUseage() {
		if (isMember()) return false;
		if (isEnemy()) return Config.territoryEnemyDenyUseage;
		if (isAlly()) return Config.territoryAllyDenyUseage;
		if (isTruce()) return Config.territoryTruceDenyUseage;
		return Config.territoryDenyUseage;
	}
	
	/**
	 * Get cost for this relation
	 * @return cost of this relation
	 */
	public Double getRelationCost() {
		if (this.isEnemy()) return Config.econCostEnemy;
		if (isAlly()) return Config.econCostAlly;
		if (isTruce()) return Config.econCostTruce;
		return Config.econCostNeutral;
	}
	
}
