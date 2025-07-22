package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Narcissus static field getter and setter methods.
 * Tests all primitive types: int, long, short, char, byte, boolean, float, double, Object.
 */
public class NarcissusStaticFieldOperationsTest {

    private Field staticIntField;
    private Field staticLongField;
    private Field staticShortField;
    private Field staticCharField;
    private Field staticByteField;
    private Field staticBooleanField;
    private Field staticFloatField;
    private Field staticDoubleField;
    private Field staticObjectField;

    @Before
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
        
        Class<?> cls = TestStaticFieldClass.class;
        
        staticIntField = cls.getDeclaredField("staticIntValue");
        staticLongField = cls.getDeclaredField("staticLongValue");
        staticShortField = cls.getDeclaredField("staticShortValue");
        staticCharField = cls.getDeclaredField("staticCharValue");
        staticByteField = cls.getDeclaredField("staticByteValue");
        staticBooleanField = cls.getDeclaredField("staticBooleanValue");
        staticFloatField = cls.getDeclaredField("staticFloatValue");
        staticDoubleField = cls.getDeclaredField("staticDoubleValue");
        staticObjectField = cls.getDeclaredField("staticObjectValue");
        
        // Reset all static fields to default values before each test
        TestStaticFieldClass.resetFields();
    }

    @Test
    public void testGetStaticIntFieldNormalValue() {
        TestStaticFieldClass.staticIntValue = 42;
        int value = Narcissus.getStaticIntField(staticIntField);
        assertThat(value).isEqualTo(42);
    }
    
    @Test
    public void testGetStaticIntFieldMaxValue() {
        TestStaticFieldClass.staticIntValue = Integer.MAX_VALUE;
        int value = Narcissus.getStaticIntField(staticIntField);
        assertThat(value).isEqualTo(Integer.MAX_VALUE);
    }
    
    @Test
    public void testGetStaticIntFieldMinValue() {
        TestStaticFieldClass.staticIntValue = Integer.MIN_VALUE;
        int value = Narcissus.getStaticIntField(staticIntField);
        assertThat(value).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testSetStaticIntFieldNormalValue() {
        Narcissus.setStaticIntField(staticIntField, 777);
        assertThat(TestStaticFieldClass.staticIntValue).isEqualTo(777);
    }
    
    @Test
    public void testSetStaticIntFieldMaxValue() {
        Narcissus.setStaticIntField(staticIntField, Integer.MAX_VALUE);
        assertThat(TestStaticFieldClass.staticIntValue).isEqualTo(Integer.MAX_VALUE);
    }
    
    @Test
    public void testSetStaticIntFieldMinValue() {
        Narcissus.setStaticIntField(staticIntField, Integer.MIN_VALUE);
        assertThat(TestStaticFieldClass.staticIntValue).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testGetStaticLongFieldNormalValue() {
        TestStaticFieldClass.staticLongValue = 123456789L;
        long value = Narcissus.getStaticLongField(staticLongField);
        assertThat(value).isEqualTo(123456789L);
    }
    
    @Test
    public void testGetStaticLongFieldMaxValue() {
        TestStaticFieldClass.staticLongValue = Long.MAX_VALUE;
        long value = Narcissus.getStaticLongField(staticLongField);
        assertThat(value).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testSetStaticLongFieldNormalValue() {
        Narcissus.setStaticLongField(staticLongField, 987654321L);
        assertThat(TestStaticFieldClass.staticLongValue).isEqualTo(987654321L);
    }
    
    @Test
    public void testSetStaticLongFieldMinValue() {
        Narcissus.setStaticLongField(staticLongField, Long.MIN_VALUE);
        assertThat(TestStaticFieldClass.staticLongValue).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testGetStaticShortFieldNormalValue() {
        TestStaticFieldClass.staticShortValue = 1000;
        short value = Narcissus.getStaticShortField(staticShortField);
        assertThat(value).isEqualTo((short) 1000);
    }
    
    @Test
    public void testGetStaticShortFieldMaxValue() {
        TestStaticFieldClass.staticShortValue = Short.MAX_VALUE;
        short value = Narcissus.getStaticShortField(staticShortField);
        assertThat(value).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    public void testSetStaticShortFieldNormalValue() {
        Narcissus.setStaticShortField(staticShortField, (short) 2000);
        assertThat(TestStaticFieldClass.staticShortValue).isEqualTo((short) 2000);
    }
    
    @Test
    public void testSetStaticShortFieldMinValue() {
        Narcissus.setStaticShortField(staticShortField, Short.MIN_VALUE);
        assertThat(TestStaticFieldClass.staticShortValue).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    public void testGetStaticCharFieldNormalValue() {
        TestStaticFieldClass.staticCharValue = 'Z';
        char value = Narcissus.getStaticCharField(staticCharField);
        assertThat(value).isEqualTo('Z');
    }
    
    @Test
    public void testGetStaticCharFieldUnicodeValue() {
        TestStaticFieldClass.staticCharValue = '\u1234';
        char value = Narcissus.getStaticCharField(staticCharField);
        assertThat(value).isEqualTo('\u1234');
    }

    @Test
    public void testSetStaticCharFieldNormalValue() {
        Narcissus.setStaticCharField(staticCharField, 'X');
        assertThat(TestStaticFieldClass.staticCharValue).isEqualTo('X');
    }
    
    @Test
    public void testSetStaticCharFieldUnicodeValue() {
        Narcissus.setStaticCharField(staticCharField, '\uFFFF');
        assertThat(TestStaticFieldClass.staticCharValue).isEqualTo('\uFFFF');
    }

    @Test
    public void testGetStaticByteFieldNormalValue() {
        TestStaticFieldClass.staticByteValue = 100;
        byte value = Narcissus.getStaticByteField(staticByteField);
        assertThat(value).isEqualTo((byte) 100);
    }
    
    @Test
    public void testGetStaticByteFieldMaxValue() {
        TestStaticFieldClass.staticByteValue = Byte.MAX_VALUE;
        byte value = Narcissus.getStaticByteField(staticByteField);
        assertThat(value).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testSetStaticByteFieldNormalValue() {
        Narcissus.setStaticByteField(staticByteField, (byte) 50);
        assertThat(TestStaticFieldClass.staticByteValue).isEqualTo((byte) 50);
    }
    
    @Test
    public void testSetStaticByteFieldMinValue() {
        Narcissus.setStaticByteField(staticByteField, Byte.MIN_VALUE);
        assertThat(TestStaticFieldClass.staticByteValue).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testGetStaticBooleanFieldTrueValue() {
        TestStaticFieldClass.staticBooleanValue = true;
        boolean value = Narcissus.getStaticBooleanField(staticBooleanField);
        assertThat(value).isTrue();
    }
    
    @Test
    public void testGetStaticBooleanFieldFalseValue() {
        TestStaticFieldClass.staticBooleanValue = false;
        boolean value = Narcissus.getStaticBooleanField(staticBooleanField);
        assertThat(value).isFalse();
    }

    @Test
    public void testSetStaticBooleanFieldTrueValue() {
        Narcissus.setStaticBooleanField(staticBooleanField, true);
        assertThat(TestStaticFieldClass.staticBooleanValue).isTrue();
    }
    
    @Test
    public void testSetStaticBooleanFieldFalseValue() {
        Narcissus.setStaticBooleanField(staticBooleanField, false);
        assertThat(TestStaticFieldClass.staticBooleanValue).isFalse();
    }

    @Test
    public void testGetStaticFloatFieldNormalValue() {
        TestStaticFieldClass.staticFloatValue = 3.14f;
        float value = Narcissus.getStaticFloatField(staticFloatField);
        assertThat(value).isEqualTo(3.14f);
    }
    
    @Test
    public void testGetStaticFloatFieldNaNValue() {
        TestStaticFieldClass.staticFloatValue = Float.NaN;
        float value = Narcissus.getStaticFloatField(staticFloatField);
        assertThat(value).isNaN();
    }
    
    @Test
    public void testGetStaticFloatFieldPositiveInfinityValue() {
        TestStaticFieldClass.staticFloatValue = Float.POSITIVE_INFINITY;
        float value = Narcissus.getStaticFloatField(staticFloatField);
        assertThat(value).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testSetStaticFloatFieldNormalValue() {
        Narcissus.setStaticFloatField(staticFloatField, 2.71f);
        assertThat(TestStaticFieldClass.staticFloatValue).isEqualTo(2.71f);
    }
    
    @Test
    public void testSetStaticFloatFieldMaxValue() {
        Narcissus.setStaticFloatField(staticFloatField, Float.MAX_VALUE);
        assertThat(TestStaticFieldClass.staticFloatValue).isEqualTo(Float.MAX_VALUE);
    }
    
    @Test
    public void testSetStaticFloatFieldNegativeInfinityValue() {
        Narcissus.setStaticFloatField(staticFloatField, Float.NEGATIVE_INFINITY);
        assertThat(TestStaticFieldClass.staticFloatValue).isEqualTo(Float.NEGATIVE_INFINITY);
    }

    @Test
    public void testGetStaticDoubleFieldNormalValue() {
        TestStaticFieldClass.staticDoubleValue = 2.718281828;
        double value = Narcissus.getStaticDoubleField(staticDoubleField);
        assertThat(value).isEqualTo(2.718281828);
    }
    
    @Test
    public void testGetStaticDoubleFieldNaNValue() {
        TestStaticFieldClass.staticDoubleValue = Double.NaN;
        double value = Narcissus.getStaticDoubleField(staticDoubleField);
        assertThat(value).isNaN();
    }
    
    @Test
    public void testGetStaticDoubleFieldPositiveInfinityValue() {
        TestStaticFieldClass.staticDoubleValue = Double.POSITIVE_INFINITY;
        double value = Narcissus.getStaticDoubleField(staticDoubleField);
        assertThat(value).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testSetStaticDoubleFieldNormalValue() {
        Narcissus.setStaticDoubleField(staticDoubleField, 1.41421356);
        assertThat(TestStaticFieldClass.staticDoubleValue).isEqualTo(1.41421356);
    }
    
    @Test
    public void testSetStaticDoubleFieldMinValue() {
        Narcissus.setStaticDoubleField(staticDoubleField, Double.MIN_VALUE);
        assertThat(TestStaticFieldClass.staticDoubleValue).isEqualTo(Double.MIN_VALUE);
    }
    
    @Test
    public void testSetStaticDoubleFieldNegativeInfinityValue() {
        Narcissus.setStaticDoubleField(staticDoubleField, Double.NEGATIVE_INFINITY);
        assertThat(TestStaticFieldClass.staticDoubleValue).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testGetStaticObjectFieldNonNullValue() {
        String testStr = "Static Hello World";
        TestStaticFieldClass.staticObjectValue = testStr;
        Object value = Narcissus.getStaticObjectField(staticObjectField);
        assertThat(value).isEqualTo(testStr);
        assertThat(value).isSameAs(testStr);
    }
    
    @Test
    public void testGetStaticObjectFieldNullValue() {
        TestStaticFieldClass.staticObjectValue = null;
        Object value = Narcissus.getStaticObjectField(staticObjectField);
        assertThat(value).isNull();
    }

    @Test
    public void testSetStaticObjectFieldStringValue() {
        String testStr = "Static Test String";
        Narcissus.setStaticObjectField(staticObjectField, testStr);
        assertThat(TestStaticFieldClass.staticObjectValue).isEqualTo(testStr);
        assertThat(TestStaticFieldClass.staticObjectValue).isSameAs(testStr);
    }
    
    @Test
    public void testSetStaticObjectFieldNullValue() {
        Narcissus.setStaticObjectField(staticObjectField, null);
        assertThat(TestStaticFieldClass.staticObjectValue).isNull();
    }
    
    @Test
    public void testSetStaticObjectFieldIntegerValue() {
        Integer intObj = Integer.valueOf(999);
        Narcissus.setStaticObjectField(staticObjectField, intObj);
        assertThat(TestStaticFieldClass.staticObjectValue).isEqualTo(intObj);
        assertThat(TestStaticFieldClass.staticObjectValue).isSameAs(intObj);
    }

    @Test
    public void testGenericStaticFieldMethods() {
        // Test generic getStaticField method
        TestStaticFieldClass.staticIntValue = 555;
        Object value = Narcissus.getStaticField(staticIntField);
        assertThat(value).isInstanceOf(Integer.class);
        assertThat(value).isEqualTo(555);
        
        TestStaticFieldClass.staticBooleanValue = true;
        value = Narcissus.getStaticField(staticBooleanField);
        assertThat(value).isInstanceOf(Boolean.class);
        assertThat(value).isEqualTo(true);
        
        // Test generic setStaticField method
        Narcissus.setStaticField(staticIntField, 666);
        assertThat(TestStaticFieldClass.staticIntValue).isEqualTo(666);
        
        Narcissus.setStaticField(staticObjectField, "generic test");
        assertThat(TestStaticFieldClass.staticObjectValue).isEqualTo("generic test");
        
        // Test boxing/unboxing
        Narcissus.setStaticField(staticLongField, Long.valueOf(12345L));
        assertThat(TestStaticFieldClass.staticLongValue).isEqualTo(12345L);
    }

    @Test
    public void testStaticFieldRoundTrip() {
        // Test that static set and get operations work together correctly
        Narcissus.setStaticIntField(staticIntField, 888);
        int retrievedInt = Narcissus.getStaticIntField(staticIntField);
        assertThat(retrievedInt).isEqualTo(888);
        
        Narcissus.setStaticObjectField(staticObjectField, "static roundtrip");
        Object retrievedObject = Narcissus.getStaticObjectField(staticObjectField);
        assertThat(retrievedObject).isEqualTo("static roundtrip");
        
        Narcissus.setStaticBooleanField(staticBooleanField, true);
        boolean retrievedBool = Narcissus.getStaticBooleanField(staticBooleanField);
        assertThat(retrievedBool).isTrue();
    }

    // Test class for static field operations
    public static class TestStaticFieldClass {
        public static int staticIntValue;
        public static long staticLongValue;
        public static short staticShortValue;
        public static char staticCharValue;
        public static byte staticByteValue;
        public static boolean staticBooleanValue;
        public static float staticFloatValue;
        public static double staticDoubleValue;
        public static Object staticObjectValue;
        
        public static void resetFields() {
            staticIntValue = 0;
            staticLongValue = 0L;
            staticShortValue = 0;
            staticCharValue = '\u0000';
            staticByteValue = 0;
            staticBooleanValue = false;
            staticFloatValue = 0.0f;
            staticDoubleValue = 0.0;
            staticObjectValue = null;
        }
    }
}