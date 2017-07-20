package net.redstoneore.legacyfactions;

import org.bukkit.ChatColor;

/**
 * Interface given to entities have relationships 
 */
public interface RelationParticipator {

    public String describe();
    
    public String describeTo(RelationParticipator that);

    public String describeTo(RelationParticipator that, boolean ucfirst);

    public Relation getRelationTo(RelationParticipator that);

    public Relation getRelationTo(RelationParticipator that, boolean ignorePeaceful);

    public ChatColor getColorTo(RelationParticipator to);
    
}
