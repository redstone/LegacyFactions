package net.redstoneore.legacyfactions.adapter;

import com.google.gson.*;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.util.LazyLocation;

import java.lang.reflect.Type;

public class LazyLocationAdapter implements JsonDeserializer<LazyLocation>, JsonSerializer<LazyLocation> {

	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private static transient LazyLocationAdapter adapter = new LazyLocationAdapter();
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public static LazyLocation deserialise(String json) {
		if (json.trim().equalsIgnoreCase("") || json.equalsIgnoreCase("null") || json == null) return null;
		return adapter.deserialize(Factions.get().getGson().toJsonTree(json), LazyLocation.class, null);
	}
	
	public static String serialise(LazyLocation location) {
		return adapter.serialize(location, LazyLocation.class, null).getAsString();
	}
	
    @Override
    public LazyLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();

            String worldName = obj.get("world").getAsString();
            double x = obj.get("x").getAsDouble();
            double y = obj.get("y").getAsDouble();
            double z = obj.get("z").getAsDouble();
            float yaw = obj.get("yaw").getAsFloat();
            float pitch = obj.get("pitch").getAsFloat();

            return new LazyLocation(worldName, x, y, z, yaw, pitch);

        } catch (Exception ex) {
            ex.printStackTrace();
            Factions.get().warn("Error encountered while deserializing a LazyLocation.");
            return null;
        }
    }

    @Override
    public JsonElement serialize(LazyLocation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        try {
            obj.addProperty("world", src.getWorldName());
            obj.addProperty("x", src.getX());
            obj.addProperty("y", src.getY());
            obj.addProperty("z", src.getZ());
            obj.addProperty("yaw", src.getYaw());
            obj.addProperty("pitch", src.getPitch());

            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            Factions.get().warn("Error encountered while serializing a LazyLocation.");
            return obj;
        }
    }
}
