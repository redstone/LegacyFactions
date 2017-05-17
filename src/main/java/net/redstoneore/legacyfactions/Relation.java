package net.redstoneore.legacyfactions;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.entity.Conf;

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
	 * @param string
	 * @return relation
	 */
	public static Relation fromString(String str) {
		switch (str.toLowerCase()) {
		case "member":
			return MEMBER;
		case "ally":
			return ALLY;
		case "truce":
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
	 * Returns true if they are at least a relation. Order of enemy -> member
	 * @return true if is at least
	 */
	public boolean isAtLeast(Relation relation) {
		return this.value >= relation.value;
	}

	/**
	 * Returns true if they are at most a relation. Order of enemy -> member
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
			return Conf.colorMember;
		} else if (this == ALLY) {
			return Conf.colorAlly;
		} else if (this == NEUTRAL) {
			return Conf.colorNeutral;
		} else if (this == TRUCE) {
			return Conf.colorTruce;
		} else {
			return Conf.colorEnemy;
		}
	}

	/** 
	 * return appropriate Conf setting for DenyBuild based on this relation and their online status
	 * @param online
	 * @return appropriate Conf setting for DenyBuild based on this relation and their online status
	 */
	public Boolean confDenyBuild(boolean online) {
		if (isMember()) {
			return false;
		}

		if (online) {
			if (isEnemy()) {
				return Conf.territoryEnemyDenyBuild;
			} else if (isAlly()) {
				return Conf.territoryAllyDenyBuild;
			} else if (isTruce()) {
				return Conf.territoryTruceDenyBuild;
			} else {
				return Conf.territoryDenyBuild;
			}
		} else {
			if (isEnemy()) {
				return Conf.territoryEnemyDenyBuildWhenOffline;
			} else if (isAlly()) {
				return Conf.territoryAllyDenyBuildWhenOffline;
			} else if (isTruce()) {
				return Conf.territoryTruceDenyBuildWhenOffline;
			} else {
				return Conf.territoryDenyBuildWhenOffline;
			}
		}
	}

	/**
	 * return appropriate Conf setting for PainBuild based on this relation and their online status
	 * @param online
	 * @return appropriate Conf setting for PainBuild based on this relation and their online status
	 */
	public Boolean confPainBuild(boolean online) {
		if (isMember()) {
			return false;
		}

		if (online) {
			if (isEnemy()) {
				return Conf.territoryEnemyPainBuild;
			} else if (isAlly()) {
				return Conf.territoryAllyPainBuild;
			} else if (isTruce()) {
				return Conf.territoryTrucePainBuild;
			} else {
				return Conf.territoryPainBuild;
			}
		} else {
			if (isEnemy()) {
				return Conf.territoryEnemyPainBuildWhenOffline;
			} else if (isAlly()) {
				return Conf.territoryAllyPainBuildWhenOffline;
			} else if (isTruce()) {
				return Conf.territoryTrucePainBuildWhenOffline;
			} else {
				return Conf.territoryPainBuildWhenOffline;
			}
		}
	}

	/**
	 * return appropriate Conf setting for DenyUseage based on this relation
	 * @return appropriate Conf setting for DenyUseage based on this relation
	 */
	public Boolean confDenyUseage() {
		if (isMember()) return false;
		if (isEnemy()) return Conf.territoryEnemyDenyUseage;
		if (isAlly()) return Conf.territoryAllyDenyUseage;
		if (isTruce()) return Conf.territoryTruceDenyUseage;
		return Conf.territoryDenyUseage;
	}
	
	/**
	 * Get cost for this relation
	 * @return cost of this relation
	 */
	public Double getRelationCost() {
		if (this.isEnemy()) return Conf.econCostEnemy;
		if (isAlly()) return Conf.econCostAlly;
		if (isTruce()) return Conf.econCostTruce;
		return Conf.econCostNeutral;
	}
	
}
