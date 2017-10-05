package net.redstoneore.legacyfactions.expansion.chat;

import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.lang.Lang;

/**
 * An enum of available chat modes for faction chat.
 */
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
	// STATIC METHODS
	// -------------------------------------------------- // 
	
	public static void forEach(Consumer<? super ChatMode> action) {
		Lists.newArrayList(values()).forEach(action);
	}
	
	public static Stream<ChatMode> stream() {
		return Lists.newArrayList(values()).stream();
	}
	
	public static Stream<ChatMode> parallelStream() {
		return Lists.newArrayList(values()).parallelStream();
	}
	
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

	/**
	 * Compare this role to another role, and see if this role is at least the other role. 
	 * @param role
	 * @return
	 */
	public boolean isAtLeast(ChatMode role) {
		return this.value >= role.value;
	}

	/**
	 * Compare this role to another role, and see if it is a most the other role. 
	 * @param role to compare
	 * @return true if at most 
	 */
	public boolean isAtMost(ChatMode role) {
		return this.value <= role.value;
	}
	
	/**
	 * Check if two roles are the same.
	 * @param role to compare
	 * @return true if the roles are the same
	 */
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
				return TRUCE;
			case TRUCE:
				return FACTION;
			default:
				return PUBLIC;
		}
	}
	
}
