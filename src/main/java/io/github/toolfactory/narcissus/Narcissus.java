/*
 * This file is part of Narcissus.
 *
 * Hosted at: https://github.com/toolfactory/narcissus
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.toolfactory.narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Narcissus reflection library.
 * 
 * @author Luke Hutchison
 */
public class Narcissus {
    /**
     * Whether or not the library was successfully loaded. If this value is false, you will get
     * {@link UnsatisfiedLinkError} when you try calling methods in this class.
     */
    public static final boolean libraryLoaded;

    private static interface Getter<T> {
        T get(Class<?> cls);
    }

    private static Getter<Method[]> getDeclaredMethods;

    private static Getter<Constructor<?>[]> getDeclaredConstructors;

    private static Getter<Field[]> getDeclaredFields;

    // Whether or not to print error to stderr if library could not be loaded.
    private static final boolean DEBUG = true;

    // Load and initialize the native library.
    static {
        boolean loaded = false;
        try {
            final String libraryResourcePrefix = "lib/libnarcissus";
            final String libraryResourceSuffix;
            switch (LibraryLoader.OS) {
            case Linux:
                libraryResourceSuffix = "-linux-" + LibraryLoader.archType + ".so";
                break;
            case MacOSX:
                libraryResourceSuffix = "-macos-" + LibraryLoader.archType + ".dylib";
                break;
            case Windows:
                libraryResourceSuffix = "-win-" + LibraryLoader.archType + ".dll";
                break;
            case BSD:
            case Solaris:
            case Unix:
            case Unknown:
            default:
                throw new IllegalArgumentException("No native library available for this operating system");
            }

            // Try loading dynamic library
            LibraryLoader.loadLibraryFromJar(libraryResourcePrefix + libraryResourceSuffix);

            // Try finding necessary fields and methods
            final Class<?> Class_class = findClass("java.lang.Class");
            try {
                // For Oracle JDK
                final Method getDeclaredMethods_method = findMethodInternal(Class_class, "getDeclaredMethods0",
                        "(Z)[Ljava/lang/reflect/Method;", /* isStatic = */ false);
                getDeclaredMethods = new Getter<Method[]>() {
                    @Override
                    public Method[] get(final Class<?> cls) {
                        return (Method[]) invokeObjectMethod(cls, getDeclaredMethods_method, false);
                    }
                };
            } catch (final Throwable t) {
                // For IBM Semeru
                final Method getDeclaredMethods_method = findMethodInternal(Class_class, "getDeclaredMethodsImpl",
                        "()[Ljava/lang/reflect/Method;", /* isStatic = */ false);
                getDeclaredMethods = new Getter<Method[]>() {
                    @Override
                    public Method[] get(final Class<?> cls) {
                        return (Method[]) invokeObjectMethod(cls, getDeclaredMethods_method);
                    }
                };
            }
            try {
                // For Oracle JDK
                final Method getDeclaredConstructors_method = findMethodInternal(Class_class,
                        "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;", /* isStatic = */ false);
                getDeclaredConstructors = new Getter<Constructor<?>[]>() {
                    @Override
                    public Constructor<?>[] get(final Class<?> cls) {
                        return (Constructor<?>[]) invokeObjectMethod(cls, getDeclaredConstructors_method, false);
                    }
                };
            } catch (final Throwable t) {
                // For IBM Semeru
                final Method getDeclaredConstructors_method = findMethodInternal(Class_class,
                        "getDeclaredConstructorsImpl", "()[Ljava/lang/reflect/Constructor;",
                        /* isStatic = */ false);
                getDeclaredConstructors = new Getter<Constructor<?>[]>() {
                    @Override
                    public Constructor<?>[] get(final Class<?> cls) {
                        return (Constructor<?>[]) invokeObjectMethod(cls, getDeclaredConstructors_method);
                    }
                };
            }
            try {
                // For Oracle JDK
                final Method getDeclaredFields_method = findMethodInternal(Class_class, "getDeclaredFields0",
                        "(Z)[Ljava/lang/reflect/Field;", /* isStatic = */ false);
                getDeclaredFields = new Getter<Field[]>() {
                    @Override
                    public Field[] get(final Class<?> cls) {
                        return (Field[]) invokeObjectMethod(cls, getDeclaredFields_method, false);
                    }
                };
            } catch (final Throwable t) {
                // For IBM Semeru
                final Method getDeclaredFields_method = findMethodInternal(Class_class, "getDeclaredFieldsImpl",
                        "()[Ljava/lang/reflect/Field;", /* isStatic = */ false);
                getDeclaredFields = new Getter<Field[]>() {
                    @Override
                    public Field[] get(final Class<?> cls) {
                        return (Field[]) invokeObjectMethod(cls, getDeclaredFields_method);
                    }
                };
            }

            loaded = true;

        } catch (final Throwable t) {
            if (DEBUG) {
                System.err.println("Could not load Narcissus native library: " + t.getMessage());
            }
        }
        libraryLoaded = loaded;
    }

