package narcissus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Narcissus {
    static {
        loadLibraryFromJar("lib/narcissus.so");
    }

    private static void loadLibraryFromJar(String libraryResourcePath) {
        File tempFile = null;
        boolean tempFileIsPosix = false;
        InputStream inputSream = null;
        try {
            inputSream = Narcissus.class.getResourceAsStream(libraryResourcePath);
            if (inputSream == null) {
                throw new FileNotFoundException("Could not find library within jar: " + libraryResourcePath);
            }

            // Extract library to temp file
            String filename = libraryResourcePath.substring(libraryResourcePath.lastIndexOf('/') + 1);
            int dotIdx = filename.indexOf('.');
            String baseName = dotIdx < 0 ? filename : filename.substring(0, dotIdx);
            String suffix = dotIdx < 0 ? ".so" : filename.substring(dotIdx);
            tempFile = File.createTempFile("lib_" + baseName, suffix);

            try {
                if (tempFile.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
                    tempFileIsPosix = true;
                }
            } catch (Exception e) {
                // Ignore
            }

            byte[] buffer = new byte[8192];
            OutputStream os = new FileOutputStream(tempFile);
            try {
                for (int readBytes; (readBytes = inputSream.read(buffer)) != -1;) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                os.close();
            }

            try {
                // Load the library
                System.load(tempFile.getAbsolutePath());
            } finally {
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not load library " + libraryResourcePath, e);
        } finally {
            if (inputSream != null) {
                try {
                    inputSream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (tempFile != null) {
                boolean deleted = false;
                if (tempFileIsPosix) {
                    deleted = tempFile.delete();
                }
                if (!deleted) {
                    tempFile.deleteOnExit();
                }
            }
        }
    }
}