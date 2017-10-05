package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.MiscUtil;

public class CmdFactionsEmblem extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsEmblem instance = new CmdFactionsEmblem();
	public static CmdFactionsEmblem get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsEmblem() {
		this.aliases.addAll(CommandAliases.cmdAliasesEmblem);

		this.requiredArgs.add("emblem");
		this.optionalArgs.put("faction", "me");
		
		this.permission = Permission.EMBLEM.getNode();
		this.disableOnLock = true;
		
		this.senderMustBeMember = true;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		String emblem = this.argAsString(0);
		
		// Ensure we're over the minimum, if it is set
		if (Config.emblemsMinLength > 0 && emblem.length() < Config.emblemsMinLength) {
			Lang.COMMAND_EMBLEM_LENGTHUNDERMIN.getBuilder()
				.parse()
				.replace("<minimum>", Config.emblemsMinLength)
				.sendTo(this.fme);
			
			return;
		}
		
		// Ensure we're under the maximum, if it is set
		if (Config.emblemsMaxLength > 0 && emblem.length() > Config.emblemsMaxLength) {
			Lang.COMMAND_EMBLEM_LENGTHOVERMAX.getBuilder()
				.parse()
				.replace("<maximum>", Config.emblemsMaxLength)
				.sendTo(this.fme);
			
			return;
		}
		
		// Default to our own faction
		Faction factionFor = this.fme.getFaction();
		
		// If we've specified another faction, set it to that.
		if (this.argIsSet(1)) {
			factionFor = this.argAsFaction(1, null, true);
			if (factionFor == null) return;
		}
		
		// If we're not bypassing we will check over some things
		if (!this.fme.isAdminBypassing()) {
			// Ensure we have permission to set emblems for other factions.
			if (factionFor != this.fme.getFaction() && !Permission.EMBLEM_OTHERS.has(this.sender)) {
				
				Lang.COMMAND_EMBLEM_CANTOTHERS.getBuilder()
					.parse()
					.sendTo(this.fme);
				
				return;
			} else {
				// Ensure our role is at least the minimum role required.
				if (!this.fme.getRole().isAtLeast(Config.emblemsMinRole)) {
					Lang.COMMAND_EMBLEM_MINROLE.getBuilder()
						.replace("<role>", Config.emblemsMinRole.toNiceName())
						.sendTo(this.fme);
					return;
				}
			}
		}
		
		// Make sure its not taken.
		if (MiscUtil.isEmblemTaken(emblem)) {
			Lang.COMMAND_EMBLEM_TAKEN.getBuilder()
				.parse()
				.sendTo(this.fme);
			
			return;
		}
		
		// Set it and notify the command sender
		factionFor.setEmblem(emblem);
		
		Lang.COMMAND_EMBLEM_SET.getBuilder()
			.parse()
			.replace("<emblem>", emblem)
			.sendTo(this.fme);
	}

	@Override
	public boolean isAvailable() {
		return Config.emblemsEnabled == true && super.isAvailable();
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_EMBLEM_DESCRIPTION.toString();
	}

}
