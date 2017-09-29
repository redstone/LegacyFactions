package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFaction;

public class MsgPackFaction extends MemoryFaction {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- // 
	
	public MsgPackFaction() {
		
	}
	
	public MsgPackFaction(String id) {
		super(id);
	}
	
	public MsgPackFaction(MemoryFaction faction) {
		super(faction);
	}

}
