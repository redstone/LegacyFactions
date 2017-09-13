package net.redstoneore.legacyfactions.entity.persist;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.bukkit.ChatColor;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.util.DiscUtil;
import net.redstoneore.legacyfactions.util.MiscUtil;

public class Persist {

	// ------------------------------------------------------------ //
	// INSTANCE
	// ------------------------------------------------------------ //
	
	private static Persist instance = null;
	public static Persist get() {
		if (instance == null) {
			instance = new Persist();
		}
		return instance;
	}
	
	// ------------------------------------------------------------ //
	// GET NAME
	// These methods determine a user friendly name for this object.
	// ------------------------------------------------------------ //

	public static String getName(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}

	public static String getName(Object object) {
		return getName(object.getClass());
	}

	public static String getName(Type type) {
		return getName(type.getClass());
	}

	// ------------------------------------------------------------ //
	// GET PATH
	// These methods determine what path to use for this object.
	// ------------------------------------------------------------ //
	
	public Path getPath(String name) {
		return Paths.get(Factions.get().getDataFolder().getAbsolutePath(), name + ".json");
	}

	public Path getPath(Class<?> clazz) {
		return getPath(getName(clazz));
	}

	public Path getPath(Object obj) {
		return getPath(getName(obj));
	}

	public Path getPath(Type type) {
		return getPath(getName(type));
	}
	
	// ------------------------------------------------------------ //
	// NICE WRAPPERS
	// ------------------------------------------------------------ //
	
	public <T> T loadOrSaveDefault(T defaultInstance, Class<T> clazz) {
		return this.loadOrSaveDefault(defaultInstance, clazz, getPath(clazz));
	}

	public <T> T loadOrSaveDefault(T defaultInstance, Class<T> clazz, String name) {
		return this.loadOrSaveDefault(defaultInstance, clazz, getPath(name));
	}
	
	public <T> T loadOrSaveDefault(T defaultInstance, Class<T> clazz, Path file) {
		if (!Files.exists(file)) {
			Factions.get().log("Creating default: " + file);
			Factions.get().debug("File does not exist, creating default: " + file);
			this.save(defaultInstance, file);
			return defaultInstance;
		}
		
		
		Factions.get().debug("file exists, loading " + file);

		T loadedInstance = this.load(clazz, file);
		
		if (loadedInstance == null) {
			Factions.get().warn("Using default. Failed to load: " + file);
			
			Path backup = Paths.get(file + "_" + System.currentTimeMillis() + "_bad");
			if (Files.exists(backup)) {
				try {
					Path backup2 = Paths.get(backup +"_" + new Random().nextInt());
					
					// This should never happen
					if (Files.exists(backup2)) {
						Files.delete(backup2);
						Factions.get().warn("Removed old backup: " + backup2);
						
						Files.move(backup, backup2);
					}
				
					Files.move(backup, backup2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return defaultInstance;
		}
		Factions.get().debug("returning loaded instance for " + file);

		return loadedInstance;
	}
	
	// ------------------------------------------------------------ //
	// SAVE
	// ------------------------------------------------------------ //

	public boolean save(Object instance) {
		return save(instance, this.getPath(instance));
	}

	public boolean save(Object instance, String name) {
		return save(instance, this.getPath(name));
	}

	public boolean save(Object instance, Path file) {
		Factions.get().debug("Saving " + file.toAbsolutePath());
		assert instance != null;
		assert file != null;
		assert Factions.get().getGson() != null;
		
		return DiscUtil.writeCatch(file, Factions.get().getGson().toJson(instance), true);
	}

	// ------------------------------------------------------------ //
	// LOAD BY CLASS
	// ------------------------------------------------------------ //
	
	public <T> T load(Class<T> clazz) {
		return load(clazz, this.getPath(clazz));
	}

	public <T> T load(Class<T> clazz, String name) {
		return load(clazz, this.getPath(name));
	}

	public <T> T load(Class<T> clazz, Path file) {
		String content = DiscUtil.readCatch(file);
		if (content == null) {
			Factions.get().debug(file + " is null");
			return null;
		}
		
		/// Validate the JSON file
		if (!MiscUtil.isValidJSON(content)) {
			Factions.get().warn(ChatColor.GOLD + "The JSON file " + ChatColor.AQUA + file.toString() + ChatColor.GOLD + " is not valid JSON!");
			Factions.get().warn(ChatColor.GOLD + "This usually happens when someone have modified the file and made a mistake.");
			Factions.get().warn(ChatColor.GOLD + "If an exception is shown, it will give you the line an column that is incorrect.");
			Factions.get().warn(ChatColor.GOLD + "You should always make sure the file is valid JSON. ");
			Factions.get().warn(ChatColor.GOLD + "Paste the file into " + ChatColor.AQUA + "www.jsonlint.com" + ChatColor.GOLD + " and valid its contents.");
		}
		
		try {
			T instance = Factions.get().getGson().fromJson(content, clazz);
			return instance;
		} catch (Throwable e) {
			Factions.get().warn(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	// ------------------------------------------------------------ //
	// LOAD BY TYPE
	// ------------------------------------------------------------ //
	
	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, String name) {
		return (T) load(typeOfT, this.getPath(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, Path file) {
		String content = DiscUtil.readCatch(file);
		if (content == null) {
			Factions.get().debug(file.toString() + " is null");
			return null;
		}
		
		// Validate the JSON file
		if (!MiscUtil.isValidJSON(content)) {
			Factions.get().warn(ChatColor.GOLD + "The JSON file " + ChatColor.AQUA + file.toString() + ChatColor.GOLD + " is not valid JSON!");
			Factions.get().warn(ChatColor.GOLD + "This usually happens when someone have modified the file and made a mistake.");
			Factions.get().warn(ChatColor.GOLD + "If an exception is shown, it will give you the line an column that is incorrect.");
			Factions.get().warn(ChatColor.GOLD + "You should always make sure the file is valid JSON. ");
			Factions.get().warn(ChatColor.GOLD + "Paste the file into " + ChatColor.AQUA + "www.jsonlint.com" + ChatColor.GOLD + " and valid its contents.");
		}

		try {
			return (T) Factions.get().getGson().fromJson(content, typeOfT);
		} catch (Throwable e) {
			Factions.get().warn(e.getMessage());
		}

		return null;
	}

}

