package net.redstoneore.legacyfactions.util;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.RelationParticipator;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.flag.Flags;

public class RelationUtil {

    public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst) {
        String ret = "";

        Faction thatFaction = getFaction(that);
        if (thatFaction == null) {
            return "ERROR"; // ERROR
        }

        Faction myFaction = getFaction(me);
//		if (myFaction == null) return that.describeTo(null); // no relation, but can show basic name or tag

        if (that instanceof Faction) {
            if (me instanceof FPlayer && myFaction == thatFaction) {
                ret = Lang.GENERIC_YOURFACTION.toString();
            } else {
                ret = thatFaction.getTag();
            }
        } else if (that instanceof FPlayer) {
            FPlayer fplayerthat = (FPlayer) that;
            if (that == me) {
                ret = Lang.GENERIC_YOU.toString();
            } else if (thatFaction == myFaction) {
                ret = fplayerthat.getNameAndTitle();
            } else {
                ret = fplayerthat.getNameAndTag();
            }
        }

        if (ucfirst) {
            ret = TextUtil.upperCaseFirst(ret);
        }

        return "" + getColorOfThatToMe(that, me) + ret;
    }

    public static String describeThatToMe(RelationParticipator that, RelationParticipator me) {
        return describeThatToMe(that, me, false);
    }

    public static Relation getRelationTo(RelationParticipator me, RelationParticipator that) {
        return getRelationTo(that, me, false);
    }

    public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful) {
        Faction fthat = getFaction(that);
        if (fthat == null) {
            return Relation.NEUTRAL; // ERROR
        }

        Faction fme = getFaction(me);
        if (fme == null) {
            return Relation.NEUTRAL; // ERROR
        }

        if (!fthat.isNormal() || !fme.isNormal()) {
            return Relation.NEUTRAL;
        }

        if (fthat.equals(fme)) {
            return Relation.MEMBER;
        }

        if (!ignorePeaceful && (fme.getFlag(Flags.PEACEFUL) || fthat.getFlag(Flags.PEACEFUL))) {
            return Relation.NEUTRAL;
        }

        if (fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value) {
            return fthat.getRelationWish(fme);
        }

        return fme.getRelationWish(fthat);
    }

    public static Faction getFaction(RelationParticipator rp) {
        if (rp instanceof Faction) {
            return (Faction) rp;
        }

        if (rp instanceof FPlayer) {
            return ((FPlayer) rp).getFaction();
        }

        // ERROR
        return null;
    }

    public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me) {
        Faction thatFaction = getFaction(that);
        if (thatFaction != null) {
            if (thatFaction.getFlag(Flags.PEACEFUL) && thatFaction != getFaction(me)) {
                return Conf.colorPeaceful.toColor();
            }

            if (thatFaction.isSafeZone() && thatFaction != getFaction(me)) {
                return FactionColl.get().getSafeZone().getForcedMapColour();
            }

            if (thatFaction.isWarZone() && thatFaction != getFaction(me)) {
                return FactionColl.get().getWarZone().getForcedMapColour();
            }
        }

        return getRelationTo(that, me).getColor();
    }
}
