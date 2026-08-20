package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests the unboxing of the value passed to {@link Narcissus#setField(Object, Field, Object)} and
 * {@link Narcissus#setStaticField(Field, Object)}: widening primitive conversion (JLS 5.1.2) is applied where the
 * language allows it, and any other value is rejected with an {@link IllegalArgumentException}.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFieldValueConversionTest {

    /** A class with one instance field and one static field of each primitive type, plus an Object field. */
    public static class Fields {
        boolean booleanField;
        byte byteField;
        char charField;
        short shortField;
        int intField;
        long longField;
        float floatField;
        double doubleField;
        Object objectField;

        static boolean staticBooleanField;
        static byte staticByteField;
        static char staticCharField;
        static short staticShortField;
        static int staticIntField;
        static long staticLongField;
        static float staticFloatField;
        static double staticDoubleField;
        static Object staticObjectField;
    }

    /** The names of the primitive types, in the order they are indexed by the loops below. */
    private static final String[] KINDS = { "boolean", "byte", "char", "short", "int", "long", "float", "double" };

    /** The names of the instance fields of {@link Fields}, in the same order as {@link #KINDS}. */
    private static final String[] INSTANCE_FIELD_NAMES = { "booleanField", "byteField", "charField", "shortField",
            "intField", "longField", "floatField", "doubleField" };

    /** The names of the static fields of {@link Fields}, in the same order as {@link #KINDS}. */
    private static final String[] STATIC_FIELD_NAMES = { "staticBooleanField", "staticByteField", "staticCharField",
            "staticShortField", "staticIntField", "staticLongField", "staticFloatField", "staticDoubleField" };

    /** One boxed value of each primitive type, in the same order as {@link #KINDS}. */
    private static final Object[] VALUES = { Boolean.TRUE, Byte.valueOf((byte) 7), Character.valueOf('A'),
            Short.valueOf((short) 7), Integer.valueOf(7), Long.valueOf(7L), Float.valueOf(7.5f),
            Double.valueOf(7.5) };

    /**
     * {@code WIDENS_TO[valKind]} has bit {@code fieldKind} set if a value of type {@code valKind} can be assigned
     * to a field of type {@code fieldKind} by widening primitive conversion (JLS 5.1.2).
     */
    private static final int[] WIDENS_TO = { //
            1 << 0, // boolean -> boolean
            1 << 1 | 1 << 3 | 1 << 4 | 1 << 5 | 1 << 6 | 1 << 7, // byte -> byte, short, int, long, float, double
            1 << 2 | 1 << 4 | 1 << 5 | 1 << 6 | 1 << 7, // char -> char, int, long, float, double
            1 << 3 | 1 << 4 | 1 << 5 | 1 << 6 | 1 << 7, // short -> short, int, long, float, double
            1 << 4 | 1 << 5 | 1 << 6 | 1 << 7, // int -> int, long, float, double
            1 << 5 | 1 << 6 | 1 << 7, // long -> long, float, double
            1 << 6 | 1 << 7, // float -> float, double
            1 << 7 // double -> double
    };

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Convert the value of the given kind to the boxed form a field of the given kind should hold after the value
     * is assigned to it.
     *
     * @param valKind
     *            the index into {@link #KINDS} of the type of the value
     * @param fieldKind
     *            the index into {@link #KINDS} of the type of the field
     * @return the value the field is expected to hold
     */
    private static Object widened(final int valKind, final int fieldKind) {
        if (fieldKind == 0) {
            return Boolean.TRUE;
        }
        final double numericVal = valKind == 2 ? 'A' : ((Number) VALUES[valKind]).doubleValue();
        switch (fieldKind) {
        case 1:
            return Byte.valueOf((byte) numericVal);
        case 2:
            return Character.valueOf((char) numericVal);
        case 3:
            return Short.valueOf((short) numericVal);
        case 4:
            return Integer.valueOf((int) numericVal);
        case 5:
            return Long.valueOf((long) numericVal);
        case 6:
            return Float.valueOf((float) numericVal);
        default:
            return Double.valueOf(numericVal);
        }
    }

    @Test
    public void testSetFieldAppliesWideningPrimitiveConversion() throws Exception {
        final Fields obj = new Fields();
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[fieldKind]);
            for (int valKind = 0; valKind < KINDS.length; valKind++) {
                if ((WIDENS_TO[valKind] & (1 << fieldKind)) != 0) {
                    Narcissus.setField(obj, field, VALUES[valKind]);
                    assertThat(Narcissus.getField(obj, field)).isEqualTo(widened(valKind, fieldKind));
                } else {
                    try {
                        Narcissus.setField(obj, field, VALUES[valKind]);
                        fail("setField() should have rejected a " + KINDS[valKind] + " value for a "
                                + KINDS[fieldKind] + " field");
                    } catch (final IllegalArgumentException e) {
                        assertThat(e.getMessage()).contains(KINDS[fieldKind]);
                    }
                }
            }
        }
    }

    @Test
    public void testSetStaticFieldAppliesWideningPrimitiveConversion() throws Exception {
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(Fields.class, STATIC_FIELD_NAMES[fieldKind]);
            for (int valKind = 0; valKind < KINDS.length; valKind++) {
                if ((WIDENS_TO[valKind] & (1 << fieldKind)) != 0) {
                    Narcissus.setStaticField(field, VALUES[valKind]);
                    assertThat(Narcissus.getStaticField(field)).isEqualTo(widened(valKind, fieldKind));
                } else {
                    try {
                        Narcissus.setStaticField(field, VALUES[valKind]);
                        fail("setStaticField() should have rejected a " + KINDS[valKind] + " value for a "
                                + KINDS[fieldKind] + " field");
                    } catch (final IllegalArgumentException e) {
                        assertThat(e.getMessage()).contains(KINDS[fieldKind]);
                    }
                }
            }
        }
    }

    @Test
    public void testSetFieldRejectsNullForPrimitiveField() throws Exception {
        final Fields obj = new Fields();
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field instanceField = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[fieldKind]);
            try {
                Narcissus.setField(obj, instanceField, null);
                fail("setField() should have rejected a null value for a " + KINDS[fieldKind] + " field");
            } catch (final IllegalArgumentException e) {
                assertThat(e.getMessage()).contains("null");
            }
            final Field staticField = Narcissus.findField(Fields.class, STATIC_FIELD_NAMES[fieldKind]);
            try {
                Narcissus.setStaticField(staticField, null);
                fail("setStaticField() should have rejected a null value for a " + KINDS[fieldKind] + " field");
            } catch (final IllegalArgumentException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testSetFieldRejectsValueThatIsNotABoxedPrimitive() throws Exception {
        // Types that are Numbers, but are not boxed primitive types, must not be unboxed
        final Object[] nonBoxedValues = { "7", new BigInteger("7"), new BigDecimal("7.5"), new AtomicInteger(7),
                new int[] { 7 }, new Object() };
        final Fields obj = new Fields();
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[fieldKind]);
            for (int i = 0; i < nonBoxedValues.length; i++) {
                try {
                    Narcissus.setField(obj, field, nonBoxedValues[i]);
                    fail("setField() should have rejected a " + nonBoxedValues[i].getClass().getName()
                            + " value for a " + KINDS[fieldKind] + " field");
                } catch (final IllegalArgumentException e) {
                    assertThat(e.getMessage()).contains(nonBoxedValues[i].getClass().getName());
                }
            }
        }
    }

    @Test
    public void testSetFieldAcceptsNullAndSubtypesForReferenceField() throws Exception {
        final Fields obj = new Fields();
        final Field field = Narcissus.findField(Fields.class, "objectField");
        Narcissus.setField(obj, field, "abc");
        assertThat(obj.objectField).isEqualTo("abc");
        Narcissus.setField(obj, field, null);
        assertThat(obj.objectField).isNull();

        final Field staticField = Narcissus.findField(Fields.class, "staticObjectField");
        Narcissus.setStaticField(staticField, Integer.valueOf(1));
        assertThat(Fields.staticObjectField).isEqualTo(Integer.valueOf(1));
        Narcissus.setStaticField(staticField, null);
        assertThat(Fields.staticObjectField).isNull();
    }

    @Test
    public void testExceptionMessageNamesTheValueTypeAndTheField() throws Exception {
        final Fields obj = new Fields();
        final Field field = Narcissus.findField(Fields.class, "intField");
        try {
            Narcissus.setField(obj, field, "abc");
            fail("setField() should have rejected a String value for an int field");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("java.lang.String").contains("intField").contains("int");
        }
    }
}
