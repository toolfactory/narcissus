package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Narcissus instance field getter methods.
 * Tests all primitive types: int, long, short, char, byte, boolean, float, double, Object.
 */
public class NarcissusInstanceFieldGettersTest {

    private TestFieldClass testObject;
    private Field intField;
    private Field longField;
    private Field shortField;
    private Field charField;
    private Field byteField;
    private Field booleanField;
    private Field floatField;
    private Field doubleField;
    private Field objectField;

    @Before
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
        
        testObject = new TestFieldClass();
        Class<?> cls = TestFieldClass.class;
        
        intField = cls.getDeclaredField("intValue");
        longField = cls.getDeclaredField("longValue");
        shortField = cls.getDeclaredField("shortValue");
        charField = cls.getDeclaredField("charValue");
        byteField = cls.getDeclaredField("byteValue");
        booleanField = cls.getDeclaredField("booleanValue");
        floatField = cls.getDeclaredField("floatValue");
        doubleField = cls.getDeclaredField("doubleValue");
        objectField = cls.getDeclaredField("objectValue");
    }

    @Test
    public void testGetIntFieldNormalValue() {
        testObject.intValue = 42;
        int value = Narcissus.getIntField(testObject, intField);
        assertThat(value).isEqualTo(42);
    }

    @Test
    public void testGetIntFieldMaxValue() {
        testObject.intValue = Integer.MAX_VALUE;
        int value = Narcissus.getIntField(testObject, intField);
        assertThat(value).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testGetIntFieldMinValue() {
        testObject.intValue = Integer.MIN_VALUE;
        int value = Narcissus.getIntField(testObject, intField);
        assertThat(value).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testGetLongFieldNormalValue() {
        testObject.longValue = 123456789L;
        long value = Narcissus.getLongField(testObject, longField);
        assertThat(value).isEqualTo(123456789L);
    }

    @Test
    public void testGetLongFieldMaxValue() {
        testObject.longValue = Long.MAX_VALUE;
        long value = Narcissus.getLongField(testObject, longField);
        assertThat(value).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testGetLongFieldMinValue() {
        testObject.longValue = Long.MIN_VALUE;
        long value = Narcissus.getLongField(testObject, longField);
        assertThat(value).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testGetShortFieldNormalValue() {
        testObject.shortValue = 1000;
        short value = Narcissus.getShortField(testObject, shortField);
        assertThat(value).isEqualTo((short) 1000);
    }

    @Test
    public void testGetShortFieldMaxValue() {
        testObject.shortValue = Short.MAX_VALUE;
        short value = Narcissus.getShortField(testObject, shortField);
        assertThat(value).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    public void testGetShortFieldMinValue() {
        testObject.shortValue = Short.MIN_VALUE;
        short value = Narcissus.getShortField(testObject, shortField);
        assertThat(value).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    public void testGetCharFieldNormalValue() {
        testObject.charValue = 'A';
        char value = Narcissus.getCharField(testObject, charField);
        assertThat(value).isEqualTo('A');
    }

    @Test
    public void testGetCharFieldMinValue() {
        testObject.charValue = '\u0000';
        char value = Narcissus.getCharField(testObject, charField);
        assertThat(value).isEqualTo('\u0000');
    }

    @Test
    public void testGetCharFieldMaxValue() {
        testObject.charValue = '\uFFFF';
        char value = Narcissus.getCharField(testObject, charField);
        assertThat(value).isEqualTo('\uFFFF');
    }

    @Test
    public void testGetByteFieldNormalValue() {
        testObject.byteValue = 100;
        byte value = Narcissus.getByteField(testObject, byteField);
        assertThat(value).isEqualTo((byte) 100);
    }

    @Test
    public void testGetByteFieldMaxValue() {
        testObject.byteValue = Byte.MAX_VALUE;
        byte value = Narcissus.getByteField(testObject, byteField);
        assertThat(value).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testGetByteFieldMinValue() {
        testObject.byteValue = Byte.MIN_VALUE;
        byte value = Narcissus.getByteField(testObject, byteField);
        assertThat(value).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testGetBooleanFieldTrue() {
        testObject.booleanValue = true;
        boolean value = Narcissus.getBooleanField(testObject, booleanField);
        assertThat(value).isTrue();
    }

    @Test
    public void testGetBooleanFieldFalse() {
        testObject.booleanValue = false;
        boolean value = Narcissus.getBooleanField(testObject, booleanField);
        assertThat(value).isFalse();
    }

    @Test
    public void testGetFloatFieldNormalValue() {
        testObject.floatValue = 3.14f;
        float value = Narcissus.getFloatField(testObject, floatField);
        assertThat(value).isEqualTo(3.14f);
    }

    @Test
    public void testGetFloatFieldMaxValue() {
        testObject.floatValue = Float.MAX_VALUE;
        float value = Narcissus.getFloatField(testObject, floatField);
        assertThat(value).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    public void testGetFloatFieldMinValue() {
        testObject.floatValue = Float.MIN_VALUE;
        float value = Narcissus.getFloatField(testObject, floatField);
        assertThat(value).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    public void testGetFloatFieldNaN() {
        testObject.floatValue = Float.NaN;
        float value = Narcissus.getFloatField(testObject, floatField);
        assertThat(value).isNaN();
    }

    @Test
    public void testGetDoubleFieldNormalValue() {
        testObject.doubleValue = 2.718281828;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isEqualTo(2.718281828);
    }

    @Test
    public void testGetDoubleFieldMaxValue() {
        testObject.doubleValue = Double.MAX_VALUE;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testGetDoubleFieldMinValue() {
        testObject.doubleValue = Double.MIN_VALUE;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    public void testGetDoubleFieldNaN() {
        testObject.doubleValue = Double.NaN;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isNaN();
    }

    @Test
    public void testGetDoubleFieldPositiveInfinity() {
        testObject.doubleValue = Double.POSITIVE_INFINITY;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testGetDoubleFieldNegativeInfinity() {
        testObject.doubleValue = Double.NEGATIVE_INFINITY;
        double value = Narcissus.getDoubleField(testObject, doubleField);
        assertThat(value).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testGetObjectFieldWithString() {
        String testStr = "Hello World";
        testObject.objectValue = testStr;
        Object value = Narcissus.getObjectField(testObject, objectField);
        assertThat(value).isEqualTo(testStr);
        assertThat(value).isSameAs(testStr);
    }

    @Test
    public void testGetObjectFieldWithNull() {
        testObject.objectValue = null;
        Object value = Narcissus.getObjectField(testObject, objectField);
        assertThat(value).isNull();
    }

    @Test
    public void testGenericGetIntField() {
        testObject.intValue = 42;
        Object value = Narcissus.getField(testObject, intField);
        assertThat(value).isInstanceOf(Integer.class);
        assertThat(value).isEqualTo(42);
    }

    @Test
    public void testGenericGetBooleanField() {
        testObject.booleanValue = true;
        Object value = Narcissus.getField(testObject, booleanField);
        assertThat(value).isInstanceOf(Boolean.class);
        assertThat(value).isEqualTo(true);
    }

    @Test
    public void testGenericGetObjectField() {
        testObject.objectValue = "test";
        Object value = Narcissus.getField(testObject, objectField);
        assertThat(value).isEqualTo("test");
    }

    // Test class for field operations
    public static class TestFieldClass {
        public int intValue;
        public long longValue;
        public short shortValue;
        public char charValue;
        public byte byteValue;
        public boolean booleanValue;
        public float floatValue;
        public double doubleValue;
        public Object objectValue;
    }
}