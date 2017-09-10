package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class LFMigration10001 implements Migration {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LFMigration10001 create() {
		return new LFMigration10001();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public long migrationId() {
		return 10001;
	}

	@Override
	public void up() {
		String query = "CREATE TABLE `board` (\n" + 
				"  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" + 
				"  `x` int(11) DEFAULT NULL,\n" + 
				"  `z` int(11) DEFAULT NULL,\n" + 
				"  `world` varchar(255) DEFAULT NULL,\n" + 
				"  `faction` varchar(40) DEFAULT NULL,\n" + 
				"  PRIMARY KEY (`id`)\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}

	@Override
	public void down() {
		String query = "DROP TABLE IF EXISTS `board`";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
}
