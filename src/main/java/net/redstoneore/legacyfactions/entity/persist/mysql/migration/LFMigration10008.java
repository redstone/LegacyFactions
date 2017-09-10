package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class LFMigration10008 implements Migration {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LFMigration10008 create() {
		return new LFMigration10008();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public long migrationId() {
		return 10008;
	}

	@Override
	public void up() {
		String query = "CREATE TABLE `faction_warps` (\n" + 
				"  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" + 
				"  `faction` varchar(40) DEFAULT NULL,\n" + 
				"  `name` varchar(255) DEFAULT NULL,\n" + 
				"  `location` varchar(255) DEFAULT NULL,\n" + 
				"  `password` varchar(255) DEFAULT NULL,\n" + 
				"  PRIMARY KEY (`id`)\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}

	@Override
	public void down() {
		String query = "DROP TABLE IF EXISTS `faction_warps`";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
}
