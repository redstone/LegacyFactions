package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

/**
 * This class provides the interface for database migrations.
 */
public interface Migration {

	/**
	 * Returns this unique migration id.
	 * @return The migration id. 
	 */
	long migrationId();
	
	/**
	 * Run the up operation.
	 */
	void up();
	
	/**
	 * Run the down operation.
	 */
	void down();
	
	/**
	 * Runs the up method along with some nice messages
	 */
	default void preUp() {
		Factions.get().log("Running migration " + this.migrationId() + "... ");
		this.up();
		
		if (FactionsMySQL.get().prepare(
				"INSERT INTO `migrations` (`migration_id`, `migration_date`)" + 
				"VALUES" + 
				"	(?, ?);")
				.setCatched(1, this.migrationId())
				.setCatched(2, System.currentTimeMillis())
			.execute(ExecuteType.UPDATE) == null) {
				Factions.get().warn("[MySQL] inserting migration record " + this.migrationId() + " failed");
		}

	}
	
}
