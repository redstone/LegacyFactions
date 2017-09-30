package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.jasypt.util.text.BasicTextEncryptor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.config.meta.Meta;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;
import net.redstoneore.legacyfactions.entity.persist.memory.json.FactionsJSON;
import net.redstoneore.legacyfactions.entity.persist.mysql.migration.Migrations;

public class FactionsMySQL extends PersistHandler {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsMySQL instance = new FactionsMySQL();
	public static FactionsMySQL get() { return instance; }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private DataSource datasource = null;
	
	private MySQLBoard boardInstance = null;
	private MySQLFPlayerColl fplayerCollInstance = null;
	private MySQLFactionColl factionCollInstance = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void init() {
		// This is not intended to completely protect the installation from attackers, but more so
		// prevent people from simply looking at a file to obtain the database credentials.
		
		if (!Meta.get().databaseCredentialsEncrypted && Meta.get().databaseHost != "") {
			Meta.get().databaseCredentialsEncrypted = true;
			
			// Details changed, set new base key
			this.setBaseKey();
			
			// Encrypt everything 
			Meta.get().databaseHost = this.encrypt(Meta.get().databaseHost);
			Meta.get().databaseUsername = this.encrypt(Meta.get().databaseUsername);
			Meta.get().databasePassword = this.encrypt(Meta.get().databasePassword);
			Meta.get().databaseName = this.encrypt(Meta.get().databaseName);
			
			// Save it
			Meta.get().save();
		}
		this.setConfig();
		
		Migrations.get().up();
	}
	
	public void setConfig() {
		String jdbcUrl = "jdbc:mysql://" + this.decrypt(Meta.get().databaseHost) + "/" + this.decrypt(Meta.get().databaseName);
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(this.decrypt(Meta.get().databaseUsername));
		config.setPassword(this.decrypt(Meta.get().databasePassword));
		config.setMaximumPoolSize(Meta.get().databaseConnectonMax);
		config.setAutoCommit(false);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		Factions.get().log("[MySQL] JDBC URL set to " + jdbcUrl);
		this.datasource = new HikariDataSource(config);
	}
	
	public Connection getConnection() throws SQLException {
		return this.datasource.getConnection();
	}
	
	@Override
	public void convertfrom(PersistHandler other) {
		// Ensure our config is set and migrations ready
		this.init();
		
		
		if (other instanceof FactionsJSON) {			
			MySQLFactionColl newFactionColl = (MySQLFactionColl) this.getFactionColl();
			other.getFactionColl().getAllFactions().forEach(faction -> {
				newFactionColl.generateFactionObject(faction);
			});
			
			MySQLBoard newBoard = (MySQLBoard) this.getBoard();
			other.getBoard().getAll().forEach(claim -> {
				newBoard.setFactionAt(Board.get().getFactionAt(claim), claim);
			});
			
			MySQLFPlayerColl newFPlayerColl = (MySQLFPlayerColl) this.getFPlayerColl();
			other.getFPlayerColl().getAllFPlayers().forEach(fplayer -> {
				newFPlayerColl.createFPlayer(fplayer);
			});
			
		}
		
		Factions.get().log("[MySQL] MySQL convert from " + other.getType().toString() + " to " + this.getType().toString());
	}

	@Override
	public PersistType getType() {
		return PersistType.MYSQL;
	}

	@Override
	public Board getBoard() {
		if (this.boardInstance == null) {
			this.boardInstance = new MySQLBoard();
		}
		return this.boardInstance;
	}

	@Override
	public FPlayerColl getFPlayerColl() {
		if (this.fplayerCollInstance == null) {
			this.fplayerCollInstance = new MySQLFPlayerColl();
		}
		return this.fplayerCollInstance;
	}

	@Override
	public FactionColl getFactionColl() {
		if (this.factionCollInstance == null) {
			this.factionCollInstance = new MySQLFactionColl();
		}
		return this.factionCollInstance;
	}
	
	private void setBaseKey() {
		String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()_+=-,./;:[]\\|}{";
		StringBuilder salt = new StringBuilder();
		Random random = new Random();
		while (salt.length() < (25 + new Random().nextInt(10))) {
			int index = (int) (random.nextFloat() * CHARS.length());
			salt.append(CHARS.charAt(index));
		}
		Meta.get().databaseKey = salt.toString();
		Meta.get().save();
	}
	
	private String encrypt(String encrypt) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(this.getKey());

		return textEncryptor.encrypt(encrypt);
	}
	
	private String decrypt(String decrypt) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(this.getKey());

		return textEncryptor.decrypt(decrypt);
	}
	
	private String getKey() {
		if (Meta.get().databaseKey == null) {
			this.setBaseKey();
		}
		String baseKey = Meta.get().databaseKey;
		
		// Nothing too crazy here, just makes the password a little bigger.
		return baseKey + "LF" +  baseKey; 
	}
	
	// -------------------------------------------------- //
	// UTIL
	// -------------------------------------------------- //
	
	/**
	 * Execute a query
	 * @param sql Query to execute
	 * @return the query, or null if it failed.
	 */
	public List<Map<String, String>> execute(String sql) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		List<Map<String, String>> result = null;
		
		try {
			connection = get().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();

			result = this.convert(resultSet);
		} catch (Exception e1) {
			Exception e2 = null;
			try {
				if (connection != null) {
					connection.rollback();					
				}
			} catch (Exception e) {
				e2 = e;
			} finally {
				e1.printStackTrace();
				
				if (e2 != null) {
					e2.printStackTrace();
				}
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
						
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Prepare a statement.
	 * @param sql SQL Statement
	 * @return A MySQLPrepared instance.
	 */
	public MySQLPrepared prepare(String sql) {
		return new MySQLPrepared(sql);
	}
	
	/**
	 * Convert a ResultSet into a List with a map of each field+value.
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> convert(ResultSet resultSet) throws SQLException {
		if (resultSet == null) {
			return new ArrayList<>();
		}

		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		
		ArrayList<Map<String, String>> rows = new ArrayList<>();
		while (resultSet.next()) {
			Map<String, String> row = new ConcurrentHashMap<>();
			for (int i = 1; i <= columnCount; ++i) {
				String value = "";
				if (resultSet.getObject(i) != null) {
					value = resultSet.getObject(i).toString();
				}
				row.put(metaData.getColumnName(i), String.valueOf(value));
			}
			
			rows.add(row);
		}
		
		return rows;
	}
	
}
