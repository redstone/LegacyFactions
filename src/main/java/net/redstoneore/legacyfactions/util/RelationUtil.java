package net.redstoneore.legacyfactions.util;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.flag.Flags;

/**
 * This utility class provides methods for working with relations.
 */
public class RelationUtil {
	
	/**
	 * Describes the a relation participatory to another relation participator. This prefixes the colour. 
	 * @param that Who is being described.
	 * @param me Who is being described to. 
	 * @param ucfirst Should we upper case the first letter?
	 * @return The relationship with the colour prefixed, or ERROR if it failed.
	 */
	public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst) {
		String result = "";
		
		if (that == null || that.getFaction() == null) {
			// No faction, something went wrong. We can't even get the basic name or tag.
			return "ERROR"; 
		}
		
		if (me == null || me.getFaction() == null) {
			// No relation, but can show basic name or tag
			return that.describeTo(null);
		}
		
		Faction factionThat = that.getFaction();
		Faction factionMe = me.getFaction();
		
		if (that instanceof Faction) {
			// Is a faction, get the faction tag
			if (me instanceof FPlayer && factionMe == factionThat) {
				// Is our own faction, return YOUR
				result = Lang.GENERIC_YOURFACTION.toString();
			} else {
				result = factionThat.getTag();
			}
		} else if (that instanceof FPlayer) {
			// Is a player
			FPlayer fplayerthat = (FPlayer) that;
			if (that == me) {
				// Is ourself, return YOU
				result = Lang.GENERIC_YOU.toString();
			} else if (factionThat == factionMe) {
				// Is our own faction, get name and title
				result = fplayerthat.getNameAndTitle();
			} else {
				// Is other faction, get name and tag
				result = fplayerthat.getNameAndTag();
			}
		}
		
		// Uppercase the first letter if required.
		if (ucfirst) {
			result = TextUtil.upperCaseFirst(result);
		}
		
		return getColorOfThatToMe(that, me) + result;
	}

	/**
 	 * Describes the a relation participatory to another relation participator. This prefixes the colour. 
	 * @param that Who is being described.
	 * @param me Who is being described to. 
	 * @return The relationship with the colour prefixed, or ERROR if it failed.
	 */
	public static String describeThatToMe(RelationParticipator that, RelationParticipator me) {
		return describeThatToMe(that, me, false);
	}

	/**
 	 * Get the relationship between me and that. 
	 * @param me Who is being described to. 
	 * @param that Who is being described.
	 * @return The relationship between the two. It will not ignore peaceful.
	 */
	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that) {
		return getRelationTo(that, me, false);
	}

	/**
 	 * Get the relationship between me and that. 
	 * @param me Who is being described to. 
	 * @param that Who is being described.
	 * @param ignorePeaceful Should we ignore peaceful?
	 * @return The relationship between the two. It will not ignore peaceful.
	 */
	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful) {
		Faction factionThat = that.getFaction();
		Faction factionMe = me.getFaction();
		
		if (factionThat == null || factionMe == null) {
			return Relation.NEUTRAL;
		}
		
		if (!factionThat.isNormal() || !factionMe.isNormal()) {
			return Relation.NEUTRAL;
		}

		if (factionThat.equals(factionMe)) {
			return Relation.MEMBER;
		}

		if (!ignorePeaceful && (factionMe.getFlag(Flags.PEACEFUL) || factionThat.getFlag(Flags.PEACEFUL))) {
			return Relation.NEUTRAL;
		}
		
		Relation factionMeWish = factionMe.getRelationWish(factionThat);
		Relation factionThatWish = factionMe.getRelationWish(factionMe);
		
		if (factionMeWish.isAtLeast(factionThatWish)) {
			return factionThatWish;
		}

		return factionMeWish;
	}
	
	/**
	 * Get colour of that relation between that and me.
	 * @param that Who is being described.
	 * @param me Who is being described to.
	 * @return The colour, or ERROR if it failed.
	 */
	public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me) {
		if (that != null && that.getFaction() != null) {
			if (that.getFaction().getFlag(Flags.PEACEFUL) && that.getFaction() != me.getFaction()) {
				return Config.colorPeaceful.toColor();
			}

			if (that.getFaction().isSafeZone() && that.getFaction() != me.getFaction()) {
				return FactionColl.get().getSafeZone().getForcedMapColour();
			}

			if (that.getFaction().isWarZone() && that.getFaction() != me.getFaction()) {
				return FactionColl.get().getWarZone().getForcedMapColour();
			}
		}

		return getRelationTo(that, me).getColor();
	}
	
	/**
	 * Deprecated. Use {@link RelationParticipator#getFaction()}
	 * @param participator
	 * @return
	 */
	@Deprecated
	public static Faction getFaction(RelationParticipator participator) {
		if (participator == null) {
			return null;
		}
		return participator.getFaction();
	}
	
}
