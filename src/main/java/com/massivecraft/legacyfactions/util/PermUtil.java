package com.massivecraft.legacyfactions.util;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.massivecraft.legacyfactions.FactionsPluginBase;
import com.massivecraft.legacyfactions.TL;

import java.util.HashMap;
import java.util.Map;

public class PermUtil {

    public Map<String, String> permissionDescriptions = new HashMap<String, String>();

    protected FactionsPluginBase p;

    public PermUtil(FactionsPluginBase p) {
        this.p = p;
        this.setup();
    }

    public String getForbiddenMessage(String perm) {
        return p.txt.parse(TL.GENERIC_NOPERMISSION.toString(), getPermissionDescription(perm));
    }

    /**
     * This method hooks into all permission plugins we are supporting
     */
    public final void setup() {
        for (Permission permission : p.getDescription().getPermissions()) {
            //p.log("\""+permission.getName()+"\" = \""+permission.getDescription()+"\"");
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String perm) {
        String desc = permissionDescriptions.get(perm);
        if (desc == null) {
            return TL.GENERIC_DOTHAT.toString();
        }
        return desc;
    }

    /**
     * This method tests if me has a certain permission and returns true if me has. Otherwise false
     */
    public boolean has(CommandSender me, String perm) {
        if (me == null) {
            return false;
        }

        return me.hasPermission(perm);
    }

    public boolean has(CommandSender me, String perm, boolean informSenderIfNot) {
        if (has(me, perm)) {
            return true;
        } else if (informSenderIfNot && me != null) {
            me.sendMessage(this.getForbiddenMessage(perm));
        }
        return false;
    }

}
