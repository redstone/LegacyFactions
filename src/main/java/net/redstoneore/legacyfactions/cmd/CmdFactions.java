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
	
	/**
	 * Get the base command instance.
	 * @return CmdFactions instance
	 */
	public static CmdFactions get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 

	// TODO: singletons..
	public CmdFactionsAdmin cmdAdmin = new CmdFactionsAdmin();
	public CmdFactionsAutoclaim cmdAutoClaim = new CmdFactionsAutoclaim();
	public CmdFactionsBoom cmdBoom = new CmdFactionsBoom();
	public CmdFactionsBypass cmdBypass = new CmdFactionsBypass();
	public CmdFactionsChat cmdChat = new CmdFactionsChat();
	public CmdFactionsChatspy cmdChatSpy = new CmdFactionsChatspy();
	public CmdFactionsClaim cmdClaim = new CmdFactionsClaim();
	public CmdFactionsColeader cmdColeader = new CmdFactionsColeader();
	public CmdFactionsConfig cmdConfig = new CmdFactionsConfig();
	public CmdFactionsCreate cmdCreate = new CmdFactionsCreate();
	public CmdFactionsDeinvite cmdDeinvite = new CmdFactionsDeinvite();
	public CmdFactionsDescription cmdDescription = new CmdFactionsDescription();
	public CmdFactionsDisband cmdDisband = new CmdFactionsDisband();
	public CmdFactionsHelp cmdHelp = new CmdFactionsHelp();
	public CmdFactionsHome cmdHome = new CmdFactionsHome();
	public CmdFactionsInvite cmdInvite = new CmdFactionsInvite();
	public CmdFactionsJoin cmdJoin = new CmdFactionsJoin();
	public CmdFactionsKick cmdKick = new CmdFactionsKick();
	public CmdFactionsLeave cmdLeave = new CmdFactionsLeave();
	public CmdFactionsList cmdList = new CmdFactionsList();
	public CmdFactionsLock cmdLock = new CmdFactionsLock();
	public CmdFactionsMap cmdMap = new CmdFactionsMap();
	public CmdFactionsMod cmdMod = new CmdFactionsMod();
	public CmdFactionsMoney cmdMoney = new CmdFactionsMoney();
	public CmdFactionsOpen cmdOpen = new CmdFactionsOpen();
	public CmdFactionsOwner cmdOwner = new CmdFactionsOwner();
	public CmdFactionsOwnerList cmdOwnerList = new CmdFactionsOwnerList();
	public CmdFactionsPeaceful cmdPeaceful = new CmdFactionsPeaceful();
	public CmdFactionsPermanent cmdPermanent = new CmdFactionsPermanent();
	public CmdFactionsPermanentPower cmdPermanentPower = new CmdFactionsPermanentPower();
	public CmdFactionsPowerBoost cmdPowerBoost = new CmdFactionsPowerBoost();
	public CmdFactionsPower cmdPower = new CmdFactionsPower();
	public CmdFactionsRelationAlly cmdRelationAlly = new CmdFactionsRelationAlly();
	public CmdFactionsRelationTruce cmdRelationTruce = new CmdFactionsRelationTruce();
	public CmdFactionsRelationEnemy cmdRelationEnemy = new CmdFactionsRelationEnemy();
	public CmdFactionsRelationNeutral cmdRelationNeutral = new CmdFactionsRelationNeutral();
	public CmdFactionsReload cmdReload = new CmdFactionsReload();
	public CmdFactionsSafeunclaimall cmdSafeunclaimall = new CmdFactionsSafeunclaimall();
	public CmdFactionsSaveAll cmdSaveAll = new CmdFactionsSaveAll();
	public CmdFactionsSethome cmdSethome = new CmdFactionsSethome();
	public CmdFactionsShow cmdShow = new CmdFactionsShow();
	public CmdFactionsStatus cmdStatus = new CmdFactionsStatus();
	public CmdFactionsStuck cmdStuck = new CmdFactionsStuck();
	public CmdFactionsTag cmdTag = new CmdFactionsTag();
	public CmdFactionsTitle cmdTitle = new CmdFactionsTitle();
	public CmdFactionsToggleAllianceChat cmdToggleAllianceChat = new CmdFactionsToggleAllianceChat();
	public CmdFactionsUnclaim cmdUnclaim = new CmdFactionsUnclaim();
	public CmdFactionsUnclaimall cmdUnclaimall = new CmdFactionsUnclaimall();
	public CmdFactionsVersion cmdVersion = new CmdFactionsVersion();
	public CmdFactionsWarunclaimall cmdWarunclaimall = new CmdFactionsWarunclaimall();
	public CmdFactionsScoreboard cmdSB = new CmdFactionsScoreboard();
	public CmdFactionsShowInvites cmdShowInvites = new CmdFactionsShowInvites();
	public CmdFactionsAnnounce cmdAnnounce = new CmdFactionsAnnounce();
	public CmdFactionsSeeChunk cmdSeeChunk = new CmdFactionsSeeChunk();
	public CmdFactionsConvert cmdConvert = new CmdFactionsConvert();
	public CmdFactionsWarp cmdFWarp = new CmdFactionsWarp();
	public CmdFactionsSetwarp cmdSetFWarp = new CmdFactionsSetwarp();
	public CmdFactionsDelwarp cmdDelFWarp = new CmdFactionsDelwarp();
	public CmdFactionsModifyPower cmdModifyPower = new CmdFactionsModifyPower();
	public CmdFactionsLogins cmdLogins = new CmdFactionsLogins();
	public CmdFactionsClaimLine cmdClaimLine = new CmdFactionsClaimLine();
	
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
		
		this.addSubCommand(this.cmdAdmin);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBoom);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdChat);
		this.addSubCommand(this.cmdToggleAllianceChat);
		this.addSubCommand(this.cmdChatSpy);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdColeader);
		this.addSubCommand(this.cmdConfig);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(CmdFactionsDebug.get());
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDescription);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdHome);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdMod);
		this.addSubCommand(this.cmdMoney);
		this.addSubCommand(this.cmdOpen);
		this.addSubCommand(this.cmdOwner);
		this.addSubCommand(this.cmdOwnerList);
		this.addSubCommand(this.cmdPeaceful);
		this.addSubCommand(this.cmdPermanent);
		this.addSubCommand(this.cmdPermanentPower);
		this.addSubCommand(this.cmdPower);
		this.addSubCommand(this.cmdPowerBoost);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationTruce);
		this.addSubCommand(this.cmdRelationEnemy);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSafeunclaimall);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdSethome);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdStatus);
		this.addSubCommand(this.cmdStuck);
		this.addSubCommand(this.cmdTag);
		this.addSubCommand(this.cmdTitle);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
		this.addSubCommand(this.cmdWarunclaimall);
		this.addSubCommand(this.cmdSB);
		this.addSubCommand(this.cmdShowInvites);
		this.addSubCommand(this.cmdAnnounce);
		this.addSubCommand(this.cmdSeeChunk);
		this.addSubCommand(this.cmdConvert);
		this.addSubCommand(this.cmdFWarp);
		this.addSubCommand(this.cmdSetFWarp);
		this.addSubCommand(this.cmdDelFWarp);
		this.addSubCommand(this.cmdModifyPower);
		this.addSubCommand(this.cmdLogins);
		this.addSubCommand(this.cmdClaimLine);
		this.addSubCommand(CmdFactionsTop.get());
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 

	@Override
	public void perform() {
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

	@Override
	public String getUsageTranslation() {
		return Lang.GENERIC_PLACEHOLDER.toString();
	}

}
