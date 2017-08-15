package net.redstoneore.legacyfactions.cmd;

import java.util.Collections;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Conf;

/**
 * Base command for Factions, and child commands if you want them to be used in the factions command.
 */
public class CmdFactions extends FCommand {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- // 
	
	private static CmdFactions instance = new CmdFactions();
	public static CmdFactions get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- // 

	private CmdFactions() {
		this.aliases.addAll(Conf.baseCommandAliases);
		this.aliases.removeAll(Collections.<String>singletonList(null));  // remove any nulls from extra commas
		this.allowNoSlashAccess = Conf.allowNoSlashCommand;
		
		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeAdmin = false;

		this.disableOnLock = false;

		this.setHelpShort("The faction base command");
		this.helpLong.add(Factions.get().getTextUtil().parseTags("<i>This command contains all faction stuff."));
		
		this.addSubCommand(CmdFactionsAdmin.get());
		this.addSubCommand(CmdFactionsAutoclaim.get());
		this.addSubCommand(CmdFactionsBoom.get());
		this.addSubCommand(CmdFactionsBypass.get());
		this.addSubCommand(CmdFactionsClaim.get());
		this.addSubCommand(CmdFactionsClaimLine.get());
		this.addSubCommand(CmdFactionsColeader.get());
		this.addSubCommand(CmdFactionsConfig.get());
		this.addSubCommand(CmdFactionsCreate.get());
		this.addSubCommand(CmdFactionsDebug.get());
		this.addSubCommand(CmdFactionsDeinvite.get());
		this.addSubCommand(CmdFactionsDescription.get());
		this.addSubCommand(CmdFactionsDisband.get());
		this.addSubCommand(CmdFactionsHelp.get());
		this.addSubCommand(CmdFactionsHome.get());
		this.addSubCommand(CmdFactionsInvite.get());
		this.addSubCommand(CmdFactionsJoin.get());
		this.addSubCommand(CmdFactionsKick.get());
		this.addSubCommand(CmdFactionsLeave.get());
		this.addSubCommand(CmdFactionsList.get());
		this.addSubCommand(CmdFactionsLock.get());
		this.addSubCommand(CmdFactionsMap.get());
		this.addSubCommand(CmdFactionsMod.get());
		this.addSubCommand(CmdFactionsMoney.get());
		this.addSubCommand(CmdFactionsOpen.get());
		this.addSubCommand(CmdFactionsOwner.get());
		this.addSubCommand(CmdFactionsOwnerList.get());
		this.addSubCommand(CmdFactionsPeaceful.get());
		this.addSubCommand(CmdFactionsPermanent.get());
		this.addSubCommand(CmdFactionsPermanentPower.get());
		this.addSubCommand(CmdFactionsPower.get());
		this.addSubCommand(CmdFactionsPowerBoost.get());
		this.addSubCommand(CmdFactionsRelationAlly.get());
		this.addSubCommand(CmdFactionsRelationTruce.get());
		this.addSubCommand(CmdFactionsRelationEnemy.get());
		this.addSubCommand(CmdFactionsRelationNeutral.get());
		this.addSubCommand(CmdFactionsReload.get());
		this.addSubCommand(CmdFactionsSafeunclaimall.get());
		this.addSubCommand(CmdFactionsSaveAll.get());
		this.addSubCommand(CmdFactionsSethome.get());
		this.addSubCommand(CmdFactionsShow.get());
		this.addSubCommand(CmdFactionsStatus.get());
		this.addSubCommand(CmdFactionsStuck.get());
		this.addSubCommand(CmdFactionsStyle.get());
		this.addSubCommand(CmdFactionsTag.get());
		this.addSubCommand(CmdFactionsTitle.get());
		this.addSubCommand(CmdFactionsUnclaim.get());
		this.addSubCommand(CmdFactionsUnclaimall.get());
		this.addSubCommand(CmdFactionsVersion.get());
		this.addSubCommand(CmdFactionsWarunclaimall.get());
		this.addSubCommand(CmdFactionsScoreboard.get());
		this.addSubCommand(CmdFactionsShowInvites.get());
		this.addSubCommand(CmdFactionsAnnounce.get());
		this.addSubCommand(CmdFactionsSeeChunk.get());
		this.addSubCommand(CmdFactionsConvert.get());
		this.addSubCommand(CmdFactionsWarp.get());
		this.addSubCommand(CmdFactionsSetwarp.get());
		this.addSubCommand(CmdFactionsDelwarp.get());
		this.addSubCommand(CmdFactionsModifyPower.get());
		this.addSubCommand(CmdFactionsLogins.get());
		this.addSubCommand(CmdFactionsTop.get());
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 

	@Override
	public void perform() {
		this.commandChain.add(this);
		CmdFactionsHelp.get().execute(this.sender, this.args, this.commandChain);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.GENERIC_PLACEHOLDER.toString();
	}
	
	// -------------------------------------------------- //
	// DEPRECATED FIELDS
	// -------------------------------------------------- // 
	// These fields were used previously but are now linked to the static methods. These will be
	// removed at some point in the future. But to maximise backwards compatibility they will stay
	// as fields here for now.
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsAdmin#get}
	 */
	@Deprecated
	public CmdFactionsAdmin cmdAdmin = CmdFactionsAdmin.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsAutoclaim#get}
	 */
	@Deprecated
	public CmdFactionsAutoclaim cmdAutoClaim = CmdFactionsAutoclaim.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsBoom#get}
	 */
	@Deprecated
	public CmdFactionsBoom cmdBoom = CmdFactionsBoom.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsBypass#get}
	 */
	@Deprecated
	public CmdFactionsBypass cmdBypass = CmdFactionsBypass.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsClaim#get}
	 */
	@Deprecated
	public CmdFactionsClaim cmdClaim = CmdFactionsClaim.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsColeader#get}
	 */
	@Deprecated
	public CmdFactionsColeader cmdColeader = CmdFactionsColeader.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsConfig#get}
	 */
	@Deprecated
	public CmdFactionsConfig cmdConfig = CmdFactionsConfig.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsCreate#get}
	 */
	@Deprecated
	public CmdFactionsCreate cmdCreate = CmdFactionsCreate.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsDeinvite#get}
	 */
	@Deprecated
	public CmdFactionsDeinvite cmdDeinvite = CmdFactionsDeinvite.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsDescription#get}
	 */
	@Deprecated
	public CmdFactionsDescription cmdDescription = CmdFactionsDescription.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsDisband#get}
	 */
	@Deprecated
	public CmdFactionsDisband cmdDisband = CmdFactionsDisband.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsHome#get}
	 */
	@Deprecated
	public CmdFactionsHome cmdHome = CmdFactionsHome.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsInvite#get}
	 */
	@Deprecated
	public CmdFactionsInvite cmdInvite = CmdFactionsInvite.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsJoin#get}
	 */
	@Deprecated
	public CmdFactionsJoin cmdJoin = CmdFactionsJoin.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsKick#get}
	 */
	@Deprecated
	public CmdFactionsKick cmdKick = CmdFactionsKick.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsLeave#get}
	 */
	@Deprecated
	public CmdFactionsLeave cmdLeave = CmdFactionsLeave.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsList#get}
	 */
	@Deprecated
	public CmdFactionsList cmdList = CmdFactionsList.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsLock#get}
	 */
	@Deprecated
	public CmdFactionsLock cmdLock = CmdFactionsLock.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsMap#get}
	 */
	@Deprecated
	public CmdFactionsMap cmdMap = CmdFactionsMap.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsMod#get}
	 */
	@Deprecated
	public CmdFactionsMod cmdMod = CmdFactionsMod.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsAnnounce#get}
	 */
	@Deprecated
	public CmdFactionsAnnounce cmdAnnounce = CmdFactionsAnnounce.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsConvert#get}
	 */
	@Deprecated
	public CmdFactionsConvert cmdConvert = CmdFactionsConvert.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsDelwarp#get}
	 */
	@Deprecated
	public CmdFactionsDelwarp cmdDelFWarp = CmdFactionsDelwarp.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsModifyPower#get}
	 */
	@Deprecated
	public CmdFactionsModifyPower cmdModifyPower = CmdFactionsModifyPower.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsLogins#get}
	 */
	@Deprecated
	public CmdFactionsLogins cmdLogins = CmdFactionsLogins.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsClaimLine#get}
	 */
	@Deprecated
	public CmdFactionsClaimLine cmdClaimLine = CmdFactionsClaimLine.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsMoney#get}
	 */
	@Deprecated
	public CmdFactionsMoney cmdMoney = CmdFactionsMoney.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsOpen#get}
	 */
	@Deprecated
	public CmdFactionsOpen cmdOpen = CmdFactionsOpen.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsOwner#get}
	 */
	@Deprecated
	public CmdFactionsOwner cmdOwner = CmdFactionsOwner.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsOwnerList#get}
	 */
	@Deprecated
	public CmdFactionsOwnerList cmdOwnerList = CmdFactionsOwnerList.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsPeaceful#get}
	 */
	@Deprecated
	public CmdFactionsPeaceful cmdPeaceful = CmdFactionsPeaceful.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsPermanent#get}
	 */
	@Deprecated
	public CmdFactionsPermanent cmdPermanent = CmdFactionsPermanent.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsPermanentPower#get}
	 */
	@Deprecated
	public CmdFactionsPermanentPower cmdPermanentPower = CmdFactionsPermanentPower.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsPowerBoost#get}
	 */
	@Deprecated
	public CmdFactionsPowerBoost cmdPowerBoost = CmdFactionsPowerBoost.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsPower#get}
	 */
	@Deprecated
	public CmdFactionsPower cmdPower = CmdFactionsPower.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsRelationAlly#get}
	 */
	@Deprecated
	public CmdFactionsRelationAlly cmdRelationAlly = CmdFactionsRelationAlly.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsRelationTruce#get}
	 */
	@Deprecated
	public CmdFactionsRelationTruce cmdRelationTruce = CmdFactionsRelationTruce.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsRelationEnemy#get}
	 */
	@Deprecated
	public CmdFactionsRelationEnemy cmdRelationEnemy = CmdFactionsRelationEnemy.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsRelationNeutral#get}
	 */
	@Deprecated
	public CmdFactionsRelationNeutral cmdRelationNeutral = CmdFactionsRelationNeutral.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsHelp#get}!
	 */
	@Deprecated
	public CmdFactionsHelp cmdHelp = CmdFactionsHelp.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsSetwarp#get}
	 */
	@Deprecated
	public CmdFactionsSetwarp cmdSetFWarp = CmdFactionsSetwarp.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsWarunclaimall#get}
	 */
	@Deprecated
	public CmdFactionsWarunclaimall cmdWarunclaimall = CmdFactionsWarunclaimall.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsScoreboard#get}
	 */
	@Deprecated
	public CmdFactionsScoreboard cmdSB = CmdFactionsScoreboard.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsShowInvites#get}
	 */
	@Deprecated
	public CmdFactionsShowInvites cmdShowInvites = CmdFactionsShowInvites.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsSeeChunk#get}
	 */
	@Deprecated
	public CmdFactionsSeeChunk cmdSeeChunk = CmdFactionsSeeChunk.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsWarp#get}
	 */
	@Deprecated
	public CmdFactionsWarp cmdFWarp = CmdFactionsWarp.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsUnclaimall#get}
	 */
	@Deprecated
	public CmdFactionsUnclaimall cmdUnclaimall = CmdFactionsUnclaimall.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsVersion#get}
	 */
	@Deprecated
	public CmdFactionsVersion cmdVersion = CmdFactionsVersion.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsUnclaim#get}
	 */
	@Deprecated
	public CmdFactionsUnclaim cmdUnclaim = CmdFactionsUnclaim.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsTitle#get}
	 */
	@Deprecated
	public CmdFactionsTitle cmdTitle = CmdFactionsTitle.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsTag#get}
	 */
	@Deprecated
	public CmdFactionsTag cmdTag = CmdFactionsTag.get();

	/**
	 * Deprecated, now using the static method {@link CmdFactionsStuck#get}
	 */
	@Deprecated
	public CmdFactionsStuck cmdStuck = CmdFactionsStuck.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsReload#get}
	 */
	@Deprecated
	public CmdFactionsReload cmdReload = CmdFactionsReload.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsSafeunclaimall#get}
	 */
	@Deprecated
	public CmdFactionsSafeunclaimall cmdSafeunclaimall = CmdFactionsSafeunclaimall.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsSaveAll#get}
	 */
	@Deprecated
	public CmdFactionsSaveAll cmdSaveAll = CmdFactionsSaveAll.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsSethome#get}
	 */
	@Deprecated
	public CmdFactionsSethome cmdSethome = CmdFactionsSethome.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsShow#get}
	 */
	@Deprecated
	public CmdFactionsShow cmdShow = CmdFactionsShow.get();
	
	/**
	 * Deprecated, now using the static method {@link CmdFactionsStatus#get}
	 */
	@Deprecated
	public CmdFactionsStatus cmdStatus = CmdFactionsStatus.get();

}
