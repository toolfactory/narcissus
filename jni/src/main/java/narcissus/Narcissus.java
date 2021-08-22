package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Narcissus {
    static {
        Utils.loadLibraryFromJar("lib/narcissus.so");
    }

    // -------------------------------------------------------------------------------------------------------------

    private static native Object nativeGetObjectFieldVal(Object object, String fieldName, String fieldSignature);

    public static Object getObjectFieldVal(Object object, String fieldName, String classNameOfFieldType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeGetObjectFieldVal(object, fieldName, Utils.getClassTypeSignature(classNameOfFieldType));
    }

    public static Object getObjectFieldVal(Object object, String fieldName, Class<?> fieldType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeGetObjectFieldVal(object, fieldName, Utils.getClassTypeSignature(fieldType));
    }

    // -------------------------------------------------------------------------------------------------------------

    private static native Object nativeCallObjectMethod(Object object, String methodName, String methodSignature);

    public static Object callObjectMethod(Object object, String methodName, String classNameOfMethodReturnType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeCallObjectMethod(object, methodName,
                "()" + Utils.getClassTypeSignature(classNameOfMethodReturnType));
    }

    public static Object callObjectMethod(Object object, String methodName, Class<?> methodReturnType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeCallObjectMethod(object, methodName, "()" + Utils.getClassTypeSignature(methodReturnType));
    }

    // -------------------------------------------------------------------------------------------------------------

    public static native Method[] nativeGetDeclaredMethods(Class<?> cls);

    public static native <T> Constructor<T>[] nativeGetDeclaredConstructors(Class<T> cls);

    public static native Field[] nativeGetDeclaredFields(Class<?> cls);

}
