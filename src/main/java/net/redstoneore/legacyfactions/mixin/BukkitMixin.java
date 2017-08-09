package net.redstoneore.legacyfactions.mixin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import net.redstoneore.legacyfactions.util.UUIDUtil;

public class BukkitMixin {

	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(Object object) {
		if (object instanceof UUID) {
			return Bukkit.getOfflinePlayer((UUID) object);
		}
		if (object instanceof String) {
			if (UUIDUtil.isUUID((String) object)) {
				UUID uuid = UUID.fromString((String) object);
				return Bukkit.getOfflinePlayer(uuid);
			}
			return Bukkit.getOfflinePlayer((String) object);
		}
		
		return null;
	}
}
