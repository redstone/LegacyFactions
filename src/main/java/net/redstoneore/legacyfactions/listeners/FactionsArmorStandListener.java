package net.redstoneore.legacyfactions.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import net.redstoneore.legacyfactions.LandAction;
import net.redstoneore.legacyfactions.mixin.PlayerMixin;
import net.redstoneore.legacyfactions.util.LocationUtil;

public class FactionsArmorStandListener implements AbstractConditionalListener {

	// -------------------------------------------------- //
	// INSTANCE 
	// -------------------------------------------------- //
	
	private static FactionsArmorStandListener instance = new FactionsArmorStandListener();
	public static FactionsArmorStandListener get() { return instance; }
	
	// -------------------------------------------------- //
	// ARMOR STAND PROTECTION
	// -------------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerInteractEntity(PlayerInteractAtEntityEvent event) {
		if (LocationUtil.isFactionsDisableIn(event)) return;
		
		Entity rightClicked = event.getRightClicked();
		
		if (rightClicked.getType() != EntityType.ARMOR_STAND) return;
		
		if (PlayerMixin.canDoAction(event.getPlayer(), rightClicked, LandAction.ENTITY, false)) return;
		
		event.setCancelled(true);
	}

	@Override
	public boolean shouldEnable() {
		try {
			Class.forName("org.bukkit.event.player.PlayerInteractAtEntityEvent");
			return true;
		} catch(Throwable e) {
			return false;
		}
	}
	
}
