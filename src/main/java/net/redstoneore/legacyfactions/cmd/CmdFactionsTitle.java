package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.callback.Callback;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.lang.Lang;
import net.redstoneore.legacyfactions.util.TextUtil;

public class CmdFactionsTitle extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsTitle instance = new CmdFactionsTitle();
	public static CmdFactionsTitle get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsTitle() {
		this.aliases.addAll(CommandAliases.cmdAliasesTitle);

		this.requiredArgs.add("player name");
		this.optionalArgs.put("title", "");

		this.permission = Permission.TITLE.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = true;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public void perform() {
		List<String> titleItems = Lists.newArrayList(this.args);
		titleItems.remove(0);
		
		final String title = TextUtil.implode(titleItems, " ");
		
		FPlayer you = this.argAsBestFPlayerMatch(0, null, false);
		final FPlayer fplayer = this.fme;
		
		if (you == null) {
			this.argAsPlayerToMojangFPlayer(0, null, new Callback<FPlayer>() {
				@Override
				public void then(FPlayer you, Optional<Exception> exception) {
					if (exception.isPresent()) {
						exception.get().printStackTrace();
						return;
					}
					
					resume(fplayer, you, title);
				}
			});
			return;
		}

		resume(fplayer, you, title);
	}
	
	private static void resume(FPlayer fme, FPlayer you, String title) {
		if (!fme.canAdminister(you)) return;
		
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!fme.payForCommand(Config.econCostTitle, Lang.COMMAND_TITLE_TOCHANGE.toString(), Lang.COMMAND_TITLE_FORCHANGE.toString())) {
			return;
		}

		if (Config.allowColourCodesInFactionTitle) {
			title = TextUtil.parseColor(title);
		}
		
		you.setTitle(title);

		// Inform
		you.getFaction().sendMessage(Lang.COMMAND_TITLE_CHANGED, fme.describeTo(you.getFaction(), true), you.describeTo(fme.getFaction(), true));
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_TITLE_DESCRIPTION.toString();
	}

}
