package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that field accessors reject a field with the wrong static modifier, and that instance field accessors
 * reject an object that is not an instance of the class that declares the field.
 *
 * <p>
 * Without the static modifier check, a static field accessed through the instance accessors is read at the static
 * field's offset relative to the object header, which reads unrelated memory. Without the receiver check, a field
 * of an unrelated class is read at that field's offset in an object that may be smaller than the offset.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFieldModifierMismatchTest {

    /** A class with one instance field and one static field of every type. */
    public static class Fields {
        int intField = 1;
        long longField = 2L;
        short shortField = 3;
        char charField = 'a';
        boolean booleanField = true;
        byte byteField = 4;
        float floatField = 5.0f;
        double doubleField = 6.0;
        Object objectField = "seven";

        static int staticIntField = 1;
        static long staticLongField = 2L;
        static short staticShortField = 3;
        static char staticCharField = 'a';
        static boolean staticBooleanField = true;
        static byte staticByteField = 4;
        static float staticFloatField = 5.0f;
        static double staticDoubleField = 6.0;
        static Object staticObjectField = "seven";
    }

    /** A class that declares no field of {@link Fields}, for use as a mismatched receiver. */
    public static class Unrelated {
        int unrelatedField = 1;
    }

    /** A subclass of {@link Fields}, which is a valid receiver for the fields of {@link Fields}. */
    public static class FieldsSubclass extends Fields {
        int subclassField = 100;
    }

    /** The names of the accessor types, in the order they are indexed by the loops below. */
    private static final String[] KINDS = { "int", "long", "short", "char", "boolean", "byte", "float", "double",
            "object" };

    /** The names of the instance fields of {@link Fields}, in the same order as {@link #KINDS}. */
    private static final String[] INSTANCE_FIELD_NAMES = { "intField", "longField", "shortField", "charField",
            "booleanField", "byteField", "floatField", "doubleField", "objectField" };

    /** The names of the static fields of {@link Fields}, in the same order as {@link #KINDS}. */
    private static final String[] STATIC_FIELD_NAMES = { "staticIntField", "staticLongField", "staticShortField",
            "staticCharField", "staticBooleanField", "staticByteField", "staticFloatField", "staticDoubleField",
            "staticObjectField" };

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Read an instance field using the accessor of the given kind.
     *
     * @param kind
     *            the index into {@link #KINDS} of the accessor to use
     * @param obj
     *            the object to read the field from
     * @param field
     *            the field to read
     */
    private static void get(final int kind, final Object obj, final Field field) {
        switch (kind) {
        case 0:
            Narcissus.getIntField(obj, field);
            break;
        case 1:
            Narcissus.getLongField(obj, field);
            break;
        case 2:
            Narcissus.getShortField(obj, field);
            break;
        case 3:
            Narcissus.getCharField(obj, field);
            break;
        case 4:
            Narcissus.getBooleanField(obj, field);
            break;
        case 5:
            Narcissus.getByteField(obj, field);
            break;
        case 6:
            Narcissus.getFloatField(obj, field);
            break;
        case 7:
            Narcissus.getDoubleField(obj, field);
            break;
        default:
            Narcissus.getObjectField(obj, field);
            break;
        }
    }

    /**
     * Write an instance field using the accessor of the given kind, writing the zero value of that type.
     *
     * @param kind
     *            the index into {@link #KINDS} of the accessor to use
     * @param obj
     *            the object to write the field in
     * @param field
     *            the field to write
     */
    private static void set(final int kind, final Object obj, final Field field) {
        switch (kind) {
        case 0:
            Narcissus.setIntField(obj, field, 0);
            break;
        case 1:
            Narcissus.setLongField(obj, field, 0L);
            break;
        case 2:
            Narcissus.setShortField(obj, field, (short) 0);
            break;
        case 3:
            Narcissus.setCharField(obj, field, '\0');
            break;
        case 4:
            Narcissus.setBooleanField(obj, field, false);
            break;
        case 5:
            Narcissus.setByteField(obj, field, (byte) 0);
            break;
        case 6:
            Narcissus.setFloatField(obj, field, 0.0f);
            break;
        case 7:
            Narcissus.setDoubleField(obj, field, 0.0);
            break;
        default:
            Narcissus.setObjectField(obj, field, null);
            break;
        }
    }

    /**
     * Read a static field using the accessor of the given kind.
     *
     * @param kind
     *            the index into {@link #KINDS} of the accessor to use
     * @param field
     *            the field to read
     */
    private static void getStatic(final int kind, final Field field) {
        switch (kind) {
        case 0:
            Narcissus.getStaticIntField(field);
            break;
        case 1:
            Narcissus.getStaticLongField(field);
            break;
        case 2:
            Narcissus.getStaticShortField(field);
            break;
        case 3:
            Narcissus.getStaticCharField(field);
            break;
        case 4:
            Narcissus.getStaticBooleanField(field);
            break;
        case 5:
            Narcissus.getStaticByteField(field);
            break;
        case 6:
            Narcissus.getStaticFloatField(field);
            break;
        case 7:
            Narcissus.getStaticDoubleField(field);
            break;
        default:
            Narcissus.getStaticObjectField(field);
            break;
        }
    }

    /**
     * Write a static field using the accessor of the given kind, writing the zero value of that type.
     *
     * @param kind
     *            the index into {@link #KINDS} of the accessor to use
     * @param field
     *            the field to write
     */
    private static void setStatic(final int kind, final Field field) {
        switch (kind) {
        case 0:
            Narcissus.setStaticIntField(field, 0);
            break;
        case 1:
            Narcissus.setStaticLongField(field, 0L);
            break;
        case 2:
            Narcissus.setStaticShortField(field, (short) 0);
            break;
        case 3:
            Narcissus.setStaticCharField(field, '\0');
            break;
        case 4:
            Narcissus.setStaticBooleanField(field, false);
            break;
        case 5:
            Narcissus.setStaticByteField(field, (byte) 0);
            break;
        case 6:
            Narcissus.setStaticFloatField(field, 0.0f);
            break;
        case 7:
            Narcissus.setStaticDoubleField(field, 0.0);
            break;
        default:
            Narcissus.setStaticObjectField(field, null);
            break;
        }
    }

    @Test
    public void testInstanceAccessorsRejectStaticField() throws Exception {
        final Fields obj = new Fields();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Field field = Narcissus.findField(Fields.class, STATIC_FIELD_NAMES[kind]);
            try {
                get(kind, obj, field);
                fail("get" + KINDS[kind] + "Field() should have rejected a static field");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
            try {
                set(kind, obj, field);
                fail("set" + KINDS[kind] + "Field() should have rejected a static field");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testStaticAccessorsRejectInstanceField() throws Exception {
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Field field = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[kind]);
            try {
                getStatic(kind, field);
                fail("getStatic" + KINDS[kind] + "Field() should have rejected a non-static field");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
            try {
                setStatic(kind, field);
                fail("setStatic" + KINDS[kind] + "Field() should have rejected a non-static field");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testInstanceAccessorsRejectUnrelatedReceiver() throws Exception {
        final Unrelated obj = new Unrelated();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Field field = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[kind]);
            try {
                get(kind, obj, field);
                fail("get" + KINDS[kind] + "Field() should have rejected an unrelated receiver");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
            try {
                set(kind, obj, field);
                fail("set" + KINDS[kind] + "Field() should have rejected an unrelated receiver");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testInstanceAccessorsAcceptSubclassReceiver() throws Exception {
        final FieldsSubclass obj = new FieldsSubclass();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Field field = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[kind]);
            // A subclass instance is a valid receiver for a superclass field
            get(kind, obj, field);
            set(kind, obj, field);
        }
        assertThat(obj.intField).isEqualTo(0);
        assertThat(obj.objectField).isNull();
        assertThat(obj.subclassField).isEqualTo(100);
    }

    @Test
    public void testGetFieldRejectsStaticField() throws Exception {
        final Fields obj = new Fields();
        final Field field = Narcissus.findField(Fields.class, "staticIntField");
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.getField(obj, field);
            }
        });
        assertThat(e.getMessage()).contains("getStaticField");
    }

    @Test
    public void testSetFieldRejectsStaticField() throws Exception {
        final Fields obj = new Fields();
        final Field field = Narcissus.findField(Fields.class, "staticIntField");
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setField(obj, field, Integer.valueOf(0));
            }
        });
        assertThat(e.getMessage()).contains("setStaticField");
    }

    @Test
    public void testGetStaticFieldRejectsInstanceField() throws Exception {
        final Field field = Narcissus.findField(Fields.class, "intField");
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.getStaticField(field);
            }
        });
        assertThat(e.getMessage()).contains("getField");
    }

    @Test
    public void testSetStaticFieldRejectsInstanceField() throws Exception {
        final Field field = Narcissus.findField(Fields.class, "intField");
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setStaticField(field, Integer.valueOf(0));
            }
        });
        assertThat(e.getMessage()).contains("setField");
    }

    @Test
    public void testGetFieldAndGetStaticFieldReturnEveryFieldType() throws Exception {
        final Fields obj = new Fields();
        final Object[] expectedInstanceValues = { Integer.valueOf(1), Long.valueOf(2L), Short.valueOf((short) 3),
                Character.valueOf('a'), Boolean.TRUE, Byte.valueOf((byte) 4), Float.valueOf(5.0f),
                Double.valueOf(6.0), "seven" };
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Field instanceField = Narcissus.findField(Fields.class, INSTANCE_FIELD_NAMES[kind]);
            assertThat(Narcissus.getField(obj, instanceField)).isEqualTo(expectedInstanceValues[kind]);
            final Field staticField = Narcissus.findField(Fields.class, STATIC_FIELD_NAMES[kind]);
            assertThat(Narcissus.getStaticField(staticField)).isEqualTo(expectedInstanceValues[kind]);
        }
    }
}
