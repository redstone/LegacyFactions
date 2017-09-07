package net.redstoneore.legacyfactions.entity;

import net.redstoneore.legacyfactions.entity.persist.Persist;

public class Meta {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static transient Meta instance = new Meta();
	public static Meta get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	public double configVersion = Conf.version;
	
	// Database credentials
	public String databaseHost = "";
	public String databaseUsername = "";
	public String databasePassword = "";
	public String databaseName = "";
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	public void load() {
		Persist.get().loadOrSaveDefault(instance, Meta.class, "database/meta");
	}

	public void save() {
		Persist.get().save(instance, "database/meta");
	}
	
}
