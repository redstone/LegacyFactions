package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.entity.Meta;
import net.redstoneore.legacyfactions.entity.persist.PersistHandler;
import net.redstoneore.legacyfactions.entity.persist.PersistType;

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
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public void init() {
		this.setConfig();
	}
	
	public void setConfig() {		
		String jdbcUrl = "jdbc:mysql://" + Meta.get().databaseHost + "/" + Meta.get().databaseName;
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(Meta.get().databaseUsername);
		config.setPassword(Meta.get().databasePassword);
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
		// TODO: convert from other 
		Factions.get().log("[MySQL] MySQL convert from " + other.getType().toString() + " to " + this.getType().toString());
	}

	@Override
	public PersistType getType() {
		return PersistType.MYSQL;
	}

	@Override
	public Board getBoard() {
		return new MySQLBoard();
	}

	@Override
	public FPlayerColl getFPlayerColl() {
		return new MySQLFPlayerColl();
	}

	@Override
	public FactionColl getFactionColl() {
		return new MySQLFactionColl();
	}
	
	// -------------------------------------------------- //
	// STATIC UTIL
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
	 * Prepare a statement
	 * @param sql
	 * @return
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
