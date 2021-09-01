package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Narcissus {
    // Statically load the native library
    static {
        final String libraryResourcePath;
        switch (LibraryLoader.OS) {
        case Linux:
            libraryResourcePath = "lib/narcissus.so";
            break;
        case MacOSX:
            libraryResourcePath = "lib/narcissus.dylib";
            break;
        case Windows:
            libraryResourcePath = "lib/narcissus.dll";
            break;
        case BSD:
        case Solaris:
        case Unix:
        case Unknown:
        default:
            throw new RuntimeException("No native library available for this operating system");
        }
        try {
            LibraryLoader.loadLibraryFromJar(libraryResourcePath);
            init();
        } catch (Throwable t) {
            throw new RuntimeException("Could not load native library " + libraryResourcePath, t);
        }
    }

    // Native init method (sets up global refs to primitive type classes, for speed)
    private static native void init();

    // -------------------------------------------------------------------------------------------------------------

    /** Enumerate all fields in the given class, ignoring visibility. */
    public static List<Field> enumerateFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Field field : Narcissus.getDeclaredFields(c)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /** Enumerate all methods in the given class, ignoring visibility. */
    public static List<Method> enumerateMethods(Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Method method : Narcissus.getDeclaredMethods(c)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /** Enumerate all constructors in the given class, ignoring visibility. */
    public static List<Constructor<?>> enumerateConstructors(Class<?> cls) {
        List<Constructor<?>> constructors = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Constructor<?> constructor : Narcissus.getDeclaredConstructors(c)) {
                constructors.add(constructor);
            }
        }
        return constructors;
    }

    /** Find a field by name in the given class, ignoring visibility. */
    public static Field findField(Class<?> cls, String fieldName) throws NoSuchFieldException {
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Field field : Narcissus.getDeclaredFields(c)) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /** Find a method by name in the given class, ignoring visibility. */
    public static Method findMethod(Class<?> cls, String methodName, Class<?>... paramTypes)
            throws NoSuchMethodException {
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Method method : Narcissus.getDeclaredMethods(c)) {
                if (method.getName().equals(methodName) && Arrays.equals(paramTypes, method.getParameterTypes())) {
                    return method;
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    // -------------------------------------------------------------------------------------------------------------

    public static native Method[] getDeclaredMethods(Class<?> cls);

    public static native <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

    public static native Field[] getDeclaredFields(Class<?> cls);

    // -------------------------------------------------------------------------------------------------------------

    public static native int getIntFieldVal(Object object, Field field);

    public static native long getLongFieldVal(Object object, Field field);

    public static native short getShortFieldVal(Object object, Field field);

    public static native char getCharFieldVal(Object object, Field field);

    public static native boolean getBooleanFieldVal(Object object, Field field);

    public static native byte getByteFieldVal(Object object, Field field);

    public static native float getFloatFieldVal(Object object, Field field);

    public static native double getDoubleFieldVal(Object object, Field field);

    public static native Object getObjectFieldVal(Object object, Field field);

    /** Get a field value, boxing the value if necessary. */
    public static Object getFieldVal(Object object, Field field) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            return getIntFieldVal(object, field);
        } else if (fieldType == long.class) {
            return getLongFieldVal(object, field);
        } else if (fieldType == short.class) {
            return getShortFieldVal(object, field);
        } else if (fieldType == char.class) {
            return getCharFieldVal(object, field);
        } else if (fieldType == boolean.class) {
            return getBooleanFieldVal(object, field);
        } else if (fieldType == byte.class) {
            return getByteFieldVal(object, field);
        } else if (fieldType == float.class) {
            return getFloatFieldVal(object, field);
        } else if (fieldType == double.class) {
            return getDoubleFieldVal(object, field);
        } else {
            return getObjectFieldVal(object, field);
        }
    }

    public static native void setIntFieldVal(Object object, Field field, int val);

    public static native void setLongFieldVal(Object object, Field field, long val);

    public static native void setShortFieldVal(Object object, Field field, short val);

    public static native void setCharFieldVal(Object object, Field field, char val);

    public static native void setBooleanFieldVal(Object object, Field field, boolean val);

    public static native void setByteFieldVal(Object object, Field field, byte val);

    public static native void setFloatFieldVal(Object object, Field field, float val);

    public static native void setDoubleFieldVal(Object object, Field field, double val);

    public static native void setObjectFieldVal(Object object, Field field, Object val);

    /** Set a field value, unboxing the passed value if necessary. */
    public static void setFieldVal(Object object, Field field, Object val) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            setIntFieldVal(object, field, ((Integer) val).intValue());
        } else if (fieldType == long.class) {
            setLongFieldVal(object, field, ((Long) val).longValue());
        } else if (fieldType == short.class) {
            setShortFieldVal(object, field, ((Short) val).shortValue());
        } else if (fieldType == char.class) {
            setCharFieldVal(object, field, ((Character) val).charValue());
        } else if (fieldType == boolean.class) {
            setBooleanFieldVal(object, field, ((Boolean) val).booleanValue());
        } else if (fieldType == byte.class) {
            setByteFieldVal(object, field, ((Byte) val).byteValue());
        } else if (fieldType == float.class) {
            setFloatFieldVal(object, field, ((Float) val).floatValue());
        } else if (fieldType == double.class) {
            setDoubleFieldVal(object, field, ((Double) val).doubleValue());
        } else {
            setObjectFieldVal(object, field, val);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    public static native void callVoidMethod(Object object, Method method, Object... args);

    public static native int callIntMethod(Object object, Method method, Object... args);

    public static native long callLongMethod(Object object, Method method, Object... args);

    public static native short callShortMethod(Object object, Method method, Object... args);

    public static native char callCharMethod(Object object, Method method, Object... args);

    public static native boolean callBooleanMethod(Object object, Method method, Object... args);

    public static native byte callByteMethod(Object object, Method method, Object... args);

    public static native float callFloatMethod(Object object, Method method, Object... args);

    public static native double callDoubleMethod(Object object, Method method, Object... args);

    public static native Object callObjectMethod(Object object, Method method, Object... args);

    /** Call a method, boxing the result if necessary. */
    public static Object callMethod(Object object, Method method, Object... args) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            callVoidMethod(object, method, args);
            return null;
        } else if (returnType == int.class) {
            return callIntMethod(object, method, args);
        } else if (returnType == long.class) {
            return callLongMethod(object, method, args);
        } else if (returnType == short.class) {
            return callShortMethod(object, method, args);
        } else if (returnType == char.class) {
            return callCharMethod(object, method, args);
        } else if (returnType == boolean.class) {
            return callBooleanMethod(object, method, args);
        } else if (returnType == byte.class) {
            return callByteMethod(object, method, args);
        } else if (returnType == float.class) {
            return callFloatMethod(object, method, args);
        } else if (returnType == double.class) {
            return callDoubleMethod(object, method, args);
        } else {
            return callObjectMethod(object, method, args);
        }
    }
}
