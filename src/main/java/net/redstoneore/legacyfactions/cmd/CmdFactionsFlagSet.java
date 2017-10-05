package net.redstoneore.legacyfactions.cmd;

import java.util.Optional;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.flag.Flag;
import net.redstoneore.legacyfactions.flag.Flags;
import net.redstoneore.legacyfactions.lang.Lang;

public class CmdFactionsFlagSet extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsFlagSet instance = new CmdFactionsFlagSet();
	public static CmdFactionsFlagSet get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// ----------------------------------------s---------- //

	private CmdFactionsFlagSet() {
		this.aliases.addAll(CommandAliases.cmdAliasesFlagSet);
		
		this.requiredArgs.add("flag");
		this.requiredArgs.add("on/off");
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.FLAG_SET.getNode();
		
		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void perform() {
		Optional<Flag> oFlag = Flags.get(this.argAsString(0));
		if (!oFlag.isPresent()) {
			this.sendMessage(Lang.COMMAND_FLAGSET_INVALID.getBuilder().parse().replace("<flag>", this.argAsString(0)).toString());
			return;
		}
		
		Flag flag = oFlag.get();
		
		boolean value = this.argAsBool(1);
		
		Faction forFaction = this.argAsFaction(2, this.myFaction, true);
		if (forFaction == null) return;
		
		if (!this.fme.isAdminBypassing()) {
			if (forFaction != this.myFaction && !Permission.FLAG_SET_ANY.has(sender, false)) {
				this.sendMessage(Lang.COMMAND_FLAGSET_NOTYOURS.getBuilder().parse().toString());
				return;
			} else {
				if (!this.fme.getRole().isAtLeast(Config.factionFlagMinRole)) {
					this.sendMessage(Lang.COMMAND_FLAGSET_BADRANK.getBuilder().parse().replace("<rank>", Config.factionFlagMinRole.toNiceName()).toString());
					return;
				}
			}
			
			if (flag.validator().isPresent() && !flag.validator().get().get(this.fme)) {
				return;
			}
		}
		
		forFaction.setFlag(flag, value);
		
		Lang.COMMAND_FLAGSET_SET.getBuilder()
			.parse()
			.replace("<flag>",flag.getName())
			.replace("<value>", value)
			.sendTo(this.fme);
		
	}
	
	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_FLAGSET_DESCRIPTION.getBuilder().parse().toString();
	}
	
}
