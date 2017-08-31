package net.redstoneore.legacyfactions.flag;

import java.util.Optional;

import net.redstoneore.legacyfactions.callback.CallbackSync;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.expansion.Provider;
import net.redstoneore.legacyfactions.util.ConditionalBoolean;

public class Flag {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static Flag of(String name, Boolean defaultValue, Provider provider) {
		return new Flag(name, defaultValue, provider);
	}
	
	public static Flag of(String name, ConditionalBoolean defaultValue, Provider provider) {
		return new Flag(name, defaultValue, provider);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private Flag(String name, Boolean defaultValue, Provider provider) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.provider = provider;
	}
	
	private Flag(String name, ConditionalBoolean defaultValue, Provider provider) {
		this.name = name;
		this.conditionalDefaultValue = defaultValue;
		this.provider = provider;
	}
	
	private Flag(String name, Boolean defaultValue, Provider provider, CallbackSync<Boolean, FPlayer> validator) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.provider = provider;
		this.validator = validator;
	}
	
	private Flag(String name, ConditionalBoolean defaultValue, Provider provider, CallbackSync<Boolean, FPlayer> validator) {
		this.name = name;
		this.conditionalDefaultValue = defaultValue;
		this.provider = provider;
		this.validator = validator;
	}

	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private final String name;
	private boolean defaultValue;
	private ConditionalBoolean conditionalDefaultValue = null;
	private final Provider provider;
	private CallbackSync<Boolean, FPlayer> validator = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	/**
	 * Returns the name of this flag.
	 * @return the name of this flag.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the default value of this flag.
	 * @return the default value of this flag.
	 */
	public boolean getDefaultValue() {
		if (this.conditionalDefaultValue == null) {
			return this.defaultValue;
		}
		return this.conditionalDefaultValue.get();
	}
	
	/**
	 * Returns the provider of this flag.
	 * @return the provider of this flag.
	 */
	public Provider getProvider() {
		return this.provider;
	}
	
	/**
	 * Returns a validator for this flag, if there is one.
	 * @return a validator for this flag.
	 */
	public Optional<CallbackSync<Boolean, FPlayer>> validator() {
		if (this.validator == null) return Optional.empty();
		return Optional.of(this.validator);
	}
	
	/**
	 * This method returns a unique stored name, based on the provider.
	 * @return a unique name used in storage.
	 */
	public String getStoredName() {
		return this.provider.getUniqueName() + "_" + this.getName();
	}
	
	@Override
	public String toString() {
		return this.getStoredName();
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Flag)) return false;
		Flag flag = (Flag) object;
		return (flag.getStoredName().equalsIgnoreCase(this.getStoredName()));
	}
}
