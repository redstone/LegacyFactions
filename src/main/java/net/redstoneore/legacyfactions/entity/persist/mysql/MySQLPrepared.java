package net.redstoneore.legacyfactions.entity.persist.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;

public class MySQLPrepared {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- /
	
	protected MySQLPrepared(String sql) {
		try {
			this.connection = FactionsMySQL.get().getConnection();
			this.statement = this.connection.prepareStatement(sql);
		} catch (SQLException e1) {
			// Something went wrong, lets clean up..
			e1.printStackTrace();
			
			// Is there a connection at all?
			if (this.connection != null) {
				// Is there a statement?
				if (this.statement != null) {
					try {
						// Try to close the statement.
						this.statement.close();
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
				}
				
				try {
					// Try to close the connection. 
					this.connection.close();
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			}
			
			this.connection = null;
		}
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- /
	
	private Connection connection = null;
	private PreparedStatement statement = null;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- /
	
	public boolean connectionSuccessful() {
		return this.connection != null; 
	}
	
	/**
	 * set index as int value
	 * @param parameterIndex
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, int value) throws SQLException {
		this.statement.setInt(parameterIndex, value);
		return this;
	}
	
	/**
	 * Set index as string value
	 * @param parameterIndex
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, String value) throws SQLException {
		this.statement.setString(parameterIndex, value);
		return this;
	}
	
	/**
	 * Set index as double value
	 * @param parameterIndex
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, double value) throws SQLException {
		this.statement.setDouble(parameterIndex, value);
		return this;
	}
	
	/**
	 * Set index as long value
	 * @param parameterIndex
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, long value) throws SQLException {
		this.statement.setLong(parameterIndex, value);
		return this;
	}
	
	/**
	 * Set index as faction - this will set a string with the faction id
	 * @param parameterIndex
	 * @param faction
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, Faction faction) throws SQLException {
		this.statement.setString(parameterIndex, faction.getId());
		return this;
	}
	
	/**
	 * Set index as Locality value - this will set a string with the locality in string form
	 * @param parameterIndex
	 * @param locality
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, Locality locality) throws SQLException {
		this.statement.setString(parameterIndex, locality.toString());
		return this;
	}
	
	/**
	 * Set index as FPlayer value - this will set a string with the fplayer id
	 * @param parameterIndex
	 * @param fplayer
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, FPlayer fplayer) throws SQLException {
		this.statement.setString(parameterIndex, fplayer.getId());
		return this;
	}
	
	/**
	 * Set index as boolean value 
	 * @param parameterIndex
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public MySQLPrepared set(int parameterIndex, boolean value) throws SQLException {
		this.statement.setBoolean(parameterIndex, value);
		return this;
	}
	
	/**
	 * set index as int value.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param value
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, int value) {
		try {
			this.statement.setInt(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as string value.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param value
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, String value) {
		try {
			this.statement.setString(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as double value.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param value
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, double value) {
		try {
			this.statement.setDouble(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as long value.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param value
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, long value) {
		try {
			this.statement.setLong(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as faction - this will set a string with the faction id
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param faction
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, Faction faction) {
		try {
			this.statement.setString(parameterIndex, faction.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as Locality value - this will set a string with the locality in string form.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param locality
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, Locality locality) {
		try {
			this.statement.setString(parameterIndex, locality.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Set index as FPlayer value - this will set a string with the fplayer id.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param fplayer
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, FPlayer fplayer) {
		try {
			this.statement.setString(parameterIndex, fplayer.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
		
	/**
	 * Set index as boolean value.
	 * Will catch the error, but simply print to console.
	 * @param parameterIndex
	 * @param value
	 * @return
	 */
	public MySQLPrepared setCatched(int parameterIndex, boolean value) {
		try {
			this.statement.setBoolean(parameterIndex, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Execute command.
	 * @return results, or null if failed
	 */
	public List<Map<String, String>> execute(ExecuteType type) {
		ResultSet resultSet = null;
		
		List<Map<String, String>> result = null;
		boolean success = false;
		
		try {
			switch (type) {
			case UPDATE:
				resultSet = null;

				if (this.statement.executeUpdate() == 1) {
					success = true;
				}
				break;
			case SELECT:
				resultSet = this.statement.executeQuery();
				break;
			default:
				Factions.get().warn("Unknown execute type " + type);
				return null;
			}
			
			if (resultSet != null) {
				result = FactionsMySQL.get().convert(resultSet);
				success = true;
			} else {
				if (success) {
					result = new ArrayList<>();
				}
			}
		} catch (Exception e1) {
			success = false;
			
			Exception e2 = null;
			try {
				if (this.connection != null) {
					this.connection.rollback();					
				}
			} catch (Exception e) {
				// Store for finally statement, so exceptions are printed in the right order.
				e2 = e;
			} finally {
				e1.printStackTrace();
				
				if (e2 != null) {
					e2.printStackTrace();
				}
				// Connection and ResultSet are closed in the next finally statement
			}
			
		} finally {
			if (success) {
				try {
					connection.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					resultSet = null;
				}
			}
			
			if (this.statement != null) {
				try {
					this.statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					this.statement = null;
				}
			}
						
			this.close();
		}
		
		return result;
	}
	
	/**
	 * You only need to call close if an error is thrown while setting a value.
	 */
	public void close() {
		if (this.statement != null) {
			try {
				this.statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				this.statement = null;
			}
		}
		
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.connection = null;
			}
		}
		
	}
	
	// -------------------------------------------------- //
	// CLASSES
	// -------------------------------------------------- //
	
	public enum ExecuteType {
		
		// -------------------------------------------------- //
		// ENUM
		// -------------------------------------------------- //
		
		SELECT,
		UPDATE,
		
		;
	}
	
}
