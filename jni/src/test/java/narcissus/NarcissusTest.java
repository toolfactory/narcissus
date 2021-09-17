package narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NarcissusTest {
    @BeforeAll
    public static void before() {
        Narcissus.init();
    }

    static class X {
        int triple(int x) {
            return x * 3;
        }
    }

    @Test
    public void invokeIntMethodWithParam() throws Exception {
        Method triple = Narcissus.findMethod(X.class, "triple", int.class);
        assertThat(Narcissus.invokeIntMethod(new X(), triple, 5)).isEqualTo(15);
    }

    static class Y {
        int i = 1;
        long j = 2;
        short s = 3;
        char c = '4';
        byte b = 5;
        boolean z = true;
        float f = 7.0f;
        double d = 8.0;

        static int _i = 1;
        static long _j = 2;
        static short _s = 3;
        static char _c = '4';
        static byte _b = 5;
        static boolean _z = true;
        static float _f = 7.0f;
        static double _d = 8.0;
    }

    @Test
    public void testFieldGetters() throws Exception {
        Y y = new Y();
        for (Field f : Y.class.getDeclaredFields()) {
            Field nf = Narcissus.findField(Y.class, f.getName());
            assertThat(nf).isEqualTo(f);
            if (Modifier.isStatic(f.getModifiers())) {
                assertThat(Narcissus.getStaticField(nf)).isEqualTo(f.get(null));
            } else {
                assertThat(Narcissus.getField(y, nf)).isEqualTo(f.get(y));
            }
        }
    }

    @Test
    public void testFieldSetters() throws Exception {
        Y y = new Y();

        Field i = Narcissus.findField(Y.class, "i");
        assertThat(Narcissus.getField(y, i)).isEqualTo(1);
        Narcissus.setField(y, i, 2);
        assertThat(Narcissus.getField(y, i)).isEqualTo(2);

        Field _i = Narcissus.findField(Y.class, "_i");
        assertThat(Narcissus.getStaticField(_i)).isEqualTo(1);
        Narcissus.setStaticField(_i, 2);
        assertThat(Narcissus.getStaticField(_i)).isEqualTo(2);
    }

    static class Z {
        int i() {
            return 1;
        }

        long j() {
            return 2;
        }

        short s() {
            return 3;
        }

        char c() {
            return '4';
        }

        byte b() {
            return 5;
        }

        boolean z() {
            return true;
        }

        float f() {
            return 7.0f;
        }

        double d() {
            return 8.0;
        }

        static int _i() {
            return 1;
        }

        static long _j() {
            return 2;
        }

        static short _s() {
            return 3;
        }

        static char _c() {
            return '4';
        }

        static byte _b() {
            return 5;
        }

        static boolean _z() {
            return true;
        }

        static float _f() {
            return 7.0f;
        }

        static double _d() {
            return 8.0;
        }
    }

    @Test
    public void invokeMethods() throws Exception {
        Z z = new Z();
        for (Method m : Z.class.getDeclaredMethods()) {
            Method nm = Narcissus.findMethod(Z.class, m.getName());
            assertThat(nm).isEqualTo(m);
            if (Modifier.isStatic(m.getModifiers())) {
                assertThat(Narcissus.invokeStaticMethod(nm)).isEqualTo(m.invoke(null));
            } else {
                assertThat(Narcissus.invokeMethod(z, nm)).isEqualTo(m.invoke(z));
            }
        }
    }

    @Test
    public void checkNullPointerException() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        assertThrows(NullPointerException.class, () -> {
            Narcissus.invokeDoubleMethod(null, dm);
        });
    }

    @Test
    public void checkStaticModifierException1() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        assertThrows(IllegalArgumentException.class, () -> {
            Narcissus.invokeStaticDoubleMethod(dm);
        });
    }

    @Test
    public void checkObjectClassDoesNotMatchDeclaringClass() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        assertThrows(IllegalArgumentException.class, () -> {
            Narcissus.invokeDoubleMethod(new Y(), dm);
        });
    }
}