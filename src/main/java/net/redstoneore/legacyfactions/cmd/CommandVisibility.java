package net.redstoneore.legacyfactions.cmd;

/**
 * Commands can be specified with visibility options.
 */
public enum CommandVisibility {
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	/**
	 * Visible commands are visible to anyone. Even those who don't have permission to use it or is of invalid sender type.
	 */
	VISIBLE,
	
	/**
	 * Secret commands are visible only to those who can use the command. These commands are usually some kind of admin commands.
	 */
	SECRET,
	
	/**
	 * Invisible commands are invisible to everyone, even those who can use the command.
	 */
	INVISIBLE,
	
	;
	
}
