package narcissus;

import java.net.URL;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        try {
            final Object fieldVal = Narcissus.getObjectFieldVal(Test.class.getClassLoader(), "ucp",
                    "jdk.internal.loader.URLClassPath");
            final URL[] methodResult = (URL[]) Narcissus.callObjectMethod(fieldVal, "getURLs", URL[].class);
            System.out.println(Arrays.toString(methodResult));
        } catch (NoSuchFieldError e) {
            throw new RuntimeException("Could not find field", e);
        }
    }
}