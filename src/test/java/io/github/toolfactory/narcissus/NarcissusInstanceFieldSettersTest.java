package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Narcissus instance field setter methods.
 * Tests all primitive types: int, long, short, char, byte, boolean, float, double, Object.
 */
public class NarcissusInstanceFieldSettersTest {

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
    public void testSetIntFieldNormalValue() {
        Narcissus.setIntField(testObject, intField, 42);
        assertThat(testObject.intValue).isEqualTo(42);
    }

    @Test
    public void testSetIntFieldMaxValue() {
        Narcissus.setIntField(testObject, intField, Integer.MAX_VALUE);
        assertThat(testObject.intValue).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testSetIntFieldMinValue() {
        Narcissus.setIntField(testObject, intField, Integer.MIN_VALUE);
        assertThat(testObject.intValue).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testSetIntFieldZero() {
        Narcissus.setIntField(testObject, intField, 0);
        assertThat(testObject.intValue).isEqualTo(0);
    }

    @Test
    public void testSetLongFieldNormalValue() {
        Narcissus.setLongField(testObject, longField, 123456789L);
        assertThat(testObject.longValue).isEqualTo(123456789L);
    }

    @Test
    public void testSetLongFieldMaxValue() {
        Narcissus.setLongField(testObject, longField, Long.MAX_VALUE);
        assertThat(testObject.longValue).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testSetLongFieldMinValue() {
        Narcissus.setLongField(testObject, longField, Long.MIN_VALUE);
        assertThat(testObject.longValue).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testSetLongFieldZero() {
        Narcissus.setLongField(testObject, longField, 0L);
        assertThat(testObject.longValue).isEqualTo(0L);
    }

    @Test
    public void testSetShortFieldNormalValue() {
        Narcissus.setShortField(testObject, shortField, (short) 1000);
        assertThat(testObject.shortValue).isEqualTo((short) 1000);
    }

    @Test
    public void testSetShortFieldMaxValue() {
        Narcissus.setShortField(testObject, shortField, Short.MAX_VALUE);
        assertThat(testObject.shortValue).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    public void testSetShortFieldMinValue() {
        Narcissus.setShortField(testObject, shortField, Short.MIN_VALUE);
        assertThat(testObject.shortValue).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    public void testSetShortFieldZero() {
        Narcissus.setShortField(testObject, shortField, (short) 0);
        assertThat(testObject.shortValue).isEqualTo((short) 0);
    }

    @Test
    public void testSetCharFieldNormalValue() {
        Narcissus.setCharField(testObject, charField, 'A');
        assertThat(testObject.charValue).isEqualTo('A');
    }

    @Test
    public void testSetCharFieldMinValue() {
        Narcissus.setCharField(testObject, charField, '\u0000');
        assertThat(testObject.charValue).isEqualTo('\u0000');
    }

    @Test
    public void testSetCharFieldMaxValue() {
        Narcissus.setCharField(testObject, charField, '\uFFFF');
        assertThat(testObject.charValue).isEqualTo('\uFFFF');
    }

    @Test
    public void testSetCharFieldSpecialValue() {
        Narcissus.setCharField(testObject, charField, '™');
        assertThat(testObject.charValue).isEqualTo('™');
    }

    @Test
    public void testSetByteFieldNormalValue() {
        Narcissus.setByteField(testObject, byteField, (byte) 100);
        assertThat(testObject.byteValue).isEqualTo((byte) 100);
    }

    @Test
    public void testSetByteFieldMaxValue() {
        Narcissus.setByteField(testObject, byteField, Byte.MAX_VALUE);
        assertThat(testObject.byteValue).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testSetByteFieldMinValue() {
        Narcissus.setByteField(testObject, byteField, Byte.MIN_VALUE);
        assertThat(testObject.byteValue).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testSetByteFieldZero() {
        Narcissus.setByteField(testObject, byteField, (byte) 0);
        assertThat(testObject.byteValue).isEqualTo((byte) 0);
    }

    @Test
    public void testSetBooleanFieldTrue() {
        Narcissus.setBooleanField(testObject, booleanField, true);
        assertThat(testObject.booleanValue).isTrue();
    }

    @Test
    public void testSetBooleanFieldFalse() {
        Narcissus.setBooleanField(testObject, booleanField, false);
        assertThat(testObject.booleanValue).isFalse();
    }

    @Test
    public void testSetFloatFieldNormalValue() {
        Narcissus.setFloatField(testObject, floatField, 3.14f);
        assertThat(testObject.floatValue).isEqualTo(3.14f);
    }

    @Test
    public void testSetFloatFieldMaxValue() {
        Narcissus.setFloatField(testObject, floatField, Float.MAX_VALUE);
        assertThat(testObject.floatValue).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    public void testSetFloatFieldMinValue() {
        Narcissus.setFloatField(testObject, floatField, Float.MIN_VALUE);
        assertThat(testObject.floatValue).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    public void testSetFloatFieldNaN() {
        Narcissus.setFloatField(testObject, floatField, Float.NaN);
        assertThat(testObject.floatValue).isNaN();
    }

    @Test
    public void testSetFloatFieldPositiveInfinity() {
        Narcissus.setFloatField(testObject, floatField, Float.POSITIVE_INFINITY);
        assertThat(testObject.floatValue).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testSetFloatFieldNegativeInfinity() {
        Narcissus.setFloatField(testObject, floatField, Float.NEGATIVE_INFINITY);
        assertThat(testObject.floatValue).isEqualTo(Float.NEGATIVE_INFINITY);
    }

    @Test
    public void testSetDoubleFieldNormalValue() {
        Narcissus.setDoubleField(testObject, doubleField, 2.718281828);
        assertThat(testObject.doubleValue).isEqualTo(2.718281828);
    }

    @Test
    public void testSetDoubleFieldMaxValue() {
        Narcissus.setDoubleField(testObject, doubleField, Double.MAX_VALUE);
        assertThat(testObject.doubleValue).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testSetDoubleFieldMinValue() {
        Narcissus.setDoubleField(testObject, doubleField, Double.MIN_VALUE);
        assertThat(testObject.doubleValue).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    public void testSetDoubleFieldNaN() {
        Narcissus.setDoubleField(testObject, doubleField, Double.NaN);
        assertThat(testObject.doubleValue).isNaN();
    }

    @Test
    public void testSetDoubleFieldPositiveInfinity() {
        Narcissus.setDoubleField(testObject, doubleField, Double.POSITIVE_INFINITY);
        assertThat(testObject.doubleValue).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testSetDoubleFieldNegativeInfinity() {
        Narcissus.setDoubleField(testObject, doubleField, Double.NEGATIVE_INFINITY);
        assertThat(testObject.doubleValue).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testSetObjectFieldStringValue() {
        String testStr = "Hello World";
        Narcissus.setObjectField(testObject, objectField, testStr);
        assertThat(testObject.objectValue).isEqualTo(testStr);
        assertThat(testObject.objectValue).isSameAs(testStr);
    }

    @Test
    public void testSetObjectFieldNullValue() {
        Narcissus.setObjectField(testObject, objectField, null);
        assertThat(testObject.objectValue).isNull();
    }

    @Test
    public void testSetObjectFieldIntegerValue() {
        Integer intObj = Integer.valueOf(42);
        Narcissus.setObjectField(testObject, objectField, intObj);
        assertThat(testObject.objectValue).isEqualTo(intObj);
        assertThat(testObject.objectValue).isSameAs(intObj);
    }

    @Test
    public void testGenericSetFieldIntValue() {
        Narcissus.setField(testObject, intField, 42);
        assertThat(testObject.intValue).isEqualTo(42);
    }

    @Test
    public void testGenericSetFieldBooleanValue() {
        Narcissus.setField(testObject, booleanField, true);
        assertThat(testObject.booleanValue).isTrue();
    }

    @Test
    public void testGenericSetFieldObjectValue() {
        Narcissus.setField(testObject, objectField, "test");
        assertThat(testObject.objectValue).isEqualTo("test");
    }

    @Test
    public void testGenericSetFieldBoxedLongValue() {
        Narcissus.setField(testObject, longField, Long.valueOf(999L));
        assertThat(testObject.longValue).isEqualTo(999L);
    }

    @Test
    public void testGenericSetFieldBoxedDoubleValue() {
        Narcissus.setField(testObject, doubleField, Double.valueOf(1.23));
        assertThat(testObject.doubleValue).isEqualTo(1.23);
    }

    @Test
    public void testSetFieldRoundTrip() {
        // Test that set and get operations work together correctly
        Narcissus.setIntField(testObject, intField, 777);
        int retrievedInt = Narcissus.getIntField(testObject, intField);
        assertThat(retrievedInt).isEqualTo(777);
        
        Narcissus.setObjectField(testObject, objectField, "roundtrip");
        Object retrievedObject = Narcissus.getObjectField(testObject, objectField);
        assertThat(retrievedObject).isEqualTo("roundtrip");
        
        Narcissus.setBooleanField(testObject, booleanField, true);
        boolean retrievedBool = Narcissus.getBooleanField(testObject, booleanField);
        assertThat(retrievedBool).isTrue();
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