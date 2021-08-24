package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Narcissus {
    static {
        Utils.loadLibraryFromJar("lib/narcissus.so");
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

    public static native int callIntMethod(Object object, Method method, Object... params);

    public static native long callLongMethod(Object object, Method method, Object... params);

    public static native short callShortMethod(Object object, Method method, Object... params);

    public static native char callCharMethod(Object object, Method method, Object... params);

    public static native boolean callBooleanMethod(Object object, Method method, Object... params);

    public static native byte callByteMethod(Object object, Method method, Object... params);

    public static native float callFloatMethod(Object object, Method method, Object... params);

    public static native double callDoubleMethod(Object object, Method method, Object... params);

    public static native void callVoidMethod(Object object, Method method, Object... params);

    public static native Object callObjectMethod(Object object, Method method, Object... params);

    public static Object callMethod(Object object, Method method, Object... params) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }
        if (params.length > 0) {
            throw new IllegalArgumentException("Params are not yet supported -- see narcissus.c");
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == int.class) {
            return callIntMethod(object, method, params);
        } else if (returnType == long.class) {
            return callLongMethod(object, method, params);
        } else if (returnType == short.class) {
            return callShortMethod(object, method, params);
        } else if (returnType == char.class) {
            return callCharMethod(object, method, params);
        } else if (returnType == boolean.class) {
            return callBooleanMethod(object, method, params);
        } else if (returnType == byte.class) {
            return callByteMethod(object, method, params);
        } else if (returnType == float.class) {
            return callFloatMethod(object, method, params);
        } else if (returnType == double.class) {
            return callDoubleMethod(object, method, params);
        } else {
            return callObjectMethod(object, method, params);
        }
    }
    
    // TODO: add methods like `int callIntMethod(...)` to avoid boxing on return
}