    // -------------------------------------------------------------------------------------------------------------

    // Find a class by name with no security checks. Name should be of the form "java/lang/String",
    // or "[Ljava/lang/Object;" for an array class.
    private static native Class<?> findClassInternal(String classNameInternal);

    // Find a method by name and Java-internal signature, with no security checks.
    private static native Method findMethodInternal(Class<?> cls, String methodName, String sig, boolean isStatic);

    // Find a field by name and Java-internal signature, with no security checks.
    private static native Field findFieldInternal(Class<?> cls, String fieldName, String sig, boolean isStatic);

    /**
     * Finds a class by name (e.g. {@code "com.xyz.MyClass"}) using the current classloader or the system
     * classloader, ignoring visibility and bypassing security checks. Finds array classes if the class name is of
     * the form {@code "com.xyz.MyClass[][]"}.
     *
     * @param className
     *            the class name
     * @return the class reference
     */
    public static Class<?> findClass(final String className) {
        if (className == null) {
            throw new IllegalArgumentException("Class name cannot be null");
        }
        String classNameInternal = className.replace('.', '/');
        String arrayDims = "";
        while (classNameInternal.endsWith("[]")) {
            arrayDims += '[';
            classNameInternal = classNameInternal.substring(0, classNameInternal.length() - 2);
        }
        return findClassInternal(
                arrayDims.isEmpty() ? classNameInternal : arrayDims + 'L' + classNameInternal + ';');
    }

    // -------------------------------------------------------------------------------------------------------------

    // Methods required by toolfactory/jvm-driver

    /**
     * Allocate an object instance, without calling any constructor. (For internal use.)
     *
     * @param cls
     *            the class to instantiate
     * @return the new object instance
     */
    public static native Object allocateInstance(Class<?> cls);

    /**
     * Throw a {@link Throwable} without requiring a throws declaration. (For internal use.)
     *
     * @param throwable
     *            the {@link Throwable} to throw
     */
    public static native void sneakyThrow(Throwable throwable);

    // -------------------------------------------------------------------------------------------------------------

    /** Iterator applied to each method of a class and its superclasses/interfaces. */
    private static interface MethodIterator {
        /** @return true to stop iterating, or false to continue iterating */
        boolean foundMethod(Method m);
    }

