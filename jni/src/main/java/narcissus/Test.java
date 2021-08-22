package narcissus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

public class Test {
    public static void main(String[] args) {
        final ClassLoader classLoader = Test.class.getClassLoader();

        final Field ucpField = Utils.findField(classLoader, "ucp");
        final Object ucpVal = Narcissus.getFieldVal(classLoader, ucpField);

        final Method getURLsMethod = Utils.findMethod(ucpVal, "getURLs");
        for (URL url : (URL[]) Narcissus.callMethod(ucpVal, getURLsMethod)) {
            System.out.println("Classpath URL: " + url);
        }
    }
}