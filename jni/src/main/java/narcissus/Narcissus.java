package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Narcissus {
    static {
        Utils.loadLibraryFromJar("lib/narcissus.so");
    }

    // -------------------------------------------------------------------------------------------------------------

    public static native Method[] nativeGetDeclaredMethods(Class<?> cls);

    public static native <T> Constructor<T>[] nativeGetDeclaredConstructors(Class<T> cls);

    public static native Field[] nativeGetDeclaredFields(Class<?> cls);

    // -------------------------------------------------------------------------------------------------------------

    private static native int nativeGetIntFieldVal(Object object, Field field);

    private static native long nativeGetLongFieldVal(Object object, Field field);

    private static native short nativeGetShortFieldVal(Object object, Field field);

    private static native char nativeGetCharFieldVal(Object object, Field field);

    private static native boolean nativeGetBooleanFieldVal(Object object, Field field);

    private static native byte nativeGetByteFieldVal(Object object, Field field);

    private static native float nativeGetFloatFieldVal(Object object, Field field);

    private static native double nativeGetDoubleFieldVal(Object object, Field field);

    private static native Object nativeGetObjectFieldVal(Object object, Field field);

    public static Object getFieldVal(Object object, Field field) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (field == null) {
            throw new IllegalArgumentException("field cannot be null");
        }
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            return nativeGetIntFieldVal(object, field);
        } else if (fieldType == long.class) {
            return nativeGetLongFieldVal(object, field);
        } else if (fieldType == short.class) {
            return nativeGetShortFieldVal(object, field);
        } else if (fieldType == char.class) {
            return nativeGetCharFieldVal(object, field);
        } else if (fieldType == boolean.class) {
            return nativeGetBooleanFieldVal(object, field);
        } else if (fieldType == byte.class) {
            return nativeGetByteFieldVal(object, field);
        } else if (fieldType == float.class) {
            return nativeGetFloatFieldVal(object, field);
        } else if (fieldType == double.class) {
            return nativeGetDoubleFieldVal(object, field);
        } else {
            return nativeGetObjectFieldVal(object, field);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    private static native int nativeCallIntMethod(Object object, Method method, Object... params);

    private static native long nativeCallLongMethod(Object object, Method method, Object... params);

    private static native short nativeCallShortMethod(Object object, Method method, Object... params);

    private static native char nativeCallCharMethod(Object object, Method method, Object... params);

    private static native boolean nativeCallBooleanMethod(Object object, Method method, Object... params);

    private static native byte nativeCallByteMethod(Object object, Method method, Object... params);

    private static native float nativeCallFloatMethod(Object object, Method method, Object... params);

    private static native double nativeCallDoubleMethod(Object object, Method method, Object... params);

    private static native void nativeCallVoidMethod(Object object, Method method, Object... params);

    private static native Object nativeCallObjectMethod(Object object, Method method, Object... params);

    public static Object callMethod(Object object, Method method, Object... params) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }
        if (params.length > 0) {
            throw new IllegalArgumentException("Params are not yet supported -- see narcissus.c");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == int.class) {
            return nativeCallIntMethod(object, method, params);
        } else if (returnType == long.class) {
            return nativeCallLongMethod(object, method, params);
        } else if (returnType == short.class) {
            return nativeCallShortMethod(object, method, params);
        } else if (returnType == char.class) {
            return nativeCallCharMethod(object, method, params);
        } else if (returnType == boolean.class) {
            return nativeCallBooleanMethod(object, method, params);
        } else if (returnType == byte.class) {
            return nativeCallByteMethod(object, method, params);
        } else if (returnType == float.class) {
            return nativeCallFloatMethod(object, method, params);
        } else if (returnType == double.class) {
            return nativeCallDoubleMethod(object, method, params);
        } else {
            return nativeCallObjectMethod(object, method, params);
        }
    }
}
