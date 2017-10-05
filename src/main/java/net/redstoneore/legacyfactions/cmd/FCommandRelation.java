package net.redstoneore.legacyfactions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsRelation;
import net.redstoneore.legacyfactions.event.EventFactionsRelationChange;
import net.redstoneore.legacyfactions.event.EventFactionsRelationshipsCapped;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.scoreboards.FTeamWrapper;

public abstract class FCommandRelation extends FCommand {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public FCommandRelation() {
		this.requiredArgs.add("faction");

		this.permission = Permission.RELATION.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected Relation targetRelation = null;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		Faction them = this.argAsFaction(0);
		if (them == null) {
			this.sendMessage(Lang.COMMAND_RELATIONS_INVALID_TARGET.toString());
			return;
		}
		
		// Ensure normal
		if (!them.isNormal()) {
			sendMessage(Lang.COMMAND_RELATIONS_INVALID_NOTNORMAL.toString());
			return;
		}
		
		// Can't set relation to self
		if (them == this.myFaction) {
			sendMessage(Lang.COMMAND_RELATIONS_INVALID_SELF.toString());
			return;
		}
		
		// Check for existing relationship
		if (this.myFaction.getRelationWish(them) == targetRelation) {
			sendMessage(Lang.COMMAND_RELATIONS_INVALID_ALREADYINRELATIONSHIP.toString(), them.getTag());
			return;
		}

		// Check for max relations, do this silently until after the event
		if (this.myFaction.hasMaxRelations(them, targetRelation, true)) {
			// Call our event
			if (!EventFactionsRelationshipsCapped.create(this.fme, this.myFaction, targetRelation).call().isCancelled()) {
				// Now we notify them
				this.myFaction.hasMaxRelations(them, targetRelation, false);
				return;
			}
		}
		
		Relation oldRelation = this.myFaction.getRelationTo(them, true);
		
		EventFactionsRelationChange event = new EventFactionsRelationChange(this.fme, this.myFaction, them, oldRelation, this.targetRelation);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		// If economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!this.payForCommand(targetRelation.getRelationCost(), Lang.COMMAND_RELATIONS_TOMARRY, Lang.COMMAND_RELATIONS_FORMARRY)) {
			return;
		}

		// Try to set the new relation
		this.myFaction.setRelationWish(them, targetRelation);
		Relation currentRelation = this.myFaction.getRelationTo(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();

		// if the relation change was successful
		if (targetRelation.value == currentRelation.value) {
			// trigger the faction relation event
			EventFactionsRelation relationEvent = new EventFactionsRelation(this.myFaction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.sendMessage(Lang.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + this.myFaction.getTag());
			this.myFaction.sendMessage(Lang.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + them.getTag());
		} else {
			// inform the other faction of your request
			them.sendMessage(Lang.COMMAND_RELATIONS_PROPOSAL_1, currentRelationColor + this.myFaction.getTag(), targetRelation.getColor() + targetRelation.getTranslation());
			them.sendMessage(Lang.COMMAND_RELATIONS_PROPOSAL_2, CommandAliases.baseCommandAliases.get(0), targetRelation, this.myFaction.getTag());
			this.myFaction.sendMessage(Lang.COMMAND_RELATIONS_PROPOSAL_SENT, currentRelationColor + them.getTag(), "" + targetRelation.getColor() + targetRelation);
		}

		if (!targetRelation.isNeutral() && them.getFlag(Flags.PEACEFUL)) {
			them.sendMessage(Lang.COMMAND_RELATIONS_PEACEFUL);
			this.myFaction.sendMessage(Lang.COMMAND_RELATIONS_PEACEFULOTHER);
		}

		if (!targetRelation.isNeutral() && myFaction.getFlag(Flags.PEACEFUL)) {
			them.sendMessage(Lang.COMMAND_RELATIONS_PEACEFULOTHER);
			this.myFaction.sendMessage(Lang.COMMAND_RELATIONS_PEACEFUL);
		}

		FTeamWrapper.updatePrefixes(myFaction);
		FTeamWrapper.updatePrefixes(them);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_RELATIONS_DESCRIPTION.toString();
	}
	
}
