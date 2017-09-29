package net.redstoneore.legacyfactions.entity.persist;

import net.redstoneore.legacyfactions.entity.persist.memory.json.FactionsJSON;
import net.redstoneore.legacyfactions.entity.persist.memory.msgpack.FactionsMsgPack;
import net.redstoneore.legacyfactions.entity.persist.mysql.FactionsMySQL;

public enum PersistType {

	// --------------------------------------------- //
	// ENUM
	// --------------------------------------------- //
	
	JSON(false, FactionsJSON.get()),
	MYSQL(true, FactionsMySQL.get()),
	MSGPACK(false, FactionsMsgPack.get()),
	
	;
	
	// --------------------------------------------- //
	// CONSTRUCT
	// --------------------------------------------- //
	
	PersistType(boolean requiresDatabaseCredentials, PersistHandler handler) {
		this.requiresDatabaseCredentials = requiresDatabaseCredentials;
		this.handler = handler;
	}

	// --------------------------------------------- //
	// FIELDS
	// --------------------------------------------- //
	
	private final boolean requiresDatabaseCredentials;
	private final PersistHandler handler;
	
	// --------------------------------------------- //
	// METHODS
	// --------------------------------------------- //
	
	public boolean requiresDatabaseCredentials() {
		return this.requiresDatabaseCredentials;
	}
	
	public PersistHandler getHandler() {
		return this.handler;
	}
	
}
