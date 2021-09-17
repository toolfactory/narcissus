package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Narcissus.
 */
public class Narcissus {
    private static AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * Load and initialize the native library. Call this once before calling any other Narcissus methods.
     * 
     * @throws IllegalArgumentException
     *             if the native library could not be loaded or initialized.
     */
    public static void init() throws IllegalArgumentException {
        if (loaded.compareAndSet(false, true)) {
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
                throw new IllegalArgumentException("No native library available for this operating system");
            }
            try {
                LibraryLoader.loadLibraryFromJar(libraryResourcePath);
                nativeInit();
            } catch (Throwable t) {
                throw new IllegalArgumentException("Could not load Narcissus native library " + libraryResourcePath,
                        t);
            }
        }
    }

    // Native init method (sets up global refs to primitive type classes, for speed)
    private static native void nativeInit();

    // -------------------------------------------------------------------------------------------------------------

    // Find a class by name with no security checks. Name should be of the form "java/lang/String",
    // or "[Ljava/lang/Object;" for an array class.
    private static native Class<?> findClassInternal(String classNameInternal);

    /**
     * Finds a class by name (e.g. {@code "com.xyz.MyClass"}) using the current classloader or the system
     * classloader, without any security checks. Finds array classes if the class name is of the form
     * {@code "com.xyz.MyClass[][]"}.
     *
     * @param className
     *            the class name
     * @return the class reference
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

    /**
     * Enumerate all fields in the given class, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @return a list of {@link Field} objects representing all fields declared by the class
     */
    public static List<Field> enumerateFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Field field : Narcissus.getDeclaredFields(c)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Enumerate all methods in the given class, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @return a list of {@link Method} objects representing all methods declared by the class
     */
    public static List<Method> enumerateMethods(Class<?> cls) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Method method : Narcissus.getDeclaredMethods(c)) {
                methods.add(method);
            }
        }
        return methods;
    }

    /**
     * Enumerate all constructors in the given class, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @return a list of {@link Constructor} objects representing all constructors declared by the class
     */
    public static List<Constructor<?>> enumerateConstructors(Class<?> cls) {
        List<Constructor<?>> constructors = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Constructor<?> constructor : Narcissus.getDeclaredConstructors(c)) {
                constructors.add(constructor);
            }
        }
        return constructors;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Find a field by name in the given class, ignoring visibility.
     *
     * @param cls
     *            the class
     * @param fieldName
     *            the field name.
     * @return the {@link Field}
     * @throws NoSuchFieldException
     *             if the class does not contain a field of the given name
     */
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

    /**
     * Find a method by name in the given class, ignoring visibility.
     *
     * @param cls
     *            the class
     * @param methodName
     *            the method name.
     * @param paramTypes
     *            the parameter types of the method.
     * @return the {@link Method}
     * @throws NoSuchMethodException
     *             if the class does not contain a method of the given name
     */
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

    /**
     * Get declared methods without any visibility or security checks.
     *
     * @param cls
     *            the class
     * @return the declared methods
     */
    public static native Method[] getDeclaredMethods(Class<?> cls);

    /**
     * Get declared constructors without any visibility or security checks.
     *
     * @param <T>
     *            the generic type
     * @param cls
     *            the class
     * @return the declared constructors
     */
    public static native <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

    /**
     * Get declared fields without any visibility or security checks.
     *
     * @param cls
     *            the class
     * @return the declared fields
     */
    public static native Field[] getDeclaredFields(Class<?> cls);

    // -------------------------------------------------------------------------------------------------------------

    // Object field getters and setters

    /**
     * Get the value of an int field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the int value of the field
     */
    public static native int getIntField(Object object, Field field);

    /**
     * Get the value of a long field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the long value of the field
     */
    public static native long getLongField(Object object, Field field);

    /**
     * Get the value of a short field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the short value of the field
     */
    public static native short getShortField(Object object, Field field);

    /**
     * Get the value of a char field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the char value of the field
     */
    public static native char getCharField(Object object, Field field);

    /**
     * Get the value of a boolean field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the boolean value of the field
     */
    public static native boolean getBooleanField(Object object, Field field);

    /**
     * Get the value of a byte field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the byte value of the field
     */
    public static native byte getByteField(Object object, Field field);

    /**
     * Get the value of a float field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the float value of the field.
     */
    public static native float getFloatField(Object object, Field field);

    /**
     * Get the value of a double field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the double value of the field
     */
    public static native double getDoubleField(Object object, Field field);

    /**
     * Get the value of an object field without any visibility or security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the value of the field
     */
    public static native Object getObjectField(Object object, Field field);

    /**
     * Get the value of an object field without any visibility or security checks, boxing the value if necessary.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the value of the field
     */
    public static Object getField(Object object, Field field) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is static, call getStaticField() instead");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            return getIntField(object, field);
        } else if (fieldType == long.class) {
            return getLongField(object, field);
        } else if (fieldType == short.class) {
            return getShortField(object, field);
        } else if (fieldType == char.class) {
            return getCharField(object, field);
        } else if (fieldType == boolean.class) {
            return getBooleanField(object, field);
        } else if (fieldType == byte.class) {
            return getByteField(object, field);
        } else if (fieldType == float.class) {
            return getFloatField(object, field);
        } else if (fieldType == double.class) {
            return getDoubleField(object, field);
        } else {
            return getObjectField(object, field);
        }
    }

    /**
     * Set the value of an int field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the int value to set
     */
    public static native void setIntField(Object object, Field field, int val);

    /**
     * Set the value of a long field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the long value to set
     */
    public static native void setLongField(Object object, Field field, long val);

    /**
     * Set the value of a short field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the short value to set
     */
    public static native void setShortField(Object object, Field field, short val);

    /**
     * Set the value of a char field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the char value to set
     */
    public static native void setCharField(Object object, Field field, char val);

    /**
     * Set the value of a boolean field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the boolean value to set
     */
    public static native void setBooleanField(Object object, Field field, boolean val);

    /**
     * Set the value of a byte field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the byte value to set
     */
    public static native void setByteField(Object object, Field field, byte val);

    /**
     * Set the value of a float field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the float value to set
     */
    public static native void setFloatField(Object object, Field field, float val);

    /**
     * Set the value of a double field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the double value to set
     */
    public static native void setDoubleField(Object object, Field field, double val);

    /**
     * Set the value of a object field without any visibility or security checks.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the value to set
     */
    public static native void setObjectField(Object object, Field field, Object val);

    /**
     * Set the value of an object field without any visibility or security checks, unboxing the passed value if
     * necessary.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the value to set
     */
    public static void setField(Object object, Field field, Object val) {
        if (object == null) {
            throw new NullPointerException("object cannot be null");
        }
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is static, call setStaticField() instead");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            setIntField(object, field, ((Integer) val).intValue());
        } else if (fieldType == long.class) {
            setLongField(object, field, ((Long) val).longValue());
        } else if (fieldType == short.class) {
            setShortField(object, field, ((Short) val).shortValue());
        } else if (fieldType == char.class) {
            setCharField(object, field, ((Character) val).charValue());
        } else if (fieldType == boolean.class) {
            setBooleanField(object, field, ((Boolean) val).booleanValue());
        } else if (fieldType == byte.class) {
            setByteField(object, field, ((Byte) val).byteValue());
        } else if (fieldType == float.class) {
            setFloatField(object, field, ((Float) val).floatValue());
        } else if (fieldType == double.class) {
            setDoubleField(object, field, ((Double) val).doubleValue());
        } else {
            setObjectField(object, field, val);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    // Static field getters and setters

    /**
     * Get the value of a static int field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the int value of the static field
     */
    public static native int getStaticIntField(Field field);

    /**
     * Get the value of a static long field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static long field
     */
    public static native long getStaticLongField(Field field);

    /**
     * Get the value of a static short field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static short field
     */
    public static native short getStaticShortField(Field field);

    /**
     * Get the value of a static char field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static char field
     */
    public static native char getStaticCharField(Field field);

    /**
     * Get the value of a static boolean field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static boolean field
     */
    public static native boolean getStaticBooleanField(Field field);

    /**
     * Get the value of a static byte field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static byte field
     */
    public static native byte getStaticByteField(Field field);

    /**
     * Get the value of a static float field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static float field
     */
    public static native float getStaticFloatField(Field field);

    /**
     * Get the value of a static double field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static double field
     */
    public static native double getStaticDoubleField(Field field);

    /**
     * Get the value of a static object field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @return the static object field
     */
    public static native Object getStaticObjectField(Field field);

    /**
     * Get the value of a static field without any visibility or security checks, boxing the value if necessary.
     *
     * @param field
     *            the static field
     * @return the static field
     */
    public static Object getStaticField(Field field) {
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is not static, call getField() instead");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            return getStaticIntField(field);
        } else if (fieldType == long.class) {
            return getStaticLongField(field);
        } else if (fieldType == short.class) {
            return getStaticShortField(field);
        } else if (fieldType == char.class) {
            return getStaticCharField(field);
        } else if (fieldType == boolean.class) {
            return getStaticBooleanField(field);
        } else if (fieldType == byte.class) {
            return getStaticByteField(field);
        } else if (fieldType == float.class) {
            return getStaticFloatField(field);
        } else if (fieldType == double.class) {
            return getStaticDoubleField(field);
        } else {
            return getStaticObjectField(field);
        }
    }

    /**
     * Set the value of a static int field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the int value to set
     */
    public static native void setStaticIntField(Field field, int val);

    /**
     * Set the value of a static long field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the long value to set
     */
    public static native void setStaticLongField(Field field, long val);

    /**
     * Set the value of a static short field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the short value to set
     */
    public static native void setStaticShortField(Field field, short val);

    /**
     * Set the value of a static char field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the char value to set
     */
    public static native void setStaticCharField(Field field, char val);

    /**
     * Set the value of a static boolean field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the boolean value to set
     */
    public static native void setStaticBooleanField(Field field, boolean val);

    /**
     * Set the value of a static byte field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the byte value to set
     */
    public static native void setStaticByteField(Field field, byte val);

    /**
     * Set the value of a static float field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the float value to set
     */
    public static native void setStaticFloatField(Field field, float val);

    /**
     * Set the value of a static double field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the double value to set
     */
    public static native void setStaticDoubleField(Field field, double val);

    /**
     * Set the value of a static object field without any visibility or security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the value to set
     */
    public static native void setStaticObjectField(Field field, Object val);

    /**
     * Set the value of a static field without any visibility or security checks, unboxing the passed value if
     * necessary.
     *
     * @param field
     *            the static field
     * @param val
     *            the value to set
     */
    public static void setStaticField(Field field, Object val) {
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is not static, call setField() instead");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            setStaticIntField(field, ((Integer) val).intValue());
        } else if (fieldType == long.class) {
            setStaticLongField(field, ((Long) val).longValue());
        } else if (fieldType == short.class) {
            setStaticShortField(field, ((Short) val).shortValue());
        } else if (fieldType == char.class) {
            setStaticCharField(field, ((Character) val).charValue());
        } else if (fieldType == boolean.class) {
            setStaticBooleanField(field, ((Boolean) val).booleanValue());
        } else if (fieldType == byte.class) {
            setStaticByteField(field, ((Byte) val).byteValue());
        } else if (fieldType == float.class) {
            setStaticFloatField(field, ((Float) val).floatValue());
        } else if (fieldType == double.class) {
            setStaticDoubleField(field, ((Double) val).doubleValue());
        } else {
            setStaticObjectField(field, val);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    // Invoke object methods:

    /**
     * Invoke a non-static void-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     */
    public static native void invokeVoidMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static int-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the int return value
     */
    public static native int invokeIntMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static long-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the long return value
     */
    public static native long invokeLongMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static short-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the short return value
     */
    public static native short invokeShortMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static char-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the char return value
     */
    public static native char invokeCharMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static boolean-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the boolean return value
     */
    public static native boolean invokeBooleanMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static byte-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the byte return value
     */
    public static native byte invokeByteMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static float-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the float return value
     */
    public static native float invokeFloatMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static double-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the double return value
     */
    public static native double invokeDoubleMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static {@link Object}-return-type method without any visibility or security checks.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value
     */
    public static native Object invokeObjectMethod(Object object, Method method, Object... args);

    /**
     * Invoke a non-static {@link Object}-return-type method without any visibility or security checks, boxing the
     * result if necessary.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value (possibly a boxed value)
     */
    public static Object invokeMethod(Object object, Method method, Object... args) {
        if (object == null) {
            throw new NullPointerException("object cannot be null");
        }
        if (method == null) {
            throw new NullPointerException("method cannot be null");
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("method is static, call invokeStaticMethod() instead");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            invokeVoidMethod(object, method, args);
            return null;
        } else if (returnType == int.class) {
            return invokeIntMethod(object, method, args);
        } else if (returnType == long.class) {
            return invokeLongMethod(object, method, args);
        } else if (returnType == short.class) {
            return invokeShortMethod(object, method, args);
        } else if (returnType == char.class) {
            return invokeCharMethod(object, method, args);
        } else if (returnType == boolean.class) {
            return invokeBooleanMethod(object, method, args);
        } else if (returnType == byte.class) {
            return invokeByteMethod(object, method, args);
        } else if (returnType == float.class) {
            return invokeFloatMethod(object, method, args);
        } else if (returnType == double.class) {
            return invokeDoubleMethod(object, method, args);
        } else {
            return invokeObjectMethod(object, method, args);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    // Invoke static methods:

    /**
     * Invoke a static void-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     */
    public static native void invokeStaticVoidMethod(Method method, Object... args);

    /**
     * Invoke a static int-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the int return value
     */
    public static native int invokeStaticIntMethod(Method method, Object... args);

    /**
     * Invoke a static long-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the long return value
     */
    public static native long invokeStaticLongMethod(Method method, Object... args);

    /**
     * Invoke a static short-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the short return value
     */
    public static native short invokeStaticShortMethod(Method method, Object... args);

    /**
     * Invoke a static char-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the char return value
     */
    public static native char invokeStaticCharMethod(Method method, Object... args);

    /**
     * Invoke a static boolean-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the boolean return value
     */
    public static native boolean invokeStaticBooleanMethod(Method method, Object... args);

    /**
     * Invoke a static byte-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the byte return value
     */
    public static native byte invokeStaticByteMethod(Method method, Object... args);

    /**
     * Invoke a static float-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the float return value
     */
    public static native float invokeStaticFloatMethod(Method method, Object... args);

    /**
     * Invoke a static double-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the double return value
     */
    public static native double invokeStaticDoubleMethod(Method method, Object... args);

    /**
     * Invoke a static {@link Object}-return-type method without any visibility or security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value
     */
    public static native Object invokeStaticObjectMethod(Method method, Object... args);

    /**
     * Invoke a static {@link Object}-return-type method without any visibility or security checks, boxing the
     * result if necessary.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value (possibly a boxed value)
     */
    public static Object invokeStaticMethod(Method method, Object... args) {
        if (method == null) {
            throw new NullPointerException("method cannot be null");
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("method is not static, call invokeMethod() instead");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            invokeStaticVoidMethod(method, args);
            return null;
        } else if (returnType == int.class) {
            return invokeStaticIntMethod(method, args);
        } else if (returnType == long.class) {
            return invokeStaticLongMethod(method, args);
        } else if (returnType == short.class) {
            return invokeStaticShortMethod(method, args);
        } else if (returnType == char.class) {
            return invokeStaticCharMethod(method, args);
        } else if (returnType == boolean.class) {
            return invokeStaticBooleanMethod(method, args);
        } else if (returnType == byte.class) {
            return invokeStaticByteMethod(method, args);
        } else if (returnType == float.class) {
            return invokeStaticFloatMethod(method, args);
        } else if (returnType == double.class) {
            return invokeStaticDoubleMethod(method, args);
        } else {
            return invokeStaticObjectMethod(method, args);
        }
    }

}
