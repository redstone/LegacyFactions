package com.massivecraft.factionsuuid.cmd;

import com.massivecraft.factionsuuid.Factions;
import com.massivecraft.factionsuuid.Permission;
import com.massivecraft.factionsuuid.Relation;
import com.massivecraft.factionsuuid.TL;
import com.massivecraft.factionsuuid.entity.FPlayer;
import com.massivecraft.factionsuuid.util.LazyLocation;

public class CmdSetFWarp extends FCommand {

    public CmdSetFWarp() {
        super();
        this.aliases.add("setwarp");
        this.aliases.add("sw");
        this.requiredArgs.add("warp name");
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
            fme.msg(TL.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }

        int maxWarps = Factions.get().getConfig().getInt("max-warps", 5);
        if (maxWarps <= myFaction.getWarps().size()) {
            fme.msg(TL.COMMAND_SETFWARP_LIMIT, maxWarps);
            return;
        }

        if (!transact(fme)) {
            return;
        }

        String warp = argAsString(0);
        LazyLocation loc = new LazyLocation(fme.getPlayer().getLocation());
        myFaction.setWarp(warp, loc);
        fme.msg(TL.COMMAND_SETFWARP_SET, warp);
    }

    private boolean transact(FPlayer player) {
        return !Factions.get().getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || payForCommand(Factions.get().getConfig().getDouble("warp-cost.setwarp", 5), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}
