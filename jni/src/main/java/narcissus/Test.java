package narcissus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

// TODO: Yes, Narcissus needs proper unit tests...
public class Test {
    private static class Cls {
        private int triple(int x) {
            return x * 3;
        }
    }

    public static void main(String[] args) throws Exception {
        final ClassLoader classLoader = Test.class.getClassLoader();

        final Field ucpField = Narcissus.findField(classLoader, "ucp");
        final Object ucpVal = Narcissus.getFieldVal(classLoader, ucpField);

        final Method getURLsMethod = Narcissus.findMethod(ucpVal, "getURLs");
        for (URL url : (URL[]) Narcissus.callMethod(ucpVal, getURLsMethod)) {
            System.out.println("Classpath URL: " + url);
        }

        Cls cls = new Cls();
        Method triple = Narcissus.findMethod(cls, "triple", int.class);
        System.out.println(Narcissus.callIntMethod(cls, triple, 5));
        System.out.println(Narcissus.callMethod(cls, triple, 2));
    }
}