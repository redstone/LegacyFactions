package net.redstoneore.legacyfactions.cmd;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.Lang;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;


public class CmdFactionsMap extends FCommand {

    public CmdFactionsMap() {
        this.aliases.addAll(Conf.cmdAliasesMap);

        this.optionalArgs.put("on/off", "once");

        this.permission = Permission.MAP.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.argIsSet(0)) {
            if (this.argAsBool(0, !fme.isMapAutoUpdating())) {
                // Turn on

                // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
                if (!payForCommand(Conf.econCostMap, "to show the map", "for showing the map")) {
                    return;
                }

                fme.setMapAutoUpdating(true);
                msg(Lang.COMMAND_MAP_UPDATE_ENABLED);

                // And show the map once
                showMap();
            } else {
                // Turn off
                fme.setMapAutoUpdating(false);
                msg(Lang.COMMAND_MAP_UPDATE_DISABLED);
            }
        } else {
            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!payForCommand(Conf.econCostMap, Lang.COMMAND_MAP_TOSHOW, Lang.COMMAND_MAP_FORSHOW)) {
                return;
            }

            showMap();
        }
    }

    public void showMap() {
        sendMessage(Board.get().getMap(myFaction, new FLocation(fme), fme.getPlayer().getLocation().getYaw()));
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_MAP_DESCRIPTION.toString();
    }

}
