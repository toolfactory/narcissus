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
 * Tests that a typed field accessor rejects a field whose declared type does not match the accessor type.
 *
 * <p>
 * Without this check, e.g. calling {@code getLongField()} on an {@code int} field reads eight bytes from the offset
 * of a four-byte field, which reads past the end of the object, and calling {@code setObjectField()} on a
 * primitive field writes a reference into a primitive slot, which corrupts the heap and crashes the JVM in the
 * next garbage collection.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFieldTypeMismatchTest {

    /** A class with one instance field and one static field of every type. */
    public static class AllTypes {
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

    /** A class with fields of several different reference types. */
    public static class ReferenceTypes {
        String stringField = "str";
        Integer boxedIntField = Integer.valueOf(1);
        int[] intArrayField = { 1, 2, 3 };
        String[] stringArrayField = { "a" };
        CharSequence charSequenceField = "cs";

        static String staticStringField = "str";
    }

    /** The names of the accessor types, in the order they are indexed by the loops below. */
    private static final String[] KINDS = { "int", "long", "short", "char", "boolean", "byte", "float", "double",
            "object" };

    /** The names of the instance fields of {@link AllTypes}, in the same order as {@link #KINDS}. */
    private static final String[] INSTANCE_FIELD_NAMES = { "intField", "longField", "shortField", "charField",
            "booleanField", "byteField", "floatField", "doubleField", "objectField" };

    /** The names of the static fields of {@link AllTypes}, in the same order as {@link #KINDS}. */
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
     * @return the field value, boxed
     */
    private static Object get(final int kind, final Object obj, final Field field) {
        switch (kind) {
        case 0:
            return Integer.valueOf(Narcissus.getIntField(obj, field));
        case 1:
            return Long.valueOf(Narcissus.getLongField(obj, field));
        case 2:
            return Short.valueOf(Narcissus.getShortField(obj, field));
        case 3:
            return Character.valueOf(Narcissus.getCharField(obj, field));
        case 4:
            return Boolean.valueOf(Narcissus.getBooleanField(obj, field));
        case 5:
            return Byte.valueOf(Narcissus.getByteField(obj, field));
        case 6:
            return Float.valueOf(Narcissus.getFloatField(obj, field));
        case 7:
            return Double.valueOf(Narcissus.getDoubleField(obj, field));
        default:
            return Narcissus.getObjectField(obj, field);
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
     * @return the field value, boxed
     */
    private static Object getStatic(final int kind, final Field field) {
        switch (kind) {
        case 0:
            return Integer.valueOf(Narcissus.getStaticIntField(field));
        case 1:
            return Long.valueOf(Narcissus.getStaticLongField(field));
        case 2:
            return Short.valueOf(Narcissus.getStaticShortField(field));
        case 3:
            return Character.valueOf(Narcissus.getStaticCharField(field));
        case 4:
            return Boolean.valueOf(Narcissus.getStaticBooleanField(field));
        case 5:
            return Byte.valueOf(Narcissus.getStaticByteField(field));
        case 6:
            return Float.valueOf(Narcissus.getStaticFloatField(field));
        case 7:
            return Double.valueOf(Narcissus.getStaticDoubleField(field));
        default:
            return Narcissus.getStaticObjectField(field);
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
    public void testInstanceGetterRejectsMismatchedFieldType() throws Exception {
        final AllTypes obj = new AllTypes();
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(AllTypes.class, INSTANCE_FIELD_NAMES[fieldKind]);
            for (int accessorKind = 0; accessorKind < KINDS.length; accessorKind++) {
                if (accessorKind == fieldKind) {
                    // The matching accessor must work
                    get(accessorKind, obj, field);
                } else {
                    try {
                        get(accessorKind, obj, field);
                        fail("get" + KINDS[accessorKind] + "Field() should have rejected a field of type "
                                + KINDS[fieldKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testInstanceSetterRejectsMismatchedFieldType() throws Exception {
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(AllTypes.class, INSTANCE_FIELD_NAMES[fieldKind]);
            for (int accessorKind = 0; accessorKind < KINDS.length; accessorKind++) {
                // Use a fresh object for each write, so that a write that succeeds cannot affect a later case
                final AllTypes obj = new AllTypes();
                if (accessorKind == fieldKind) {
                    set(accessorKind, obj, field);
                } else {
                    try {
                        set(accessorKind, obj, field);
                        fail("set" + KINDS[accessorKind] + "Field() should have rejected a field of type "
                                + KINDS[fieldKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testStaticGetterRejectsMismatchedFieldType() throws Exception {
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(AllTypes.class, STATIC_FIELD_NAMES[fieldKind]);
            for (int accessorKind = 0; accessorKind < KINDS.length; accessorKind++) {
                if (accessorKind == fieldKind) {
                    getStatic(accessorKind, field);
                } else {
                    try {
                        getStatic(accessorKind, field);
                        fail("getStatic" + KINDS[accessorKind] + "Field() should have rejected a field of type "
                                + KINDS[fieldKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testStaticSetterRejectsMismatchedFieldType() throws Exception {
        for (int fieldKind = 0; fieldKind < KINDS.length; fieldKind++) {
            final Field field = Narcissus.findField(AllTypes.class, STATIC_FIELD_NAMES[fieldKind]);
            for (int accessorKind = 0; accessorKind < KINDS.length; accessorKind++) {
                if (accessorKind == fieldKind) {
                    setStatic(accessorKind, field);
                } else {
                    try {
                        setStatic(accessorKind, field);
                        fail("setStatic" + KINDS[accessorKind] + "Field() should have rejected a field of type "
                                + KINDS[fieldKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testObjectAccessorsWorkForEveryReferenceType() throws Exception {
        final ReferenceTypes obj = new ReferenceTypes();
        final String[] fieldNames = { "stringField", "boxedIntField", "intArrayField", "stringArrayField",
                "charSequenceField" };
        for (final String fieldName : fieldNames) {
            final Field field = Narcissus.findField(ReferenceTypes.class, fieldName);
            assertThat(Narcissus.getObjectField(obj, field)).isNotNull();
            Narcissus.setObjectField(obj, field, null);
            assertThat(Narcissus.getObjectField(obj, field)).isNull();
        }
    }

    @Test
    public void testSetObjectFieldRejectsValueOfIncompatibleType() throws Exception {
        final ReferenceTypes obj = new ReferenceTypes();
        final Field field = Narcissus.findField(ReferenceTypes.class, "stringField");
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setObjectField(obj, field, Integer.valueOf(1));
            }
        });
        // The field must be unchanged
        assertThat(obj.stringField).isEqualTo("str");
    }

    @Test
    public void testSetObjectFieldAcceptsSubtypeOfFieldType() throws Exception {
        final ReferenceTypes obj = new ReferenceTypes();
        final Field field = Narcissus.findField(ReferenceTypes.class, "charSequenceField");
        Narcissus.setObjectField(obj, field, "a String is a CharSequence");
        assertThat(obj.charSequenceField).isEqualTo("a String is a CharSequence");
    }

    @Test
    public void testSetStaticObjectFieldRejectsValueOfIncompatibleType() throws Exception {
        final Field field = Narcissus.findField(ReferenceTypes.class, "staticStringField");
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setStaticObjectField(field, Integer.valueOf(1));
            }
        });
    }

    @Test
    public void testSetObjectFieldAcceptsNullForAnyReferenceType() throws Exception {
        final ReferenceTypes obj = new ReferenceTypes();
        final Field field = Narcissus.findField(ReferenceTypes.class, "intArrayField");
        Narcissus.setObjectField(obj, field, null);
        assertThat(obj.intArrayField).isNull();
    }

    @Test
    public void testSetObjectFieldRejectsNullForPrimitiveField() throws Exception {
        final AllTypes obj = new AllTypes();
        final Field field = Narcissus.findField(AllTypes.class, "intField");
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setObjectField(obj, field, null);
            }
        });
        assertThat(obj.intField).isEqualTo(1);
    }

    @Test
    public void testSetStaticObjectFieldRejectsNullForPrimitiveField() throws Exception {
        final Field field = Narcissus.findField(AllTypes.class, "staticIntField");
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                Narcissus.setStaticObjectField(field, null);
            }
        });
    }
}
