package com.codey.OpenGL;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.lwjgl.LWJGLException;

public class LibraryLoader {
	
	public static void loadNativeLibrary() throws LWJGLException, Exception {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		
		
		if (os.compareTo("Mac OS X") == 0) {
			addLibraryPath("natives/macosx");
		}
		else if (os.compareTo("Linux") == 0)
			addLibraryPath("natives/linux");
		else {
			addLibraryPath("natives/windows");
			
			if (arch.compareTo("amd64") == 0 || arch.compareTo("x86_64") == 0) 
				System.loadLibrary("OpenAL64");
			else
				System.loadLibrary("OpenAL32");
		}
		
	}
	
	private static void addLibraryPath(String s) throws Exception{
		final Field userPaths = ClassLoader.class.getDeclaredField("usr_paths");
		userPaths.setAccessible(true);
		
		final String[] paths = (String[]) userPaths.get(null);
		
		for (String path : paths) 
			if (path.equals(s))
				return;
		
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[paths.length - 1] = s;
		userPaths.set(null, newPaths);
		
	}
}
