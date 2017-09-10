package net.redstoneore.legacyfactions.entity.persist.mysql.migration;

import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;
import net.redstoneore.legacyfactions.entity.persist.mysql.MySQLPrepared.ExecuteType;

public class LFMigration10002 implements Migration {

	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static LFMigration10002 create() {
		return new LFMigration10002();
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //

	@Override
	public long migrationId() {
		return 10002;
	}

	@Override
	public void up() {
		String query = "CREATE TABLE `fplayer` (\n" + 
				"  `id` varchar(40) NOT NULL DEFAULT '',\n" + 
				"  `name` varchar(255) DEFAULT '',\n" + 
				"  `faction` varchar(36) DEFAULT '0',\n" + 
				"  `role` varchar(255) DEFAULT 'MEMBER',\n" + 
				"  `title` varchar(255) DEFAULT '',\n" + 
				"  `power` double DEFAULT 0,\n" + 
				"  `powerboost` double DEFAULT 0,\n" + 
				"  `lastpowerupdate` bigint(255) DEFAULT 0,\n" + 
				"  `lastlogintime` bigint(255) DEFAULT 0,\n" + 
				"  `chatmode` varchar(255) DEFAULT 'PUBLIC',\n" + 
				"  `ignorealliancechat` tinyint(1) DEFAULT 0,\n" + 
				"  `monitorjoins` tinyint(1) DEFAULT 0,\n" + 
				"  `spyingchat` tinyint(1) DEFAULT 0,\n" + 
				"  `showscoreboard` tinyint(1) DEFAULT 1,\n" + 
				"  `adminbypassing` tinyint(1) DEFAULT 0,\n" + 
				"  `kills` int(255) DEFAULT 0,\n" + 
				"  `deaths` int(255) DEFAULT 0,\n" + 
				"  `willautoleave` tinyint(1) DEFAULT 0,\n" + 
				"  `territorytitlesoff` tinyint(1) DEFAULT 0\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}

	@Override
	public void down() {
		String query = "DROP TABLE IF EXISTS `fplayer`";
		
		FactionsMySQL.get().prepare(query).execute(ExecuteType.UPDATE);
	}
	
}
