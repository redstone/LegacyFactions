package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class LFMigration10009 implements Migration {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LFMigration10009 create() {
		return new LFMigration10009();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public long migrationId() {
		return 10009;
	}

	@Override
	public void up() {
		String query = "CREATE TABLE `faction` (\n" + 
				"  `id` varchar(40) NOT NULL DEFAULT '',\n" + 
				"  `tag` varchar(255) DEFAULT 'unknown',\n" + 
				"  `description` varchar(255) DEFAULT '',\n" + 
				"  `forcedmapcharacter` char(1) DEFAULT '',\n" + 
				"  `forcedmapcolour` varchar(255) DEFAULT '',\n" + 
				"  `permanentpower` int(11) DEFAULT 0,\n" + 
				"  `powerboost` double DEFAULT 0,\n" + 
				"  `home` varchar(255) DEFAULT '',\n" + 
				"  `foundeddate` bigint(20) DEFAULT 0,\n" + 
				"  `autokick` bigint(20) DEFAULT -1,\n" + 
				"  `emblem` varchar(255) DEFAULT '???',\n" + 
				"  `maxvaults` int(20) DEFAULT 0,\n" + 
				"  `lastdeath` bigint(20) DEFAULT 0\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}

	@Override
	public void down() {
		String query = "DROP TABLE IF EXISTS `faction`";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
}
