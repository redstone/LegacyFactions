package net.redstoneore.legacyfactions.announcement;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Sets;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFPlayer;
import net.redstoneore.legacyfactions.entity.persist.shared.SharedFaction;

/**
 * This class provides a nice abstraction over announcements.
 *
 */
public class Announcements {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public Announcements(SharedFaction faction) {
		this.faction = faction;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private SharedFaction faction;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get all announcements. 
	 * @return All announcements.
	 */
	public List<Announcement> getAll() {
		List<Announcement> announcements = new ArrayList<>();
		
		this.faction.getAnnouncements().entrySet().stream()
			.forEach(entry -> {
				SharedFPlayer fplayer = (SharedFPlayer) FPlayerColl.get(entry.getKey());
				
				entry.getValue().forEach(announcement -> 
					announcements.add(new Announcement(fplayer, announcement))
				);
				
			});
		
		return announcements;
	}
	
	/**
	 * Get Faction these announcements are for.
	 * @return The faction.
	 */
	public Faction getFaction() {
		return this.faction;
	}
	
	/**
	 * Get all announcements for a player.
	 * @param fplayer FPlayer to get for.
	 * @return List of announcements. 
	 */
	public List<Announcement> get(FPlayer fplayer) {
		List<Announcement> playerAnnouncements = new ArrayList<>();
		if (!this.faction.getAnnouncements().containsKey(fplayer.getId())) {
			return playerAnnouncements;
		}
		
		Sets.newHashSet(this.faction.getAnnouncements().get(fplayer.getId())).forEach(announcement -> 
			new Announcement((SharedFPlayer) fplayer, announcement)
		);
		
		return playerAnnouncements;
	}
	
	/**
	 * Remove all announcements for a player.
	 * @param fplayer FPlayer to remove announcements for.
	 */
	public void remove(FPlayer fplayer) {
		this.faction.removeAnnouncements(fplayer);
	}
	
	/**
	 * Remove all announcements.
	 */
	public void removeAll() {
		this.faction.getAnnouncements().keySet().forEach(fplayer -> this.faction.memberRemove(FPlayerColl.get(fplayer)));
	}
	
	/**
	 * Add an announcement.
	 * @param fplayer FPlayer to add for.
	 * @param message The message to announce. 
	 */
	public void add(FPlayer fplayer, String message) {
		this.faction.addAnnouncement(fplayer, message);
	}
	
	/**
	 * Send unread announcements to a player.
	 * @param fplayer FPlayer to send to.
	 */
	public void sendUnread(FPlayer fplayer) {
		this.faction.sendUnreadAnnouncements(fplayer);
	}
	
}
