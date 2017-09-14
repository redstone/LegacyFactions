package net.redstoneore.legacyfactions.util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.redstoneore.legacyfactions.Factions;

public class LibraryUtil {

	/**
	 * loadLibrary is from factions-top by novucs<br>
	 * <br>
	 * The MIT License (MIT)<br>
	 * <br>
	 * Copyright (c) novucs http://www.novucs.net<br>
	 * Copyright (c) contributors<br>
	 * <br>
	 * Permission is hereby granted, free of charge, to any person obtaining a copy<br>
	 * of this software and associated documentation files (the "Software"), to deal<br>
	 * in the Software without restriction, including without limitation the rights<br>
	 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>
	 * copies of the Software, and to permit persons to whom the Software is<br>
	 * furnished to do so, subject to the following conditions:<br>
	 * <br>
	 * The above copyright notice and this permission notice shall be included in<br>
	 * all copies or substantial portions of the Software.<br>
	 * <br>
	 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>
	 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>
	 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>
	 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>
	 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>
	 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN<br>
	 * THE SOFTWARE.<br>
	 * @param url
	 * @throws Exception
	 * 
	 * {@literal}
	 * 
	 */
    public static void loadLibrary(String url) throws Exception {
        // Get the library file.
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        String pathName = "lib" + File.separator + fileName;
        File library = new File(pathName);

        // Download the library from maven into the libs folder if none already exists.
        if (!library.exists()) {
            Factions.get().log("Downloading " + fileName + " dependency . . .");
            library.getParentFile().mkdirs();
            library.createNewFile();
            URL repo = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(repo.openStream());
            FileOutputStream fos = new FileOutputStream(pathName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            Factions.get().log(fileName + " successfully downloaded!");
            fos.close();
        }

        // Load library to JVM.
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), library.toURI().toURL());
    }

}
