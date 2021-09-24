package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

public class NarcissusTest {
    static class X {
        int triple(int x) {
            return x * 3;
        }
    }

    @Test
    public void testInvokeIntMethodWithParam() throws Exception {
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
    public void testInvokeMethods() throws Exception {
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

    @Test(expected = NullPointerException.class)
    public void testCheckNullPointerExceptionNonStatic() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeDoubleMethod(null, dm);
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNullPointerExceptionStatic() throws Exception {
        Method _dm = Narcissus.findMethod(Z.class, "_d");
        Narcissus.invokeDoubleMethod(null, _dm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckStaticModifierException1() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeStaticDoubleMethod(dm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckObjectClassDoesNotMatchDeclaringClass() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeDoubleMethod(new Y(), dm);
    }

    @Test
    public void testFindClass() throws Exception {
        Class<?> cls = Narcissus.findClass(Y.class.getName());
        assertThat(cls).isNotNull();
        assertThat(cls.getName()).isEqualTo(Y.class.getName());
        Class<?> arrCls = Narcissus.findClass(Y.class.getName() + "[]");
        assertThat(arrCls).isNotNull();
        assertThat(arrCls.getName()).isEqualTo("[L" + Y.class.getName() + ";");
    }

    @Test
    public void testEnumerateFields() throws Exception {
        assertThat(Narcissus.enumerateFields(Y.class).stream().map(new Function<Field, String>() {
            @Override
            public String apply(Field field) {
                return field.getName();
            }
        }).collect(Collectors.toList())).containsOnly("i", "j", "s", "c", "b", "z", "f", "d", "_i", "_j", "_s",
                "_c", "_b", "_z", "_f", "_d");
    }

    @Test
    public void testEnumerateMethods() throws Exception {
        assertThat(Narcissus.enumerateMethods(Z.class).stream().map(new Function<Method, String>() {
            @Override
            public String apply(Method method) {
                return method.getName();
            }
        }).collect(Collectors.toList())).contains("i", "j", "s", "c", "b", "z", "f", "d", "_i", "_j", "_s", "_c",
                "_b", "_z", "_f", "_d");
    }
    
    static class A {
        int x;
    }
    
    static class B extends A {
    }
    
    @Test
    public void testInheritedField() throws Exception {
        Field ax = Narcissus.findField(A.class, "x");
        assertThat(ax).isNotNull();
        Field bx = Narcissus.findField(B.class, "x");
        assertThat(bx).isNotNull();
        A a = new A();
        a.x = 3;
        assertThat(Narcissus.getIntField(a, ax)).isEqualTo(a.x);
        B b = new B();
        b.x = 5;
        assertThat(Narcissus.getIntField(b, bx)).isEqualTo(b.x);
    }
}