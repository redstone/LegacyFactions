package net.redstoneore.legacyfactions.integration.conquer;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import me.andrew28.addons.conquer.api.EventWrapperListener;
import me.andrew28.addons.conquer.api.events.ConquerFactionCreateEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionDisbandEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionLandClaimEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionLandUnclaimEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionPlayerJoinEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionPlayerLeaveEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionRelationChangeEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionRenameEvent;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsCreate;
import net.redstoneore.legacyfactions.event.EventFactionsDisband;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;
import net.redstoneore.legacyfactions.event.EventFactionsNameChange;
import net.redstoneore.legacyfactions.event.EventFactionsRelationChange;
import net.redstoneore.legacyfactions.integration.conquer.impl.ConquerClaimImpl;
import net.redstoneore.legacyfactions.integration.conquer.impl.ConquerFactionImpl;
import net.redstoneore.legacyfactions.locality.Locality;

/**
 * This wrapper passes the inbuilt LegacyFactions events to the Conquer plugin.
 */
public class ConquerWrapperListener extends EventWrapperListener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static ConquerWrapperListener instance = new ConquerWrapperListener();
	public static ConquerWrapperListener get() { return instance; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@EventHandler
	public void onFactionCreate(EventFactionsCreate eventFactionsCreate){
		Event event = new ConquerFactionCreateEvent(eventFactionsCreate.getFPlayer().getPlayer(), eventFactionsCreate.getFactionTag());
		this.throwEvent(event);
		if (((Cancellable) event).isCancelled()) {
			eventFactionsCreate.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFactionDisband(EventFactionsDisband eventFactionsDisband) {
		Event event = new ConquerFactionDisbandEvent(ConquerFactionImpl.get(eventFactionsDisband.getFaction()), eventFactionsDisband.getFPlayer().getPlayer());
		this.throwEvent(event);
		if (((Cancellable) event).isCancelled()){
			eventFactionsDisband.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLandChange(EventFactionsLandChange eventFactionsLandChange) {
		Iterator<Entry<Locality, Faction>> it = eventFactionsLandChange.transactionsIterator();
		
		while (it.hasNext()) {
			Entry<Locality, Faction> entry = it.next();
			Event event = null;
			switch (eventFactionsLandChange.getCause()) {
			case Claim:
				event = 	new ConquerFactionLandClaimEvent(ConquerFactionImpl.get(entry.getValue()), eventFactionsLandChange.getFPlayer().getPlayer(), new ConquerClaimImpl(entry.getKey()));
				break;
			case Unclaim:
				event = 	new ConquerFactionLandUnclaimEvent(ConquerFactionImpl.get(entry.getValue()), eventFactionsLandChange.getFPlayer().getPlayer(), new ConquerClaimImpl(entry.getKey()));
				break;
			}
			
			if (event == null) continue;
			
			this.throwEvent(event);
			if (((Cancellable) event).isCancelled()){
				it.remove();
			}
		}
	}
	
	@EventHandler
	public void onFactionsChange(EventFactionsChange eventFactionsChange) {
		Event event = null;
		
		if (eventFactionsChange.getFactionNew().isWilderness()) {
			ConquerFactionPlayerLeaveEvent.LeaveReason leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.OTHER;
			switch (eventFactionsChange.getReason()) {
			case DISBAND:
				leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.DISBAND;
				break;
			case JOINOTHER:
				leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.JOINOTHER;
				break;
			case LEADER:
			case LEAVE:
				leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.LEAVE;
				break;
			case RESET:
				leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.RESET;
				break;
			default:
				leaveReason = ConquerFactionPlayerLeaveEvent.LeaveReason.KICKED;
				break;
			}
			
			event = new ConquerFactionPlayerLeaveEvent(ConquerFactionImpl.get(eventFactionsChange.getFactionOld()), eventFactionsChange.getFPlayer().getPlayer(), leaveReason);
		} else {
			ConquerFactionPlayerJoinEvent.JoinReason joinReason = ConquerFactionPlayerJoinEvent.JoinReason.OTHER;
			switch (eventFactionsChange.getReason()) {
			case COMMAND:
				joinReason = ConquerFactionPlayerJoinEvent.JoinReason.COMMAND;
				break;
			case CREATE:
				joinReason = ConquerFactionPlayerJoinEvent.JoinReason.CREATE;
				break;
			case LEADER:
				joinReason = ConquerFactionPlayerJoinEvent.JoinReason.LEADER;
				break;
			default:
				joinReason = ConquerFactionPlayerJoinEvent.JoinReason.OTHER;
				break;
			}
			
			event = new ConquerFactionPlayerJoinEvent(ConquerFactionImpl.get(eventFactionsChange.getFactionNew()), eventFactionsChange.getFPlayer().getPlayer(), joinReason);
		}
		
		this.throwEvent(event);
		if (((Cancellable) event).isCancelled()){
			eventFactionsChange.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFactionRename(EventFactionsNameChange eventFactionsNameChange) {
		Event event = new ConquerFactionRenameEvent(ConquerFactionImpl.get(eventFactionsNameChange.getFaction()), eventFactionsNameChange.getfPlayer().getPlayer(), eventFactionsNameChange.getFaction().getTag(), eventFactionsNameChange.getFactionTag());
		this.throwEvent(event);
		if (((Cancellable) event).isCancelled()){
			eventFactionsNameChange.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFactionRelationChange(EventFactionsRelationChange eventFactionsRelationChange) {
		ConquerFactionRelationChangeEvent.Relation relationOld = ConquerFactionRelationChangeEvent.Relation.OTHER;
		ConquerFactionRelationChangeEvent.Relation relationNew = ConquerFactionRelationChangeEvent.Relation.OTHER;
		
		try {
			relationOld = ConquerFactionRelationChangeEvent.Relation.valueOf(eventFactionsRelationChange.getCurrentRelation().name());
		} catch (Exception e) {
			
		}
		
		try {
			relationNew = ConquerFactionRelationChangeEvent.Relation.valueOf(eventFactionsRelationChange.getTargetRelation().name());
		} catch (Exception e) {
			
		}
		
		Event event = new ConquerFactionRelationChangeEvent(ConquerFactionImpl.get(eventFactionsRelationChange.getFaction()), ConquerFactionImpl.get(eventFactionsRelationChange.getTargetFaction()), relationOld, relationNew);
		this.throwEvent(event);
		if (((Cancellable) event).isCancelled()){
			eventFactionsRelationChange.setCancelled(true);
		}
	}
	
}
