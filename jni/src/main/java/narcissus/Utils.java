package narcissus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static String getClassTypeSignature(Class<?> cls) {
        Class<?> eltType = cls;
        int arrayDepth = 0;
        while (eltType.isArray()) {
            arrayDepth++;
            eltType = eltType.componentType();
        }
        String eltSig = eltType == int.class ? "I"
                : eltType == long.class ? "J"
                        : eltType == short.class ? "S"
                                : eltType == char.class ? "C"
                                        : eltType == double.class ? "D"
                                                : eltType == float.class ? "F"
                                                        : eltType == byte.class ? "B"
                                                                : eltType == boolean.class ? "Z"
                                                                        : eltType == short.class ? "S"
                                                                                : "L" + eltType.getName()
                                                                                        .replace('.', '/') + ";";
        String fieldSig;
        if (arrayDepth == 0) {
            fieldSig = eltSig;
        } else {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < arrayDepth; i++) {
                buf.append('[');
            }
            buf.append(eltSig);
            fieldSig = buf.toString();
        }
        return fieldSig;
    }

    public static String getClassTypeSignature(String className) {
        String eltType = className;
        int arrayDepth = 0;
        while (eltType.endsWith("[]")) {
            arrayDepth++;
            eltType = eltType.substring(0, eltType.length() - 2);
        }
        String eltSig = eltType.equals("int") ? "I"
                : eltType.equals("long") ? "J"
                        : eltType.equals("short") ? "S"
                                : eltType.equals("char") ? "C"
                                        : eltType.equals("double") ? "D"
                                                : eltType.equals("float") ? "F"
                                                        : eltType.equals("byte") ? "B"
                                                                : eltType.equals("boolean") ? "Z"
                                                                        : eltType.equals("short") ? "S"
                                                                                : "L" + eltType.replace('.', '/')
                                                                                        + ";";
        String fieldSig;
        if (arrayDepth == 0) {
            fieldSig = eltSig;
        } else {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < arrayDepth; i++) {
                buf.append('[');
            }
            buf.append(eltSig);
            fieldSig = buf.toString();
        }
        return fieldSig;
    }

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
