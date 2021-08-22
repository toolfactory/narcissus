package narcissus;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        try {
            final ClassLoader classLoader = Test.class.getClassLoader();

            // Enumerate private fields
            List<Field> fields = new ArrayList<>();
            for (Class<?> cls = classLoader.getClass(); cls != null; cls = cls.getSuperclass()) {
                for (Field field : (Field[]) Narcissus.callObjectMethod(classLoader.getClass(),
                        "getDeclaredFields0", "java.lang.reflect.Field[]")) {
                    fields.add(field);
                }
            }
            System.out.println(fields);

            final Object fieldVal = Narcissus.getObjectFieldVal(classLoader, "ucp",
                    "jdk.internal.loader.URLClassPath");
            final URL[] methodResult = (URL[]) Narcissus.callObjectMethod(fieldVal, "getURLs", URL[].class);
            System.out.println(Arrays.toString(methodResult));
        } catch (NoSuchFieldError e) {
            throw new RuntimeException("Could not find field", e);
        }
    }
}