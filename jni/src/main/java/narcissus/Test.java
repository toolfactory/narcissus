package narcissus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

public class Test {
    private static class Cls {
        private void prt(int x) {
            System.out.println(x);
        }
    }

    public static void main(String[] args) throws Exception {
        final ClassLoader classLoader = Test.class.getClassLoader();

        final Field ucpField = Utils.findField(classLoader, "ucp");
        final Object ucpVal = Narcissus.getFieldVal(classLoader, ucpField);

        final Method getURLsMethod = Utils.findMethod(ucpVal, "getURLs");
        for (URL url : (URL[]) Narcissus.callMethod(ucpVal, getURLsMethod)) {
            System.out.println("Classpath URL: " + url);
        }

        Cls cls = new Cls();
        Method prt = Utils.findMethod(cls, "prt", int.class);
        Narcissus.callVoidMethod(cls, prt, 5);
    }
}