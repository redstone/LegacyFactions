package net.redstoneore.legacyfactions.announcement;

import net.redstoneore.legacyfactions.entity.FPlayer;

public class Announcement {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	public Announcement(FPlayer fplayer, String announcement) {
		this.fplayer = fplayer;
		this.announcement = announcement;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	transient private FPlayer fplayer;
	transient private String announcement;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Get the announcer of this announcement.
	 * @return The announcer of this announcement.
	 */
	public FPlayer getAnnouncer() {
		return this.fplayer;
	}
	
	/**
	 * Get the announcement.
	 * @return The announcement.
	 */
	public String getAnnouncement() {
		return this.announcement;
	}
	
}
