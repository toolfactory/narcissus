package narcissus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

public class Test {
    private static class Cls {
        private int prt(int x) {
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
        Method prt = Narcissus.findMethod(cls, "prt", int.class);
        int val = Narcissus.callIntMethod(cls, prt, 5);
    }
}