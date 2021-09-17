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

    // Find a class by name with no security checks. Name should be of the form "java/lang/String",
    // or "[Ljava/lang/Object;" for an array class.
    private static native Class<?> findClassInternal(String classNameInternal);

    /**
     * Finds a class by name (e.g. {@code "com.xyz.MyClass"}) using the current classloader or the system
     * classloader, without any security checks. Finds array classes if the class name is of the form
     * {@code "com.xyz.MyClass[][]"}.
     */
    public static Class<?> findClass(String className) {
        String classNameInternal = className.replace('.', '/');
        if (classNameInternal.endsWith("[]")) {
            classNameInternal = 'L' + classNameInternal;
            do {
                classNameInternal = '[' + classNameInternal.substring(0, classNameInternal.length() - 2);
            } while (classNameInternal.endsWith("[]"));
            classNameInternal += ';';
        }
        return findClassInternal(classNameInternal);
    }

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

    /** Get declared methods without any visibility checks. */
    public static native Method[] getDeclaredMethods(Class<?> cls);

    /** Get declared constructors without any visibility checks. */
    public static native <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

    /** Get declared fields without any visibility checks. */
    public static native Field[] getDeclaredFields(Class<?> cls);

    // -------------------------------------------------------------------------------------------------------------

    // Object field getters and setters
    
    public static native int getIntFieldVal(Object object, Field field);

    public static native long getLongFieldVal(Object object, Field field);

    public static native short getShortFieldVal(Object object, Field field);

    public static native char getCharFieldVal(Object object, Field field);

    public static native boolean getBooleanFieldVal(Object object, Field field);

    public static native byte getByteFieldVal(Object object, Field field);

    public static native float getFloatFieldVal(Object object, Field field);

    public static native double getDoubleFieldVal(Object object, Field field);

    public static native Object getObjectFieldVal(Object object, Field field);

    /** Get an object field value, boxing the value if necessary. */
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

    /** Set an object field value, unboxing the passed value if necessary. */
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

    // Static field getters and setters
    
    public static native int getStaticIntFieldVal(Field field);

    public static native long getStaticLongFieldVal(Field field);

    public static native short getStaticShortFieldVal(Field field);

    public static native char getStaticCharFieldVal(Field field);

    public static native boolean getStaticBooleanFieldVal(Field field);

    public static native byte getStaticByteFieldVal(Field field);

    public static native float getStaticFloatFieldVal(Field field);

    public static native double getStaticDoubleFieldVal(Field field);

    public static native Object getStaticObjectFieldVal(Field field);

    /** Get a static field value, boxing the value if necessary. */
    public static Object getStaticFieldVal(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            return getStaticIntFieldVal(field);
        } else if (fieldType == long.class) {
            return getStaticLongFieldVal(field);
        } else if (fieldType == short.class) {
            return getStaticShortFieldVal(field);
        } else if (fieldType == char.class) {
            return getStaticCharFieldVal(field);
        } else if (fieldType == boolean.class) {
            return getStaticBooleanFieldVal(field);
        } else if (fieldType == byte.class) {
            return getStaticByteFieldVal(field);
        } else if (fieldType == float.class) {
            return getStaticFloatFieldVal(field);
        } else if (fieldType == double.class) {
            return getStaticDoubleFieldVal(field);
        } else {
            return getStaticObjectFieldVal(field);
        }
    }

    public static native void setStaticIntFieldVal(Field field, int val);

    public static native void setStaticLongFieldVal(Field field, long val);

    public static native void setStaticShortFieldVal(Field field, short val);

    public static native void setStaticCharFieldVal(Field field, char val);

    public static native void setStaticBooleanFieldVal(Field field, boolean val);

    public static native void setStaticByteFieldVal(Field field, byte val);

    public static native void setStaticFloatFieldVal(Field field, float val);

    public static native void setStaticDoubleFieldVal(Field field, double val);

    public static native void setStaticObjectFieldVal(Field field, Object val);

    /** Set a static field value, unboxing the passed value if necessary. */
    public static void setStaticFieldVal(Field field, Object val) {
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            setStaticIntFieldVal(field, ((Integer) val).intValue());
        } else if (fieldType == long.class) {
            setStaticLongFieldVal(field, ((Long) val).longValue());
        } else if (fieldType == short.class) {
            setStaticShortFieldVal(field, ((Short) val).shortValue());
        } else if (fieldType == char.class) {
            setStaticCharFieldVal(field, ((Character) val).charValue());
        } else if (fieldType == boolean.class) {
            setStaticBooleanFieldVal(field, ((Boolean) val).booleanValue());
        } else if (fieldType == byte.class) {
            setStaticByteFieldVal(field, ((Byte) val).byteValue());
        } else if (fieldType == float.class) {
            setStaticFloatFieldVal(field, ((Float) val).floatValue());
        } else if (fieldType == double.class) {
            setStaticDoubleFieldVal(field, ((Double) val).doubleValue());
        } else {
            setStaticObjectFieldVal(field, val);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    // Call object methods:
    
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

    /** Call an object method, boxing the result if necessary. */
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
    
    // -------------------------------------------------------------------------------------------------------------

    // Call static methods:

    public static native void callStaticVoidMethod(Method method, Object... args);

    public static native int callStaticIntMethod(Method method, Object... args);

    public static native long callStaticLongMethod(Method method, Object... args);

    public static native short callStaticShortMethod(Method method, Object... args);

    public static native char callStaticCharMethod(Method method, Object... args);

    public static native boolean callStaticBooleanMethod(Method method, Object... args);

    public static native byte callStaticByteMethod(Method method, Object... args);

    public static native float callStaticFloatMethod(Method method, Object... args);

    public static native double callStaticDoubleMethod(Method method, Object... args);

    public static native Object callStaticObjectMethod(Method method, Object... args);

    /** Call a static method, boxing the result if necessary. */
    public static Object callStaticMethod(Method method, Object... args) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            callStaticVoidMethod(method, args);
            return null;
        } else if (returnType == int.class) {
            return callStaticIntMethod(method, args);
        } else if (returnType == long.class) {
            return callStaticLongMethod(method, args);
        } else if (returnType == short.class) {
            return callStaticShortMethod(method, args);
        } else if (returnType == char.class) {
            return callStaticCharMethod(method, args);
        } else if (returnType == boolean.class) {
            return callStaticBooleanMethod(method, args);
        } else if (returnType == byte.class) {
            return callStaticByteMethod(method, args);
        } else if (returnType == float.class) {
            return callStaticFloatMethod(method, args);
        } else if (returnType == double.class) {
            return callStaticDoubleMethod(method, args);
        } else {
            return callStaticObjectMethod(method, args);
        }
    }

}
