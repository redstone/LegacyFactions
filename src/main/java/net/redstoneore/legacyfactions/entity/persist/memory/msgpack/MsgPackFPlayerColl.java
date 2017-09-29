package net.redstoneore.legacyfactions.entity.persist.memory.msgpack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayer;
import net.redstoneore.legacyfactions.entity.persist.memory.MemoryFPlayerColl;
import net.redstoneore.legacyfactions.util.DiscUtil;

public class MsgPackFPlayerColl extends MemoryFPlayerColl {

	// -------------------------------------------------- //
	// STATIC 
	// -------------------------------------------------- // 
	
	private static transient Path file = Paths.get(FactionsMsgPack.getDatabasePath().toString(), "players.msgpack");
	public static Path getMsgPackFile() { return file; }
	
	public static TypeReference<Map<String, MsgPackFPlayer>> getMapType() {
		return new TypeReference<Map<String, MsgPackFPlayer>>() {};
	}
	
	// -------------------------------------------------- //
	// METHODS 
	// -------------------------------------------------- // 
	
	@Override
	public void forceSave() {
		this.forceSave(true);
	}

	@Override
	public void loadColl() {
		Map<String, MsgPackFPlayer> loadedFPlayers = this.loadCore();
		if (loadedFPlayers == null) return;
		this.fPlayers.clear();
		this.fPlayers.putAll(loadedFPlayers);
		Factions.get().log("Loaded " + fPlayers.size() + " players");
	}
	
	private Map<String, MsgPackFPlayer> loadCore() {
		if (!Files.exists(getMsgPackFile())) {
			return new HashMap<String, MsgPackFPlayer>();
		}

		byte[] content = null;
		try {
			content = DiscUtil.readBytes(getMsgPackFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (content == null) return null;
		Map<String, MsgPackFPlayer> data;
		
		try {
			data = Factions.get().getMsgPackObjectMapper().readValue(content, getMapType());
		} catch (IOException e1) {
			e1.printStackTrace();
			return  new HashMap<String, MsgPackFPlayer>();
		}
		
		for (Entry<String, MsgPackFPlayer> entry : data.entrySet()) {
			String id = entry.getKey();
			entry.getValue().setId(id);
		}

		return data;
	}

	@Override
	public FPlayer generateFPlayer(String id) {
		FPlayer player = new MsgPackFPlayer(id);
		this.fPlayers.put(player.getId(), player);
		return player;
	}

	@Override
	public void convertFrom(MemoryFPlayerColl old) {
		this.fPlayers.putAll(Maps.transformValues(old.fPlayers, new Function<FPlayer, MsgPackFPlayer>() {
			@Override
			public MsgPackFPlayer apply(FPlayer arg0) {
				return new MsgPackFPlayer((MemoryFPlayer) arg0);
			}
		}));
		forceSave();
		FPlayerColl.instance = this;		
	}
	
	@Override
	public void forceSave(boolean sync) {
		final Map<String, MsgPackFPlayer> entitiesThatShouldBeSaved = this.fPlayers.values().stream()
				.filter(entity -> ((MemoryFPlayer) entity).shouldBeSaved())
				.collect(Collectors.toMap(FPlayer::getId, p -> (MsgPackFPlayer) p));
		
		this.saveCore(getMsgPackFile(), entitiesThatShouldBeSaved, sync);
	}

	private boolean saveCore(Path target, Map<String, MsgPackFPlayer> data, boolean sync) {
		try {
			DiscUtil.writeBytes(target, Factions.get().getMsgPackObjectMapper().writeValueAsBytes(data));
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String getPersistType() {
		return FactionsMsgPack.get().getType().name();
	}

}
