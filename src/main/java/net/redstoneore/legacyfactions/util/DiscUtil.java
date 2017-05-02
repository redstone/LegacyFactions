package net.redstoneore.legacyfactions.util;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

	// -------------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------------- //

	private final static String UTF8 = "UTF-8";

	// -------------------------------------------------- //
	// BYTE
	// -------------------------------------------------- //
	
	public static byte[] readBytes(File file) throws IOException {
		Integer length = (int) file.length();
		byte[] output = new byte[length];
		Integer offset = 0;
		
		InputStream in = new FileInputStream(file);
		
		while (offset < length) {
			offset += in.read(output, offset, (length - offset));
		}
		
		in.close();
		
		return output;
	}

	public static void writeBytes(File file, byte[] bytes) throws IOException {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.close();
	}

	// -------------------------------------------------- //
	// STRING
	// -------------------------------------------------- //

	public static void write(File file, String content) throws IOException {
		writeBytes(file, utf8(content));
	}

	public static String read(File file) throws IOException {
		return utf8(readBytes(file));
	}

	// -------------------------------------------------- //
	// CATCH
	// -------------------------------------------------- //

	private static HashMap<String, Lock> locks = new HashMap<>();

	public static Boolean writeCatch(final File file, final String content, boolean sync) {
		String name = file.getName();
		final Lock lock;

		// Create lock for each file if there isn't already one.
		if (locks.containsKey(name)) {
			lock = locks.get(name);
		} else {
			ReadWriteLock rwl = new ReentrantReadWriteLock();
			lock = rwl.writeLock();
			locks.put(name, lock);
		}

		if (sync) {
			lock.lock();
			try {
				write(file, content);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> {
				lock.lock();
				try {
					write(file, content);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			});
		}

		return true;
	}

	public static String readCatch(File file) {
		try {
			return read(file);
		} catch (Exception e) {
			return null;
		}
	}

	// -------------------------------------------------- //
	// UTF8 UTIL
	// -------------------------------------------------- //

	public static byte[] utf8(String string) {
		try {
			return string.getBytes(UTF8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String utf8(byte[] bytes) {
		try {
			return new String(bytes, UTF8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
