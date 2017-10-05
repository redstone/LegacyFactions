package net.redstoneore.legacyfactions;

import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.lang.Lang;

public enum Role {
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	ADMIN(3, Lang.ROLE_ADMIN),
	COLEADER(2, Lang.ROLE_COLEADER),
	MODERATOR(1, Lang.ROLE_MODERATOR),
	NORMAL(0, Lang.ROLE_NORMAL);

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final int value;
	private final Lang nicename;
	private final Lang translation;

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private Role(final int value, final Lang translation) {
		this.value = value;
		this.nicename = translation;
		this.translation = translation;
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public int getValue() {
		return this.value;
	}
	
	public String toNiceName() {
		return this.nicename.toString();
	}

	public Lang getTranslation(){
		return this.translation;
	}

	public String getPrefix() {
		if (this == Role.ADMIN) {
			return Config.playerPrefixAdmin;
		}
		
		if (this == Role.COLEADER) {
			return Config.playerPrefixColeader;
		}

		if (this == Role.MODERATOR) {
			return Config.playerPrefixMod;
		}

		return "";
	}
	
	public boolean isAtLeast(Role role) {
		return this.value >= role.value;
	}

	public boolean isAtMost(Role role) {
		return this.value <= role.value;
	}
	
	public boolean is(Role role) {
		return role == this;
	}
	
	public boolean isLessThan(Role role) {
		return this.getValue() < role.getValue();
	}
	
	public boolean isMoreThan(Role role) {
		return this.getValue() > role.getValue();
	}
	
}
