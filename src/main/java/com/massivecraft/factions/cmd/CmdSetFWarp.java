package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.Relation;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.util.TL;

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
