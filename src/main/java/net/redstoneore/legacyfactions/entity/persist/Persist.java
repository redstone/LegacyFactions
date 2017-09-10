package net.redstoneore.legacyfactions.entity.persist;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.util.DiscUtil;

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

	public static String getName(Object o) {
		return getName(o.getClass());
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
	
	public <T> T loadOrSaveDefault(T def, Class<T> clazz) {
		return this.loadOrSaveDefault(def, clazz, getPath(clazz));
	}

	public <T> T loadOrSaveDefault(T def, Class<T> clazz, String name) {
		return this.loadOrSaveDefault(def, clazz, getPath(name));
	}
	
	public <T> T loadOrSaveDefault(T instance, Class<T> clazz, Path file) {
		if (!Files.exists(file)) {
			Factions.get().log("Creating default: " + file);
			Factions.get().debug("File does not exist, creating default: " + file);
			this.save(instance, file);
			return instance;
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
			
			return instance;
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

		try {
			T instance = Factions.get().getGson().fromJson(content, clazz);
			return instance;
		} catch (Exception ex) {	// output the error message rather than full stack trace; error parsing the file, most likely
			Factions.get().warn(ex.getMessage());
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
			return null;
		}

		try {
			return (T) Factions.get().getGson().fromJson(content, typeOfT);
		} catch (Exception ex) {	// output the error message rather than full stack trace; error parsing the file, most likely
			Factions.get().warn(ex.getMessage());
		}

		return null;

	}
	
	// ------------------------------------------------------------ //
	// DEPRECATED: NICE WRAPPERS
	// ------------------------------------------------------------ //
	
	@Deprecated
	public <T> T loadOrSaveDefault(T def, Class<T> clazz, File file) {
		if (!file.exists()) {
			Factions.get().log("Creating default: " + file);
			this.save(def, file);
			return def;
		}

		T loaded = this.load(clazz, file);

		if (loaded == null) {
			Factions.get().warn("Using default as I failed to load: " + file);

			// backup bad file, so user can attempt to recover their changes from it
			File backup = new File(file.getPath() + "_bad");
			if (backup.exists()) {
				backup.delete();
			}
			Factions.get().warn("Backing up copy of bad file to: " + backup);
			file.renameTo(backup);

			return def;
		}

		return loaded;
	}
	
	// ------------------------------------------------------------ //
	// DEPRECATED: FILE SAVE
	// ------------------------------------------------------------ //
	
	@Deprecated
	public boolean save(Object instance, File file) {
		Factions.get().debug("Saving " + file.getAbsolutePath());
		assert instance != null;
		assert file != null;
		assert Factions.get().gson != null;
		
		return DiscUtil.writeCatch(file, Factions.get().gson.toJson(instance), true);
	}
	
	// ------------------------------------------------------------ //
	// DEPRECATED: FILE LOAD
	// ------------------------------------------------------------ //

	@Deprecated
	public <T> T load(Class<T> clazz, File file) {
		String content = DiscUtil.readCatch(file);
		if (content == null) {
			return null;
		}

		try {
			T instance = Factions.get().gson.fromJson(content, clazz);
			return instance;
		} catch (Exception ex) {	// output the error message rather than full stack trace; error parsing the file, most likely
			Factions.get().warn(ex.getMessage());
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T> T load(Type typeOfT, File file) {
		String content = DiscUtil.readCatch(file);
		if (content == null) {
			return null;
		}

		try {
			return (T) Factions.get().gson.fromJson(content, typeOfT);
		} catch (Exception ex) {	// output the error message rather than full stack trace; error parsing the file, most likely
			Factions.get().warn(ex.getMessage());
		}

		return null;
	}
	
	// ------------------------------------------------------------ //
	// DEPRECATED: GET FILE
	// ------------------------------------------------------------ //

	@Deprecated
	public File getFile(String name) {
		return new File(Factions.get().getDataFolder(), name + ".json");
	}

	@Deprecated
	public File getFile(Class<?> clazz) {
		return getFile(getName(clazz));
	}

	@Deprecated
	public File getFile(Object obj) {
		return getFile(getName(obj));
	}

	@Deprecated
	public File getFile(Type type) {
		return getFile(getName(type));
	}

}

