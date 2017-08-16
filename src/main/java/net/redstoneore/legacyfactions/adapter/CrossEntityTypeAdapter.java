package net.redstoneore.legacyfactions.adapter;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.redstoneore.legacyfactions.util.cross.CrossEntityType;

public class CrossEntityTypeAdapter implements JsonDeserializer<CrossEntityType>, JsonSerializer<CrossEntityType> {

	@Override
	public JsonElement serialize(CrossEntityType src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getName());
	}

	@Override
	public CrossEntityType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return CrossEntityType.of(json.getAsString());
	}
	
}