    /**
     * Iterate through all methods in the given class, ignoring visibility and bypassing security checks. Also
     * iterates up through superclasses, to collect all methods of the class and its superclasses.
     *
     * @param cls
     *            the class
     */
    private static void forAllMethods(final Class<?> cls, final MethodIterator methodIter) {
        // Iterate from class to its superclasses, and find initial interfaces to start traversing from
        final Set<Class<?>> visited = new HashSet<>();
        final LinkedList<Class<?>> interfaceQueue = new LinkedList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (final Method m : getDeclaredMethods(c)) {
                if (methodIter.foundMethod(m)) {
                    return;
                }
            }
            // Find interfaces and superinterfaces implemented by this class or its superclasses
            if (c.isInterface() && visited.add(c)) {
                interfaceQueue.add(c);
            }
            for (final Class<?> iface : c.getInterfaces()) {
                if (visited.add(iface)) {
                    interfaceQueue.add(iface);
                }
            }
        }
        // Traverse through interfaces looking for default methods
        while (!interfaceQueue.isEmpty()) {
            final Class<?> iface = interfaceQueue.remove();
            for (final Method m : getDeclaredMethods(iface)) {
                if (methodIter.foundMethod(m)) {
                    return;
                }
            }
            for (final Class<?> superIface : iface.getInterfaces()) {
                if (visited.add(superIface)) {
                    interfaceQueue.add(superIface);
                }
            }
        }
    }

    /**
     * Find a method by name and parameter types in the given class, ignoring visibility and bypassing security
     * checks.
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
    public static Method findMethod(final Class<?> cls, final String methodName, final Class<?>... paramTypes)
            throws NoSuchMethodException {
        final AtomicReference<Method> method = new AtomicReference<>();
        forAllMethods(cls, new MethodIterator() {
            @Override
            public boolean foundMethod(final Method m) {
                if (m.getName().equals(methodName) && Arrays.equals(paramTypes, m.getParameterTypes())) {
                    method.set(m);
                    return true;
                }
                return false;
            }
        });
        final Method m = method.get();
        if (m != null) {
            return m;
        } else {
            throw new NoSuchMethodException(methodName);
        }
    }

    /**
     * Find a constructor by parameter types in the given class, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @param paramTypes
     *            the parameter types of the constructor.
     * @return the {@link Method}
     * @throws NoSuchMethodException
     *             if the class does not contain a constructor of the given name
     */
    public static Constructor<?> findConstructor(final Class<?> cls, final Class<?>... paramTypes)
            throws NoSuchMethodException {
        for (final Constructor<?> c : getDeclaredConstructors(cls)) {
            if (Arrays.equals(paramTypes, c.getParameterTypes())) {
                return c;
            }
        }
        throw new NoSuchMethodException(cls.getName());
    }

    /**
     * Enumerate all methods in the given class, ignoring visibility and bypassing security checks. Also iterates up
     * through superclasses, to collect all methods of the class and its superclasses.
     *
     * @param cls
     *            the class
     * @return a list of {@link Method} objects representing all methods declared by the class or a superclass.
     */
    public static List<Method> enumerateMethods(final Class<?> cls) {
        final List<Method> methodOrder = new ArrayList<>();
        forAllMethods(cls, new MethodIterator() {
            @Override
            public boolean foundMethod(final Method m) {
                methodOrder.add(m);
                return false;
            }
        });
        return methodOrder;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Find a field by name in the given class, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @param fieldName
     *            the field name.
     * @return the {@link Field}
     * @throws NoSuchFieldException
     *             if the class does not contain a field of the given name
     */
    public static Field findField(final Class<?> cls, final String fieldName) throws NoSuchFieldException {
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (final Field field : getDeclaredFields(c)) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * Enumerate all fields in the given class, ignoring visibility and bypassing security checks. Also iterates up
     * through superclasses, to collect all fields of the class and its superclasses.
     *
     * @param cls
     *            the class
     * @return a list of {@link Field} objects representing all fields declared by the class or a superclass.
     */
    public static List<Field> enumerateFields(final Class<?> cls) {
        final List<Field> fields = new ArrayList<>();
        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (final Field field : getDeclaredFields(c)) {
                fields.add(field);
            }
        }
        return fields;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Get declared methods, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @return the declared methods
     */
    public static Method[] getDeclaredMethods(final Class<?> cls) {
        return getDeclaredMethods.get(cls);
    }

    /**
     * Get declared constructors, ignoring visibility and bypassing security checks.
     *
     * @param <T>
     *            the generic type
     * @param cls
     *            the class
     * @return the declared constructors
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getDeclaredConstructors(final Class<T> cls) {
        return (Constructor<T>[]) getDeclaredConstructors.get(cls);
    }

    /**
     * Get declared fields, ignoring visibility and bypassing security checks.
     *
     * @param cls
     *            the class
     * @return the declared fields
     */
    public static Field[] getDeclaredFields(final Class<?> cls) {
        return getDeclaredFields.get(cls);
    }

    // -------------------------------------------------------------------------------------------------------------

    // Object field getters and setters

    /**
     * Get the value of an int field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the int value of the field
     */
    public static native int getIntField(Object object, Field field);

    /**
     * Get the value of a long field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the long value of the field
     */
    public static native long getLongField(Object object, Field field);

    /**
     * Get the value of a short field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the short value of the field
     */
    public static native short getShortField(Object object, Field field);

    /**
     * Get the value of a char field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the char value of the field
     */
    public static native char getCharField(Object object, Field field);

    /**
     * Get the value of a boolean field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the boolean value of the field
     */
    public static native boolean getBooleanField(Object object, Field field);

    /**
     * Get the value of a byte field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the byte value of the field
     */
    public static native byte getByteField(Object object, Field field);

    /**
     * Get the value of a float field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the float value of the field.
     */
    public static native float getFloatField(Object object, Field field);

    /**
     * Get the value of a double field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the double value of the field
     */
    public static native double getDoubleField(Object object, Field field);

    /**
     * Get the value of an object field, ignoring visibility and bypassing security checks.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the value of the field
     */
    public static native Object getObjectField(Object object, Field field);

    /**
     * Get the value of an object field, ignoring visibility and bypassing security checks, boxing the value if
     * necessary.
     *
     * @param object
     *            the object instance to get the field value from
     * @param field
     *            the non-static field
     * @return the value of the field
     */
    public static Object getField(final Object object, final Field field) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is static, call getStaticField() instead");
        }
        final Class<?> fieldType = field.getType();
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
     * Set the value of an int field, ignoring visibility and bypassing security checks.
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
     * Set the value of a long field, ignoring visibility and bypassing security checks.
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
     * Set the value of a short field, ignoring visibility and bypassing security checks.
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
     * Set the value of a char field, ignoring visibility and bypassing security checks.
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
     * Set the value of a boolean field, ignoring visibility and bypassing security checks.
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
     * Set the value of a byte field, ignoring visibility and bypassing security checks.
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
     * Set the value of a float field, ignoring visibility and bypassing security checks.
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
     * Set the value of a double field, ignoring visibility and bypassing security checks.
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
     * Set the value of a object field, ignoring visibility and bypassing security checks.
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
     * Set the value of an object field, ignoring visibility and bypassing security checks, unboxing the passed
     * value if necessary.
     *
     * @param object
     *            the object instance in which to set the field value
     * @param field
     *            the non-static field
     * @param val
     *            the value to set
     */
    public static void setField(final Object object, final Field field, final Object val) {
        if (object == null) {
            throw new NullPointerException("object cannot be null");
        }
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is static, call setStaticField() instead");
        }
        final Class<?> fieldType = field.getType();
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
     * Get the value of a static int field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the int value of the static field
     */
    public static native int getStaticIntField(Field field);

    /**
     * Get the value of a static long field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static long field
     */
    public static native long getStaticLongField(Field field);

    /**
     * Get the value of a static short field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static short field
     */
    public static native short getStaticShortField(Field field);

    /**
     * Get the value of a static char field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static char field
     */
    public static native char getStaticCharField(Field field);

    /**
     * Get the value of a static boolean field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static boolean field
     */
    public static native boolean getStaticBooleanField(Field field);

    /**
     * Get the value of a static byte field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static byte field
     */
    public static native byte getStaticByteField(Field field);

    /**
     * Get the value of a static float field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static float field
     */
    public static native float getStaticFloatField(Field field);

    /**
     * Get the value of a static double field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static double field
     */
    public static native double getStaticDoubleField(Field field);

    /**
     * Get the value of a static object field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @return the static object field
     */
    public static native Object getStaticObjectField(Field field);

    /**
     * Get the value of a static field, ignoring visibility and bypassing security checks, boxing the value if
     * necessary.
     *
     * @param field
     *            the static field
     * @return the static field
     */
    public static Object getStaticField(final Field field) {
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is not static, call getField() instead");
        }
        final Class<?> fieldType = field.getType();
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
     * Set the value of a static int field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the int value to set
     */
    public static native void setStaticIntField(Field field, int val);

    /**
     * Set the value of a static long field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the long value to set
     */
    public static native void setStaticLongField(Field field, long val);

    /**
     * Set the value of a static short field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the short value to set
     */
    public static native void setStaticShortField(Field field, short val);

    /**
     * Set the value of a static char field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the char value to set
     */
    public static native void setStaticCharField(Field field, char val);

    /**
     * Set the value of a static boolean field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the boolean value to set
     */
    public static native void setStaticBooleanField(Field field, boolean val);

    /**
     * Set the value of a static byte field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the byte value to set
     */
    public static native void setStaticByteField(Field field, byte val);

    /**
     * Set the value of a static float field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the float value to set
     */
    public static native void setStaticFloatField(Field field, float val);

    /**
     * Set the value of a static double field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the double value to set
     */
    public static native void setStaticDoubleField(Field field, double val);

    /**
     * Set the value of a static object field, ignoring visibility and bypassing security checks.
     *
     * @param field
     *            the static field
     * @param val
     *            the value to set
     */
    public static native void setStaticObjectField(Field field, Object val);

    /**
     * Set the value of a static field, ignoring visibility and bypassing security checks, unboxing the passed value
     * if necessary.
     *
     * @param field
     *            the static field
     * @param val
     *            the value to set
     */
    public static void setStaticField(final Field field, final Object val) {
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("field is not static, call setField() instead");
        }
        final Class<?> fieldType = field.getType();
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
     * Invoke a non-static void-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static int-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static long-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static short-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static char-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static boolean-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static byte-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static float-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static double-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static {@link Object}-return-type method, ignoring visibility and bypassing security checks.
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
     * Invoke a non-static {@link Object}-return-type method, ignoring visibility and bypassing security checks,
     * boxing the result if necessary.
     *
     * @param object
     *            the object instance to invoke the method on
     * @param method
     *            the non-static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value (possibly a boxed value)
     */
    public static Object invokeMethod(final Object object, final Method method, final Object... args) {
        if (object == null) {
            throw new NullPointerException("object cannot be null");
        }
        if (method == null) {
            throw new NullPointerException("method cannot be null");
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("method is static, call invokeStaticMethod() instead");
        }
        final Class<?> returnType = method.getReturnType();
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
     * Invoke a static void-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     */
    public static native void invokeStaticVoidMethod(Method method, Object... args);

    /**
     * Invoke a static int-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the int return value
     */
    public static native int invokeStaticIntMethod(Method method, Object... args);

    /**
     * Invoke a static long-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the long return value
     */
    public static native long invokeStaticLongMethod(Method method, Object... args);

    /**
     * Invoke a static short-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the short return value
     */
    public static native short invokeStaticShortMethod(Method method, Object... args);

    /**
     * Invoke a static char-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the char return value
     */
    public static native char invokeStaticCharMethod(Method method, Object... args);

    /**
     * Invoke a static boolean-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the boolean return value
     */
    public static native boolean invokeStaticBooleanMethod(Method method, Object... args);

    /**
     * Invoke a static byte-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the byte return value
     */
    public static native byte invokeStaticByteMethod(Method method, Object... args);

    /**
     * Invoke a static float-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the float return value
     */
    public static native float invokeStaticFloatMethod(Method method, Object... args);

    /**
     * Invoke a static double-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the double return value
     */
    public static native double invokeStaticDoubleMethod(Method method, Object... args);

    /**
     * Invoke a static {@link Object}-return-type method, ignoring visibility and bypassing security checks.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value
     */
    public static native Object invokeStaticObjectMethod(Method method, Object... args);

    /**
     * Invoke a static {@link Object}-return-type method, ignoring visibility and bypassing security checks, boxing
     * the result if necessary.
     *
     * @param method
     *            the static method
     * @param args
     *            the method arguments (or {@code new Object[0]} if there are no args)
     * @return the return value (possibly a boxed value)
     */
    public static Object invokeStaticMethod(final Method method, final Object... args) {
        if (method == null) {
            throw new NullPointerException("method cannot be null");
        }
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("method is not static, call invokeMethod() instead");
        }
        final Class<?> returnType = method.getReturnType();
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
