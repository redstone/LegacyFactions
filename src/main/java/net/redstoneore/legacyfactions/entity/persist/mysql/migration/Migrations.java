package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class Migrations {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static Migrations instance = new Migrations();
	public static Migrations get() { return instance; }
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private Migrations() {
		this.add(
			LFMigration10001.create(),
			LFMigration10002.create(),
			LFMigration10003.create(),
			LFMigration10004.create(),
			LFMigration10005.create(),
			LFMigration10006.create(),
			LFMigration10007.create(),
			LFMigration10008.create(),
			LFMigration10009.create(),
			LFMigration10010.create()
		);
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //

	private Set<Migration> migrations = new LinkedHashSet<>();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	private void prepare() {
		String query = "CREATE TABLE IF NOT EXISTS `migrations` (\n" + 
				"  `migration_id` bigint(20) DEFAULT NULL,\n" + 
				"  `migration_date` bigint(20) DEFAULT NULL\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
	/**
	 * Add migrations 
	 * @param migrations Migrations to add
	 * @return {@link Migrations}, for chaining.
	 */
	public Migrations add(Migration...migrations) {
		for (Migration migration : migrations) {
			if (migration != null || !this.migrations.contains(migration)) {
				this.migrations.add(migration);
			}
		}
		return this;
	}
	
	public void up() {
		this.prepare();
		
		this.migrations.forEach(migration -> {
			String query = "SELECT * FROM migrations WHERE migration_id = ?";
			List<Map<String, String>> results = FactionsMySQL.get().prepare(query).setCatched(1, migration.migrationId()).execute(ExecuteType.SELECT);
			if (results == null || results.isEmpty()) {
				migration.preUp();
			}
		});
	}
	
	public void down() {
		this.prepare();
		
		List<Migration> allMigrations = new ArrayList<>(this.migrations);
		Collections.reverse(allMigrations);
		
		allMigrations.forEach(Migration::down);
	}
	
}
