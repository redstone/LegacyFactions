package net.redstoneore.legacyfactions.util;

import org.bukkit.Bukkit;

import net.redstoneore.legacyfactions.Factions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil {

	// -------------------------------------------------- //
	// BYTE
	// -------------------------------------------------- //
	
	/**
	 * Read bytes from a path
	 * @param file The file to read from
	 * @return the bytes read
	 * @throws IOException
	 */
	public static byte[] readBytes(Path file) throws IOException {
		SeekableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.READ);
		ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
		channel.read(buffer);
		
		return (byte[]) buffer.flip().array();
	}
	
	/**
	 * Writes bytes to a path
	 * @param file The file to write to
	 * @param bytes Bytes to write
	 * @return the number of bytes written, possibly zero.
	 * @throws IOException
	 */
	public static int writeBytes(Path file, byte[] bytes) throws IOException {		
		FileChannel channel = FileChannel.open(file, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		channel.force(false);
		int bytesWritten = channel.write(buffer);
		channel.close();
				
		return bytesWritten;
	}
	
	@Deprecated
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
	
	@Deprecated
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

	public static String read(Path file) throws IOException {
		return utf8(readBytes(file));
	}
	
	public static void write(Path file, String content) throws IOException {
		writeBytes(file, utf8(content));
	}
	
	@Deprecated
	public static String read(File file) throws IOException {
		return utf8(readBytes(file));
	}
	
	@Deprecated
	public static void write(File file, String content) throws IOException {
		writeBytes(file, utf8(content));
	}
	
	// -------------------------------------------------- //
	// CATCH
	// -------------------------------------------------- //

	private static HashMap<String, Lock> locks = new HashMap<>();
	
	public static String readCatch(Path file) {
		try {
			return read(file);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Boolean writeCatch(final Path file, final String content, boolean sync) {
		String name = file.getFileName().toString();
		final Lock lock;
		
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
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), () -> {
				lock.lock();
				
				try {
					write(file, content);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			});
		}
		
		return true;
	}
	
	@Deprecated
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

	@Deprecated
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
			return string.getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String utf8(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
