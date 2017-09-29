package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayer;

public class MsgPackFPlayer extends MemoryFPlayer {

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- // 
	
	public MsgPackFPlayer() { 
		
	}
	
	public MsgPackFPlayer(String id) {
		super(id);
	}

	public MsgPackFPlayer(MemoryFPlayer fplayer) {
		super(fplayer);
	}
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- // 

	@Override
	public void remove() {
		((MsgPackFPlayerColl) FPlayerColl.getUnsafeInstance()).remove(getId());
	}

}
