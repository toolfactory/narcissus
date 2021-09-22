package narcissus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * The Class LibraryLoader.
 */
public class LibraryLoader {
    /** The operating system type. */
    public static final OperatingSystem OS;

    /** The machine word size. */
    public static int archBits;

    /** The operating system type. */
    public enum OperatingSystem {
        /** Windows. */
        Windows,

        /** Mac OS X. */
        MacOSX,

        /** Linux. */
        Linux,

        /** Solaris. */
        Solaris,

        /** BSD. */
        BSD,

        /** Unix or AIX. */
        Unix,

        /** Unknown. */
        Unknown
    }

    static {
        String osName = null;
        try {
            osName = System.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        } catch (final SecurityException e) {
            // Ignore
        }
        if (osName == null) {
            OS = OperatingSystem.Unknown;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            OS = OperatingSystem.MacOSX;
        } else if (osName.contains("win")) {
            OS = OperatingSystem.Windows;
        } else if (osName.contains("nux")) {
            OS = OperatingSystem.Linux;
        } else if (osName.contains("sunos") || osName.contains("solaris")) {
            OS = OperatingSystem.Solaris;
        } else if (osName.contains("bsd")) {
            OS = OperatingSystem.Unix;
        } else if (osName.contains("nix") || osName.contains("aix")) {
            OS = OperatingSystem.Unix;
        } else {
            OS = OperatingSystem.Unknown;
        }

        archBits = 64;
        String osArch = System.getProperty("os.arch");
        System.out.println("OS.ARCH: " + osArch);
        if (osArch != null && (osArch.contains("86") || osArch.contains("32"))) {
            archBits = 32;
        } else {
            String dataModel = System.getProperty("sun.arch.data.model");
            if (dataModel != null && dataModel.contains("32")) {
                archBits = 32;
            }
        }
    }

    /**
     * Load library from jar.
     *
     * @param libraryResourcePath
     *            the library resource path
     */
    public static void loadLibraryFromJar(String libraryResourcePath) {
        File tempFile = null;
        boolean tempFileIsPosix = false;
        InputStream inputSream = null;
        try {
            inputSream = Narcissus.class.getResourceAsStream(
                    libraryResourcePath.startsWith("/") ? libraryResourcePath : "/" + libraryResourcePath);
            if (inputSream == null) {
                throw new FileNotFoundException("Could not find library within jar: " + libraryResourcePath);
            }

            // Extract library to temp file
            String filename = libraryResourcePath.substring(libraryResourcePath.lastIndexOf('/') + 1);
            int dotIdx = filename.indexOf('.');
            String baseName = dotIdx < 0 ? filename : filename.substring(0, dotIdx);
            String suffix = dotIdx < 0 ? ".so" : filename.substring(dotIdx);
            tempFile = File.createTempFile("lib_" + baseName + "_", suffix);

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

            // Load the library
            System.load(tempFile.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Could not load library " + libraryResourcePath + " : " + e);
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
