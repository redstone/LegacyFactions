package net.redstoneore.legacyfactions.adapter;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.redstoneore.legacyfactions.util.cross.CrossColour;

public class CrossColourAdapter implements JsonDeserializer<CrossColour>, JsonSerializer<CrossColour> {

	@Override
	public JsonElement serialize(CrossColour src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getName());
	}

	@Override
	public CrossColour deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return CrossColour.of(json.getAsString());
	}
	
}
