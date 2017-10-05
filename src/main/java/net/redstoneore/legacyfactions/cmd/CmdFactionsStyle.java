package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsStyle extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsStyle instance = new CmdFactionsStyle();
	public static CmdFactionsStyle get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsStyle() {
		this.aliases.addAll(CommandAliases.cmdAliasesStyle);

		this.requiredArgs.add(Lang.COMMAND_STYLE_ARG_CHARACTER.toString() + "|" + Lang.COMMAND_STYLE_ARG_COLOUR.toString());
		this.requiredArgs.add(Lang.COMMAND_STYLE_ARG_VALUE.toString());
		this.optionalArgs.put("faction", "yours");
		
		this.permission = Permission.STYLE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		Faction targetFaction = this.argAsFaction(2, this.myFaction, true);
		
		if (targetFaction != this.myFaction && !Permission.STYLE_ANY.has(this.sender, true)) {
			return;
		}
		
		switch (this.argAsString(0).toLowerCase()) {
		case "character":
		case "char":
			Character character = null;
			character = this.argAsString(1).charAt(0);
			
			targetFaction.setForcedMapCharacter(character);
			
			this.sendMessage(TextUtil.parseColor(Lang.COMMAND_STYLE_CHARACTERUPDATED.toString().replace("<character>", character.toString())));
			break;
			
		case "colour":
		case "color":
			ChatColor colour;
			try {
				colour = ChatColor.valueOf(this.argAsString(1));
			} catch (Exception e) {
				this.sendMessage(TextUtil.parseColor(Lang.COMMAND_STYLE_INVALIDCOLOUR.toString()));
				return;
			}
			
			targetFaction.setForcedMapColour(colour);
			
			this.sendMessage(TextUtil.parseColor(Lang.COMMAND_STYLE_COLOURUPDATED.toString().replace("<colour>", colour.name())));
			break;
			
		default:
			this.sendMessage(TextUtil.parseColor(Lang.COMMAND_STYLE_INVALIDSTYLE.toString()));
			break;
		}
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_STYLE_DESCRIPTION.toString();
	}

}
