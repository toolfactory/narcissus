package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for Narcissus instance method invocation methods.
 * Tests all return types: void, int, long, short, char, byte, boolean, float, double, Object.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusInstanceMethodInvocationTest {

    private TestMethodClass testObject;
    private Method voidMethod;
    private Method intMethod;
    private Method longMethod;
    private Method shortMethod;
    private Method charMethod;
    private Method byteMethod;
    private Method booleanMethod;
    private Method floatMethod;
    private Method doubleMethod;
    private Method objectMethod;
    private Method methodWithParams;
    private Method varargsMethod;

    @BeforeEach
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
        
        testObject = new TestMethodClass();
        Class<?> cls = TestMethodClass.class;
        
        voidMethod = cls.getDeclaredMethod("voidMethod");
        intMethod = cls.getDeclaredMethod("intMethod");
        longMethod = cls.getDeclaredMethod("longMethod");
        shortMethod = cls.getDeclaredMethod("shortMethod");
        charMethod = cls.getDeclaredMethod("charMethod");
        byteMethod = cls.getDeclaredMethod("byteMethod");
        booleanMethod = cls.getDeclaredMethod("booleanMethod");
        floatMethod = cls.getDeclaredMethod("floatMethod");
        doubleMethod = cls.getDeclaredMethod("doubleMethod");
        objectMethod = cls.getDeclaredMethod("objectMethod");
        methodWithParams = cls.getDeclaredMethod("methodWithParams", int.class, String.class);
        varargsMethod = cls.getDeclaredMethod("varargsMethod", String[].class);
    }

    @Test
    public void testInvokeVoidMethod() {
        testObject.voidMethodCalled = false;
        Narcissus.invokeVoidMethod(testObject, voidMethod);
        assertThat(testObject.voidMethodCalled).isTrue();
        
        // Test multiple invocations
        testObject.voidMethodCalled = false;
        Narcissus.invokeVoidMethod(testObject, voidMethod);
        assertThat(testObject.voidMethodCalled).isTrue();
    }

    @Test
    public void testInvokeIntMethodNormalValue() {
        int result = Narcissus.invokeIntMethod(testObject, intMethod);
        assertThat(result).isEqualTo(42);
    }

    @Test
    public void testInvokeIntMethodCustomValue() {
        testObject.intValue = 999;
        int result = Narcissus.invokeIntMethod(testObject, intMethod);
        assertThat(result).isEqualTo(999);
    }

    @Test
    public void testInvokeIntMethodMaxValue() {
        testObject.intValue = Integer.MAX_VALUE;
        int result = Narcissus.invokeIntMethod(testObject, intMethod);
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testInvokeIntMethodMinValue() {
        testObject.intValue = Integer.MIN_VALUE;
        int result = Narcissus.invokeIntMethod(testObject, intMethod);
        assertThat(result).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testInvokeLongMethodNormalValue() {
        long result = Narcissus.invokeLongMethod(testObject, longMethod);
        assertThat(result).isEqualTo(123456789L);
    }

    @Test
    public void testInvokeLongMethodMaxValue() {
        testObject.longValue = Long.MAX_VALUE;
        long result = Narcissus.invokeLongMethod(testObject, longMethod);
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testInvokeLongMethodMinValue() {
        testObject.longValue = Long.MIN_VALUE;
        long result = Narcissus.invokeLongMethod(testObject, longMethod);
        assertThat(result).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testInvokeShortMethodNormalValue() {
        short result = Narcissus.invokeShortMethod(testObject, shortMethod);
        assertThat(result).isEqualTo((short) 1000);
    }

    @Test
    public void testInvokeShortMethodMaxValue() {
        testObject.shortValue = Short.MAX_VALUE;
        short result = Narcissus.invokeShortMethod(testObject, shortMethod);
        assertThat(result).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    public void testInvokeShortMethodMinValue() {
        testObject.shortValue = Short.MIN_VALUE;
        short result = Narcissus.invokeShortMethod(testObject, shortMethod);
        assertThat(result).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    public void testInvokeCharMethodNormalValue() {
        char result = Narcissus.invokeCharMethod(testObject, charMethod);
        assertThat(result).isEqualTo('A');
    }

    @Test
    public void testInvokeCharMethodNullChar() {
        testObject.charValue = '\u0000';
        char result = Narcissus.invokeCharMethod(testObject, charMethod);
        assertThat(result).isEqualTo('\u0000');
    }

    @Test
    public void testInvokeCharMethodMaxUnicode() {
        testObject.charValue = '\uFFFF';
        char result = Narcissus.invokeCharMethod(testObject, charMethod);
        assertThat(result).isEqualTo('\uFFFF');
    }

    @Test
    public void testInvokeCharMethodSpecialSymbol() {
        testObject.charValue = '™';
        char result = Narcissus.invokeCharMethod(testObject, charMethod);
        assertThat(result).isEqualTo('™');
    }

    @Test
    public void testInvokeByteMethodNormalValue() {
        byte result = Narcissus.invokeByteMethod(testObject, byteMethod);
        assertThat(result).isEqualTo((byte) 100);
    }

    @Test
    public void testInvokeByteMethodMaxValue() {
        testObject.byteValue = Byte.MAX_VALUE;
        byte result = Narcissus.invokeByteMethod(testObject, byteMethod);
        assertThat(result).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testInvokeByteMethodMinValue() {
        testObject.byteValue = Byte.MIN_VALUE;
        byte result = Narcissus.invokeByteMethod(testObject, byteMethod);
        assertThat(result).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testInvokeBooleanMethodInitialTrue() {
        boolean result = Narcissus.invokeBooleanMethod(testObject, booleanMethod);
        assertThat(result).isTrue();
    }

    @Test
    public void testInvokeBooleanMethodFalseValue() {
        testObject.booleanValue = false;
        boolean result = Narcissus.invokeBooleanMethod(testObject, booleanMethod);
        assertThat(result).isFalse();
    }

    @Test
    public void testInvokeBooleanMethodTrueValue() {
        testObject.booleanValue = true;
        boolean result = Narcissus.invokeBooleanMethod(testObject, booleanMethod);
        assertThat(result).isTrue();
    }

    @Test
    public void testInvokeFloatMethodNormalValue() {
        float result = Narcissus.invokeFloatMethod(testObject, floatMethod);
        assertThat(result).isEqualTo(3.14f);
    }

    @Test
    public void testInvokeFloatMethodMaxValue() {
        testObject.floatValue = Float.MAX_VALUE;
        float result = Narcissus.invokeFloatMethod(testObject, floatMethod);
        assertThat(result).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    public void testInvokeFloatMethodMinValue() {
        testObject.floatValue = Float.MIN_VALUE;
        float result = Narcissus.invokeFloatMethod(testObject, floatMethod);
        assertThat(result).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    public void testInvokeFloatMethodNaNValue() {
        testObject.floatValue = Float.NaN;
        float result = Narcissus.invokeFloatMethod(testObject, floatMethod);
        assertThat(result).isNaN();
    }

    @Test
    public void testInvokeFloatMethodPositiveInfinity() {
        testObject.floatValue = Float.POSITIVE_INFINITY;
        float result = Narcissus.invokeFloatMethod(testObject, floatMethod);
        assertThat(result).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testInvokeDoubleMethodNormalValue() {
        double result = Narcissus.invokeDoubleMethod(testObject, doubleMethod);
        assertThat(result).isEqualTo(2.718281828);
    }

    @Test
    public void testInvokeDoubleMethodMaxValue() {
        testObject.doubleValue = Double.MAX_VALUE;
        double result = Narcissus.invokeDoubleMethod(testObject, doubleMethod);
        assertThat(result).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testInvokeDoubleMethodMinValue() {
        testObject.doubleValue = Double.MIN_VALUE;
        double result = Narcissus.invokeDoubleMethod(testObject, doubleMethod);
        assertThat(result).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    public void testInvokeDoubleMethodNaNValue() {
        testObject.doubleValue = Double.NaN;
        double result = Narcissus.invokeDoubleMethod(testObject, doubleMethod);
        assertThat(result).isNaN();
    }

    @Test
    public void testInvokeDoubleMethodNegativeInfinity() {
        testObject.doubleValue = Double.NEGATIVE_INFINITY;
        double result = Narcissus.invokeDoubleMethod(testObject, doubleMethod);
        assertThat(result).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testInvokeObjectMethodNormalValue() {
        Object result = Narcissus.invokeObjectMethod(testObject, objectMethod);
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    public void testInvokeObjectMethodModifiedString() {
        testObject.objectValue = "Modified";
        Object result = Narcissus.invokeObjectMethod(testObject, objectMethod);
        assertThat(result).isEqualTo("Modified");
    }

    @Test
    public void testInvokeObjectMethodNullValue() {
        testObject.objectValue = null;
        Object result = Narcissus.invokeObjectMethod(testObject, objectMethod);
        assertThat(result).isNull();
    }

    @Test
    public void testInvokeObjectMethodIntegerObject() {
        Integer intObj = Integer.valueOf(42);
        testObject.objectValue = intObj;
        Object result = Narcissus.invokeObjectMethod(testObject, objectMethod);
        assertThat(result).isEqualTo(intObj);
        assertThat(result).isSameAs(intObj);
    }

    @Test
    public void testInvokeMethodWithParametersNormalValues() {
        String result = (String) Narcissus.invokeObjectMethod(testObject, methodWithParams, 42, "test");
        assertThat(result).isEqualTo("42:test");
    }

    @Test
    public void testInvokeMethodWithParametersEmptyString() {
        String result = (String) Narcissus.invokeObjectMethod(testObject, methodWithParams, 0, "");
        assertThat(result).isEqualTo("0:");
    }

    @Test
    public void testInvokeMethodWithParametersNullString() {
        String result = (String) Narcissus.invokeObjectMethod(testObject, methodWithParams, -1, null);
        assertThat(result).isEqualTo("-1:null");
    }

    @Test
    public void testInvokeVarargsMethodMultipleArgs() {
        // Varargs methods need arguments expanded as individual Object[] elements
        String result = (String) Narcissus.invokeObjectMethod(testObject, varargsMethod, new Object[]{"a", "b", "c"});
        assertThat(result).isEqualTo("a,b,c");
    }

    @Test
    public void testInvokeVarargsMethodEmptyArgs() {
        String result = (String) Narcissus.invokeObjectMethod(testObject, varargsMethod, new Object[]{});
        assertThat(result).isEqualTo("");
    }

    @Test
    public void testInvokeVarargsMethodSingleArg() {
        String result = (String) Narcissus.invokeObjectMethod(testObject, varargsMethod, new Object[]{"single"});
        assertThat(result).isEqualTo("single");
    }

    @Test
    public void testGenericInvokeMethod() {
        // Test generic invokeMethod with void return
        testObject.voidMethodCalled = false;
        Object result = Narcissus.invokeMethod(testObject, voidMethod);
        assertThat(result).isNull();
        assertThat(testObject.voidMethodCalled).isTrue();
        
        // Test generic invokeMethod with primitive returns (should be boxed)
        result = Narcissus.invokeMethod(testObject, intMethod);
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isEqualTo(42);
        
        result = Narcissus.invokeMethod(testObject, booleanMethod);
        assertThat(result).isInstanceOf(Boolean.class);
        assertThat(result).isEqualTo(true);
        
        result = Narcissus.invokeMethod(testObject, doubleMethod);
        assertThat(result).isInstanceOf(Double.class);
        assertThat(result).isEqualTo(2.718281828);
        
        // Test generic invokeMethod with Object return
        result = Narcissus.invokeMethod(testObject, objectMethod);
        assertThat(result).isEqualTo("Hello World");
        
        // Test with parameters
        result = Narcissus.invokeMethod(testObject, methodWithParams, 99, "generic");
        assertThat(result).isEqualTo("99:generic");
    }

    @Test
    public void testInvokeMethodNoArguments() {
        // Test methods that take no arguments
        Object result = Narcissus.invokeMethod(testObject, intMethod);
        assertThat(result).isEqualTo(42);
        
        result = Narcissus.invokeMethod(testObject, objectMethod);
        assertThat(result).isEqualTo("Hello World");
    }

    // Test class for method invocation
    public static class TestMethodClass {
        public boolean voidMethodCalled = false;
        public int intValue = 42;
        public long longValue = 123456789L;
        public short shortValue = 1000;
        public char charValue = 'A';
        public byte byteValue = 100;
        public boolean booleanValue = true;
        public float floatValue = 3.14f;
        public double doubleValue = 2.718281828;
        public Object objectValue = "Hello World";
        
        public void voidMethod() {
            voidMethodCalled = true;
        }
        
        public int intMethod() {
            return intValue;
        }
        
        public long longMethod() {
            return longValue;
        }
        
        public short shortMethod() {
            return shortValue;
        }
        
        public char charMethod() {
            return charValue;
        }
        
        public byte byteMethod() {
            return byteValue;
        }
        
        public boolean booleanMethod() {
            return booleanValue;
        }
        
        public float floatMethod() {
            return floatValue;
        }
        
        public double doubleMethod() {
            return doubleValue;
        }
        
        public Object objectMethod() {
            return objectValue;
        }
        
        public String methodWithParams(int num, String str) {
            return num + ":" + str;
        }
        
        public String varargsMethod(String... args) {
            if (args == null || args.length == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }
}