package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for Narcissus static method invocation methods.
 * Tests all return types: void, int, long, short, char, byte, boolean, float, double, Object.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusStaticMethodInvocationTest {

    private Method staticVoidMethod;
    private Method staticIntMethod;
    private Method staticLongMethod;
    private Method staticShortMethod;
    private Method staticCharMethod;
    private Method staticByteMethod;
    private Method staticBooleanMethod;
    private Method staticFloatMethod;
    private Method staticDoubleMethod;
    private Method staticObjectMethod;
    private Method staticMethodWithParams;
    private Method staticVarargsMethod;

    @BeforeEach
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
        
        Class<?> cls = TestStaticMethodClass.class;
        
        staticVoidMethod = cls.getDeclaredMethod("staticVoidMethod");
        staticIntMethod = cls.getDeclaredMethod("staticIntMethod");
        staticLongMethod = cls.getDeclaredMethod("staticLongMethod");
        staticShortMethod = cls.getDeclaredMethod("staticShortMethod");
        staticCharMethod = cls.getDeclaredMethod("staticCharMethod");
        staticByteMethod = cls.getDeclaredMethod("staticByteMethod");
        staticBooleanMethod = cls.getDeclaredMethod("staticBooleanMethod");
        staticFloatMethod = cls.getDeclaredMethod("staticFloatMethod");
        staticDoubleMethod = cls.getDeclaredMethod("staticDoubleMethod");
        staticObjectMethod = cls.getDeclaredMethod("staticObjectMethod");
        staticMethodWithParams = cls.getDeclaredMethod("staticMethodWithParams", int.class, String.class);
        staticVarargsMethod = cls.getDeclaredMethod("staticVarargsMethod", String[].class);
        
        // Reset static state before each test
        TestStaticMethodClass.resetState();
    }

    @Test
    public void testInvokeStaticVoidMethodFirstCall() {
        TestStaticMethodClass.staticVoidMethodCalled = false;
        Narcissus.invokeStaticVoidMethod(staticVoidMethod);
        assertThat(TestStaticMethodClass.staticVoidMethodCalled).isTrue();
    }

    @Test
    public void testInvokeStaticVoidMethodSecondCall() {
        TestStaticMethodClass.staticVoidMethodCalled = false;
        Narcissus.invokeStaticVoidMethod(staticVoidMethod);
        assertThat(TestStaticMethodClass.staticVoidMethodCalled).isTrue();
    }

    @Test
    public void testInvokeStaticIntMethodDefaultValue() {
        int result = Narcissus.invokeStaticIntMethod(staticIntMethod);
        assertThat(result).isEqualTo(777);
    }

    @Test
    public void testInvokeStaticIntMethodCustomValue() {
        TestStaticMethodClass.staticIntValue = 999;
        int result = Narcissus.invokeStaticIntMethod(staticIntMethod);
        assertThat(result).isEqualTo(999);
    }

    @Test
    public void testInvokeStaticIntMethodMaxValue() {
        TestStaticMethodClass.staticIntValue = Integer.MAX_VALUE;
        int result = Narcissus.invokeStaticIntMethod(staticIntMethod);
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticIntMethodMinValue() {
        TestStaticMethodClass.staticIntValue = Integer.MIN_VALUE;
        int result = Narcissus.invokeStaticIntMethod(staticIntMethod);
        assertThat(result).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticLongMethodDefaultValue() {
        long result = Narcissus.invokeStaticLongMethod(staticLongMethod);
        assertThat(result).isEqualTo(987654321L);
    }

    @Test
    public void testInvokeStaticLongMethodMaxValue() {
        TestStaticMethodClass.staticLongValue = Long.MAX_VALUE;
        long result = Narcissus.invokeStaticLongMethod(staticLongMethod);
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticLongMethodMinValue() {
        TestStaticMethodClass.staticLongValue = Long.MIN_VALUE;
        long result = Narcissus.invokeStaticLongMethod(staticLongMethod);
        assertThat(result).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticShortMethodDefaultValue() {
        short result = Narcissus.invokeStaticShortMethod(staticShortMethod);
        assertThat(result).isEqualTo((short) 2000);
    }

    @Test
    public void testInvokeStaticShortMethodMaxValue() {
        TestStaticMethodClass.staticShortValue = Short.MAX_VALUE;
        short result = Narcissus.invokeStaticShortMethod(staticShortMethod);
        assertThat(result).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticShortMethodMinValue() {
        TestStaticMethodClass.staticShortValue = Short.MIN_VALUE;
        short result = Narcissus.invokeStaticShortMethod(staticShortMethod);
        assertThat(result).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticCharMethodDefaultValue() {
        char result = Narcissus.invokeStaticCharMethod(staticCharMethod);
        assertThat(result).isEqualTo('Z');
    }

    @Test
    public void testInvokeStaticCharMethodMinValue() {
        TestStaticMethodClass.staticCharValue = '\u0000';
        char result = Narcissus.invokeStaticCharMethod(staticCharMethod);
        assertThat(result).isEqualTo('\u0000');
    }

    @Test
    public void testInvokeStaticCharMethodMaxValue() {
        TestStaticMethodClass.staticCharValue = '\uFFFF';
        char result = Narcissus.invokeStaticCharMethod(staticCharMethod);
        assertThat(result).isEqualTo('\uFFFF');
    }

    @Test
    public void testInvokeStaticCharMethodSpecialValue() {
        TestStaticMethodClass.staticCharValue = '©';
        char result = Narcissus.invokeStaticCharMethod(staticCharMethod);
        assertThat(result).isEqualTo('©');
    }

    @Test
    public void testInvokeStaticByteMethodDefaultValue() {
        byte result = Narcissus.invokeStaticByteMethod(staticByteMethod);
        assertThat(result).isEqualTo((byte) 50);
    }

    @Test
    public void testInvokeStaticByteMethodMaxValue() {
        TestStaticMethodClass.staticByteValue = Byte.MAX_VALUE;
        byte result = Narcissus.invokeStaticByteMethod(staticByteMethod);
        assertThat(result).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticByteMethodMinValue() {
        TestStaticMethodClass.staticByteValue = Byte.MIN_VALUE;
        byte result = Narcissus.invokeStaticByteMethod(staticByteMethod);
        assertThat(result).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticBooleanMethodDefaultValue() {
        boolean result = Narcissus.invokeStaticBooleanMethod(staticBooleanMethod);
        assertThat(result).isFalse();
    }

    @Test
    public void testInvokeStaticBooleanMethodTrueValue() {
        TestStaticMethodClass.staticBooleanValue = true;
        boolean result = Narcissus.invokeStaticBooleanMethod(staticBooleanMethod);
        assertThat(result).isTrue();
    }

    @Test
    public void testInvokeStaticBooleanMethodFalseValue() {
        TestStaticMethodClass.staticBooleanValue = false;
        boolean result = Narcissus.invokeStaticBooleanMethod(staticBooleanMethod);
        assertThat(result).isFalse();
    }

    @Test
    public void testInvokeStaticFloatMethodDefaultValue() {
        float result = Narcissus.invokeStaticFloatMethod(staticFloatMethod);
        assertThat(result).isEqualTo(2.71f);
    }

    @Test
    public void testInvokeStaticFloatMethodMaxValue() {
        TestStaticMethodClass.staticFloatValue = Float.MAX_VALUE;
        float result = Narcissus.invokeStaticFloatMethod(staticFloatMethod);
        assertThat(result).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticFloatMethodMinValue() {
        TestStaticMethodClass.staticFloatValue = Float.MIN_VALUE;
        float result = Narcissus.invokeStaticFloatMethod(staticFloatMethod);
        assertThat(result).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticFloatMethodNaNValue() {
        TestStaticMethodClass.staticFloatValue = Float.NaN;
        float result = Narcissus.invokeStaticFloatMethod(staticFloatMethod);
        assertThat(result).isNaN();
    }

    @Test
    public void testInvokeStaticFloatMethodPositiveInfinityValue() {
        TestStaticMethodClass.staticFloatValue = Float.POSITIVE_INFINITY;
        float result = Narcissus.invokeStaticFloatMethod(staticFloatMethod);
        assertThat(result).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testInvokeStaticDoubleMethodDefaultValue() {
        double result = Narcissus.invokeStaticDoubleMethod(staticDoubleMethod);
        assertThat(result).isEqualTo(1.41421356);
    }

    @Test
    public void testInvokeStaticDoubleMethodMaxValue() {
        TestStaticMethodClass.staticDoubleValue = Double.MAX_VALUE;
        double result = Narcissus.invokeStaticDoubleMethod(staticDoubleMethod);
        assertThat(result).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testInvokeStaticDoubleMethodMinValue() {
        TestStaticMethodClass.staticDoubleValue = Double.MIN_VALUE;
        double result = Narcissus.invokeStaticDoubleMethod(staticDoubleMethod);
        assertThat(result).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    public void testInvokeStaticDoubleMethodNaNValue() {
        TestStaticMethodClass.staticDoubleValue = Double.NaN;
        double result = Narcissus.invokeStaticDoubleMethod(staticDoubleMethod);
        assertThat(result).isNaN();
    }

    @Test
    public void testInvokeStaticDoubleMethodNegativeInfinityValue() {
        TestStaticMethodClass.staticDoubleValue = Double.NEGATIVE_INFINITY;
        double result = Narcissus.invokeStaticDoubleMethod(staticDoubleMethod);
        assertThat(result).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testInvokeStaticObjectMethodDefaultValue() {
        Object result = Narcissus.invokeStaticObjectMethod(staticObjectMethod);
        assertThat(result).isEqualTo("Static Hello World");
    }

    @Test
    public void testInvokeStaticObjectMethodStringValue() {
        TestStaticMethodClass.staticObjectValue = "Modified Static";
        Object result = Narcissus.invokeStaticObjectMethod(staticObjectMethod);
        assertThat(result).isEqualTo("Modified Static");
    }

    @Test
    public void testInvokeStaticObjectMethodNullValue() {
        TestStaticMethodClass.staticObjectValue = null;
        Object result = Narcissus.invokeStaticObjectMethod(staticObjectMethod);
        assertThat(result).isNull();
    }

    @Test
    public void testInvokeStaticObjectMethodIntegerValue() {
        Integer intObj = Integer.valueOf(123);
        TestStaticMethodClass.staticObjectValue = intObj;
        Object result = Narcissus.invokeStaticObjectMethod(staticObjectMethod);
        assertThat(result).isEqualTo(intObj);
        assertThat(result).isSameAs(intObj);
    }

    @Test
    public void testInvokeStaticMethodWithParametersNormalValues() {
        String result = (String) Narcissus.invokeStaticObjectMethod(staticMethodWithParams, 100, "static test");
        assertThat(result).isEqualTo("static:100:static test");
    }

    @Test
    public void testInvokeStaticMethodWithParametersEmptyString() {
        String result = (String) Narcissus.invokeStaticObjectMethod(staticMethodWithParams, 0, "");
        assertThat(result).isEqualTo("static:0:");
    }

    @Test
    public void testInvokeStaticMethodWithParametersNullString() {
        String result = (String) Narcissus.invokeStaticObjectMethod(staticMethodWithParams, -1, null);
        assertThat(result).isEqualTo("static:-1:null");
    }

    @Test
    public void testInvokeStaticVarargsMethodMultipleArgs() {
        // Varargs methods need arguments expanded as individual Object[] elements
        String result = (String) Narcissus.invokeStaticObjectMethod(staticVarargsMethod, new Object[]{"x", "y", "z"});
        assertThat(result).isEqualTo("static:x,y,z");
    }

    @Test
    public void testInvokeStaticVarargsMethodEmptyArgs() {
        String result = (String) Narcissus.invokeStaticObjectMethod(staticVarargsMethod, new Object[]{});
        assertThat(result).isEqualTo("static:");
    }

    @Test
    public void testInvokeStaticVarargsMethodSingleArg() {
        String result = (String) Narcissus.invokeStaticObjectMethod(staticVarargsMethod, new Object[]{"alone"});
        assertThat(result).isEqualTo("static:alone");
    }

    @Test
    public void testGenericInvokeStaticMethod() {
        // Test generic invokeStaticMethod with void return
        TestStaticMethodClass.staticVoidMethodCalled = false;
        Object result = Narcissus.invokeStaticMethod(staticVoidMethod);
        assertThat(result).isNull();
        assertThat(TestStaticMethodClass.staticVoidMethodCalled).isTrue();
        
        // Test generic invokeStaticMethod with primitive returns (should be boxed)
        result = Narcissus.invokeStaticMethod(staticIntMethod);
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isEqualTo(777);
        
        result = Narcissus.invokeStaticMethod(staticBooleanMethod);
        assertThat(result).isInstanceOf(Boolean.class);
        assertThat(result).isEqualTo(false);
        
        result = Narcissus.invokeStaticMethod(staticDoubleMethod);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isEqualTo(1.41421356);
        
        // Test generic invokeStaticMethod with Object return
        result = Narcissus.invokeStaticMethod(staticObjectMethod);
        assertThat(result).isEqualTo("Static Hello World");
        
        // Test with parameters
        result = Narcissus.invokeStaticMethod(staticMethodWithParams, 555, "generic static");
        assertThat(result).isEqualTo("static:555:generic static");
    }

    @Test
    public void testInvokeStaticMethodNoArguments() {
        // Test static methods that take no arguments
        Object result = Narcissus.invokeStaticMethod(staticIntMethod);
        assertThat(result).isEqualTo(777);
        
        result = Narcissus.invokeStaticMethod(staticObjectMethod);
        assertThat(result).isEqualTo("Static Hello World");
    }

    // Test class for static method invocation
    public static class TestStaticMethodClass {
        public static boolean staticVoidMethodCalled = false;
        public static int staticIntValue = 777;
        public static long staticLongValue = 987654321L;
        public static short staticShortValue = 2000;
        public static char staticCharValue = 'Z';
        public static byte staticByteValue = 50;
        public static boolean staticBooleanValue = false;
        public static float staticFloatValue = 2.71f;
        public static double staticDoubleValue = 1.41421356;
        public static Object staticObjectValue = "Static Hello World";
        
        public static void resetState() {
            staticVoidMethodCalled = false;
            staticIntValue = 777;
            staticLongValue = 987654321L;
            staticShortValue = 2000;
            staticCharValue = 'Z';
            staticByteValue = 50;
            staticBooleanValue = false;
            staticFloatValue = 2.71f;
            staticDoubleValue = 1.41421356;
            staticObjectValue = "Static Hello World";
        }
        
        public static void staticVoidMethod() {
            staticVoidMethodCalled = true;
        }
        
        public static int staticIntMethod() {
            return staticIntValue;
        }
        
        public static long staticLongMethod() {
            return staticLongValue;
        }
        
        public static short staticShortMethod() {
            return staticShortValue;
        }
        
        public static char staticCharMethod() {
            return staticCharValue;
        }
        
        public static byte staticByteMethod() {
            return staticByteValue;
        }
        
        public static boolean staticBooleanMethod() {
            return staticBooleanValue;
        }
        
        public static float staticFloatMethod() {
            return staticFloatValue;
        }
        
        public static double staticDoubleMethod() {
            return staticDoubleValue;
        }
        
        public static Object staticObjectMethod() {
            return staticObjectValue;
        }
        
        public static String staticMethodWithParams(int num, String str) {
            return "static:" + num + ":" + str;
        }
        
        public static String staticVarargsMethod(String... args) {
            if (args == null || args.length == 0) {
                return "static:";
            }
            StringBuilder sb = new StringBuilder("static:");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }
}