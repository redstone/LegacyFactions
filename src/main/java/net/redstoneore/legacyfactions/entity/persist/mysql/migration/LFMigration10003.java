package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class LFMigration10003 implements Migration {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LFMigration10003 create() {
		return new LFMigration10003();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public long migrationId() {
		return 10003;
	}

	@Override
	public void up() {
		String query = "CREATE TABLE `faction_announcements` (\n" + 
				"  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" + 
				"  `faction` varchar(40) DEFAULT NULL,\n" + 
				"  `announcer` varchar(40) DEFAULT NULL,\n" + 
				"  `message` text DEFAULT NULL,\n" + 
				"  PRIMARY KEY (`id`)\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}

	@Override
	public void down() {
		String query = "DROP TABLE IF EXISTS `faction_announcements`";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
}
