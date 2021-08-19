package narcissus;

public class Narcissus {
    static {
        Utils.loadLibraryFromJar("lib/narcissus.so");
    }

    private static native Object nativeGetObjectFieldVal(Object object, String fieldName, String fieldSignature);

    public static Object getObjectFieldVal(Object object, String fieldName, String classNameOfFieldType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeGetObjectFieldVal(Test.class.getClassLoader(), fieldName,
                "L" + classNameOfFieldType.replace('.', '/') + ";");
    }

    public static Object getObjectFieldVal(Object object, String fieldName, Class<?> fieldType) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        return nativeGetObjectFieldVal(Test.class.getClassLoader(), fieldName,
                Utils.getFieldTypeSignature(fieldType));
    }
}