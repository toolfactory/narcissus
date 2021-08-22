package narcissus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            final ClassLoader classLoader = Test.class.getClassLoader();

            // Enumerate all fields
            List<Field> fields = new ArrayList<>();
            for (Class<?> cls = classLoader.getClass(); cls != null; cls = cls.getSuperclass()) {
                for (Field field : Narcissus.nativeGetDeclaredFields(classLoader.getClass())) {
                    fields.add(field);
                    System.out.println("Field: " + field);
                }
            }

            // Enumerate all methods
            List<Method> methods = new ArrayList<>();
            for (Class<?> cls = classLoader.getClass(); cls != null; cls = cls.getSuperclass()) {
                for (Method method : Narcissus.nativeGetDeclaredMethods(classLoader.getClass())) {
                    methods.add(method);
                    System.out.println("Method: " + method);
                }
            }

            // Enumerate all constructors
            List<Constructor<?>> constructors = new ArrayList<>();
            for (Class<?> cls = classLoader.getClass(); cls != null; cls = cls.getSuperclass()) {
                for (Constructor<?> constructor : Narcissus.nativeGetDeclaredConstructors(classLoader.getClass())) {
                    constructors.add(constructor);
                    System.out.println("Constructor: " + constructor);
                }
            }

            final Object fieldVal = Narcissus.getObjectFieldVal(classLoader, "ucp",
                    "jdk.internal.loader.URLClassPath");
            for (URL url : (URL[]) Narcissus.callObjectMethod(fieldVal, "getURLs", URL[].class)) {
                System.out.println("Classpath URL: " + url);
            }

        } catch (NoSuchFieldError e) {
            throw new RuntimeException("Could not find field", e);
        }
    }
}