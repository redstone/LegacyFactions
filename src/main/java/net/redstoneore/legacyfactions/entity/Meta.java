package net.redstoneore.legacyfactions.entity;

import java.util.List;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.entity.persist.Persist;

public class Meta {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static transient Meta instance = new Meta();
	public static Meta get() { return instance; }
	
	// -------------------------------------------------- //
	// WARNINGS
	// -------------------------------------------------- //
	
	public List<String> _warning = Lists.newArrayList(
			"",
			"################################  WARNING  ################################",
			"|                                                                         |",
			"| THIS FILE CONTAINS SENSITIVE INFORMATION. DO NOT SHARE THIS FILE WITH   |", 
			"| ANYONE UNLESS YOU KNOW THE CONSEQUENCE OF DOING SO. NOT LEGACYFACTIONS  |",
			"| DEVELOPERS, NOT MOJANG, NOT EVEN YOUR SIGNIFICANT OTHER!                |",
			"|                                                                         |",
			"################################  WARNING  ################################",
			""
	);
	public List<String> _stop = Lists.newArrayList(
			"",
			"################################  STOP  ################################",
			"|                                                                      |",
			"| This is the meta file, do not change things in here unless you know  |", 
			"| what you are doing. Otherwise, your server could break. You should   |",
			"| not need to modify this file.                                        |",
			"|                                                                      |",
			"################################  STOP  ################################",
			"",
			"",
			""
	);
	
	// -------------------------------------------------- //
	// CONFIG VERSION
	// -------------------------------------------------- //

	public List<String> _configVersion = Lists.newArrayList(
			"",
			"################################  CONFIG VERSION  ################################",
			"|                                                                                |",
			"| This variable is used for migrating your configuration file. Changing it could |", 
			"| break your server setup, revert variables, or break your database.             |", 
			"|                                                                                |",
			"################################  CONFIG VERSION  ################################",
			""
	);
	
	public double configVersion = Conf.version;
	
	// -------------------------------------------------- //
	// DATABASE VERSION
	// -------------------------------------------------- //
	
	public List<String> _database = Lists.newArrayList(
			"",
			"################################  DATABASE  ################################",
			"|                                                                          |",
			"| You should use in-game commands to change these.                         |",
			"| Please note these variables are encrypted at runtime.                    |", 
			"| Set 'databaseCredentialsEncrypted' to 'false' if you change them here.   |",
			"| You should use the /f convert command in-game.                           |",
			"| If databaseHost is blank we simply don't encrypt these variables.        |",
			"|                                                                          |",
			"################################  DATABASE  ################################",
			""
	);
	
	public String databaseHost = "";
	public String databaseUsername = "";
	public String databasePassword = "";
	public String databaseName = "";
	public int databaseConnectonMax = 12;
	public boolean databaseCredentialsEncrypted = false;
	
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
