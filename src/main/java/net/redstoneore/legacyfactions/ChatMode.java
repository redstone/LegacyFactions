package net.redstoneore.legacyfactions;

public enum ChatMode {
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- // 
	
	FACTION(3, Lang.CHAT_FACTION),
	ALLIANCE(2, Lang.CHAT_ALLIANCE),
	TRUCE(1, Lang.CHAT_TRUCE),
	PUBLIC(0, Lang.CHAT_PUBLIC),
	;

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- // 

	private ChatMode(final int value, final Lang niceName) {
		this.value = value;
		this.niceName = niceName;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- // 

	public final int value;
	public final Lang niceName;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 

	public boolean isAtLeast(ChatMode role) {
		return this.value >= role.value;
	}

	public boolean isAtMost(ChatMode role) {
		return this.value <= role.value;
	}
	
	public boolean is(ChatMode role) {
		return role == this;
	}

	/**
	 * Get the nice name of this chat mode, as per configuration
	 * @return String of the nice name
	 */
	public String getNiceName() {
		return this.niceName.toString();
	}

	/**
	 * Get the next chat mode
	 * @return ChatMode that is next
	 */
	public ChatMode getNext() {
		switch (this) {
			case PUBLIC:
				return ALLIANCE;
			case ALLIANCE:
				return FACTION;
			default:
				return PUBLIC;
		}
	}
	
}
