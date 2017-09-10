package net.redstoneore.legacyfactions.entity.persist.memory.json;

import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFaction;

public class JSONFaction extends MemoryFaction {
	
	public JSONFaction(MemoryFaction old) {
		super(old);
	}
	
	public JSONFaction() { }
	
	public JSONFaction(String id) {
		super(id);
	}
	
}
