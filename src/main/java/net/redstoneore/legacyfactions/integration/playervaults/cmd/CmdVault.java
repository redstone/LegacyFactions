package net.redstoneore.legacyfactions.integration.playervaults.cmd;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.UUIDVaultManager;
import com.drtshock.playervaults.vaultmanagement.VaultOperations;
import com.drtshock.playervaults.vaultmanagement.VaultViewInfo;

import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.cmd.FCommand;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.lang.Lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CmdVault extends FCommand {

    public CmdVault() {
        this.aliases.add("vault");

        //this.requiredArgs.add("");
        this.optionalArgs.put("number", "number");

        this.permission = Permission.VAULT.getNode();
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void perform() {
        /*
             /f vault <number>
         */

        int number = argAsInt(0, 0); // Default to 0 or show on 0

        Player player = me;

        if (PlayerVaults.getInstance().getInVault().containsKey(player.getUniqueId().toString())) {
            return; // Already in a vault so they must be trying to dupe.
        }

        int max = myFaction.getMaxVaults();
        if (number > max) {
            me.sendMessage(Lang.COMMAND_VAULT_TOOHIGH.format(number, max));
            return;
        }

        // Something like faction-id
        String vaultName = String.format(Config.vaultPrefix, myFaction.getId());

        if (number < 1) {
            // Message about which vaults that Faction has.
            // List the target
            YamlConfiguration file = UUIDVaultManager.getInstance().getPlayerVaultFile(vaultName);
            if (file == null) {
                sender.sendMessage(Lang.TITLE.toString() + com.drtshock.playervaults.util.Lang.VAULT_DOES_NOT_EXIST.toString());
            } else {
                StringBuilder sb = new StringBuilder();
                for (String key : file.getKeys(false)) {
                    sb.append(key.replace("vault", "")).append(" ");
                }

                sender.sendMessage(Lang.TITLE.toString() + com.drtshock.playervaults.util.Lang.EXISTING_VAULTS.toString().replaceAll("%p", fme.getTag()).replaceAll("%v", sb.toString().trim()));
            }
            return;
        } // end listing vaults.

        // Attempt to open vault.
        if (VaultOperations.openOtherVault(player, vaultName, String.valueOf(number))) {
            // Success
            PlayerVaults.getInstance().getInVault().put(player.getUniqueId().toString(), new VaultViewInfo(vaultName, number));
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_VAULT_DESCRIPTION.toString();
    }
}
