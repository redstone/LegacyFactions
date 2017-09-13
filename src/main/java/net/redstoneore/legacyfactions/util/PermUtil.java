package net.redstoneore.legacyfactions.util;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Lang;

import java.util.HashMap;
import java.util.Map;

public class PermUtil {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static PermUtil instance = null;
	public static PermUtil get() {
		if (instance == null) {
			instance = new PermUtil();
		}
		return instance;
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

    private PermUtil() {
        this.setup();
    }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

    public Map<String, String> permissionDescriptions = new HashMap<String, String>();

    
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

    public String getForbiddenMessage(String perm) {
        return TextUtil.get().parse(Lang.GENERIC_NOPERMISSION.toString(), getPermissionDescription(perm));
    }

    /**
     * This method hooks into all permission plugins we are supporting
     */
    public final void setup() {
        for (Permission permission : Factions.get().getDescription().getPermissions()) {
            //p.log("\""+permission.getName()+"\" = \""+permission.getDescription()+"\"");
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String perm) {
        String desc = permissionDescriptions.get(perm);
        if (desc == null) {
            return Lang.GENERIC_DOTHAT.toString();
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
