package narcissus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static List<Field> enumerateFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Field field : Narcissus.nativeGetDeclaredFields(c)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static List<Method> enumerateMethods(Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Method method : Narcissus.nativeGetDeclaredMethods(c)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static List<Constructor<?>> enumerateConstructors(Class<?> cls) {
        List<Constructor<?>> constructors = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Constructor<?> constructor : Narcissus.nativeGetDeclaredConstructors(c)) {
                constructors.add(constructor);
            }
        }
        return constructors;
    }

    public static Field findField(Object obj, String fieldName) {
        for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : Narcissus.nativeGetDeclaredFields(c)) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static Method findMethod(Object obj, String methodName, Class<?>... paramTypes) {
        for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Method method : Narcissus.nativeGetDeclaredMethods(c)) {
                if (method.getName().equals(methodName) && Arrays.equals(paramTypes, method.getParameterTypes())) {
                    return method;
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------------------------------------------

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
