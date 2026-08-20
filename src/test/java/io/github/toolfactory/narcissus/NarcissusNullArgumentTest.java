package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that every entry point rejects a null argument with a {@link NullPointerException}, rather than
 * dereferencing it in native code, which would crash the JVM.
 *
 * <p>
 * Each of the typed field accessors and method invokers is covered, with the null in each argument position in
 * turn.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusNullArgumentTest {

    /** A class with a field and a method of each type. */
    public static class Target {
        int intField;
        long longField;
        short shortField;
        char charField;
        boolean booleanField;
        byte byteField;
        float floatField;
        double doubleField;
        Object objectField;

        static int staticIntField;
        static long staticLongField;
        static short staticShortField;
        static char staticCharField;
        static boolean staticBooleanField;
        static byte staticByteField;
        static float staticFloatField;
        static double staticDoubleField;
        static Object staticObjectField;

        void retVoid() {
        }

        int retInt() {
            return 0;
        }

        long retLong() {
            return 0;
        }

        short retShort() {
            return 0;
        }

        char retChar() {
            return 0;
        }

        boolean retBoolean() {
            return false;
        }

        byte retByte() {
            return 0;
        }

        float retFloat() {
            return 0;
        }

        double retDouble() {
            return 0;
        }

        Object retObject() {
            return null;
        }
    }

    /** The number of typed field accessors, and of typed method invokers minus the void invoker. */
    private static final int NUM_FIELD_KINDS = 9;

    /** The number of typed method invokers. */
    private static final int NUM_METHOD_KINDS = 10;

    /** The names of the instance fields of {@link Target}. */
    private static final String[] FIELD_NAMES = { "intField", "longField", "shortField", "charField",
            "booleanField", "byteField", "floatField", "doubleField", "objectField" };

    /** The names of the static fields of {@link Target}. */
    private static final String[] STATIC_FIELD_NAMES = { "staticIntField", "staticLongField", "staticShortField",
            "staticCharField", "staticBooleanField", "staticByteField", "staticFloatField", "staticDoubleField",
            "staticObjectField" };

    /** The names of the methods of {@link Target}. */
    private static final String[] METHOD_NAMES = { "retVoid", "retInt", "retLong", "retShort", "retChar",
            "retBoolean", "retByte", "retFloat", "retDouble", "retObject" };

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Read an instance field using the typed accessor of the given kind.
     *
     * @param kind
     *            the accessor to use
     * @param obj
     *            the object to read the field from
     * @param field
     *            the field to read
     */
    private static void getField(final int kind, final Object obj, final Field field) {
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
     * Write an instance field using the typed accessor of the given kind.
     *
     * @param kind
     *            the accessor to use
     * @param obj
     *            the object to write the field in
     * @param field
     *            the field to write
     */
    private static void setField(final int kind, final Object obj, final Field field) {
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
     * Read a static field using the typed accessor of the given kind.
     *
     * @param kind
     *            the accessor to use
     * @param field
     *            the field to read
     */
    private static void getStaticField(final int kind, final Field field) {
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
     * Write a static field using the typed accessor of the given kind.
     *
     * @param kind
     *            the accessor to use
     * @param field
     *            the field to write
     */
    private static void setStaticField(final int kind, final Field field) {
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

    /**
     * Invoke an instance method using the typed invoker of the given kind.
     *
     * @param kind
     *            the invoker to use
     * @param obj
     *            the object to invoke the method on
     * @param method
     *            the method to invoke
     */
    private static void invoke(final int kind, final Object obj, final Method method) {
        switch (kind) {
        case 0:
            Narcissus.invokeVoidMethod(obj, method);
            break;
        case 1:
            Narcissus.invokeIntMethod(obj, method);
            break;
        case 2:
            Narcissus.invokeLongMethod(obj, method);
            break;
        case 3:
            Narcissus.invokeShortMethod(obj, method);
            break;
        case 4:
            Narcissus.invokeCharMethod(obj, method);
            break;
        case 5:
            Narcissus.invokeBooleanMethod(obj, method);
            break;
        case 6:
            Narcissus.invokeByteMethod(obj, method);
            break;
        case 7:
            Narcissus.invokeFloatMethod(obj, method);
            break;
        case 8:
            Narcissus.invokeDoubleMethod(obj, method);
            break;
        default:
            Narcissus.invokeObjectMethod(obj, method);
            break;
        }
    }

    /**
     * Invoke a static method using the typed invoker of the given kind.
     *
     * @param kind
     *            the invoker to use
     * @param method
     *            the method to invoke
     */
    private static void invokeStatic(final int kind, final Method method) {
        switch (kind) {
        case 0:
            Narcissus.invokeStaticVoidMethod(method);
            break;
        case 1:
            Narcissus.invokeStaticIntMethod(method);
            break;
        case 2:
            Narcissus.invokeStaticLongMethod(method);
            break;
        case 3:
            Narcissus.invokeStaticShortMethod(method);
            break;
        case 4:
            Narcissus.invokeStaticCharMethod(method);
            break;
        case 5:
            Narcissus.invokeStaticBooleanMethod(method);
            break;
        case 6:
            Narcissus.invokeStaticByteMethod(method);
            break;
        case 7:
            Narcissus.invokeStaticFloatMethod(method);
            break;
        case 8:
            Narcissus.invokeStaticDoubleMethod(method);
            break;
        default:
            Narcissus.invokeStaticObjectMethod(method);
            break;
        }
    }

    @Test
    public void testTypedFieldAccessorsRejectNullObject() throws Exception {
        for (int kind = 0; kind < NUM_FIELD_KINDS; kind++) {
            final Field field = Narcissus.findField(Target.class, FIELD_NAMES[kind]);
            try {
                getField(kind, null, field);
                fail("field getter " + kind + " should have rejected a null object");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
            try {
                setField(kind, null, field);
                fail("field setter " + kind + " should have rejected a null object");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testTypedFieldAccessorsRejectNullField() {
        final Target obj = new Target();
        for (int kind = 0; kind < NUM_FIELD_KINDS; kind++) {
            try {
                getField(kind, obj, null);
                fail("field getter " + kind + " should have rejected a null field");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
            try {
                setField(kind, obj, null);
                fail("field setter " + kind + " should have rejected a null field");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testTypedStaticFieldAccessorsRejectNullField() {
        for (int kind = 0; kind < NUM_FIELD_KINDS; kind++) {
            try {
                getStaticField(kind, null);
                fail("static field getter " + kind + " should have rejected a null field");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
            try {
                setStaticField(kind, null);
                fail("static field setter " + kind + " should have rejected a null field");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testTypedInvokersRejectNullObject() throws Exception {
        for (int kind = 0; kind < NUM_METHOD_KINDS; kind++) {
            final Method method = Narcissus.findMethod(Target.class, METHOD_NAMES[kind]);
            try {
                invoke(kind, null, method);
                fail("invoker " + kind + " should have rejected a null object");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testTypedInvokersRejectNullMethod() {
        final Target obj = new Target();
        for (int kind = 0; kind < NUM_METHOD_KINDS; kind++) {
            try {
                invoke(kind, obj, null);
                fail("invoker " + kind + " should have rejected a null method");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
            try {
                invokeStatic(kind, null);
                fail("static invoker " + kind + " should have rejected a null method");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testBoxingDispatchersRejectNullArguments() throws Exception {
        final Target obj = new Target();
        final Field field = Narcissus.findField(Target.class, "intField");
        final Field staticField = Narcissus.findField(Target.class, "staticIntField");
        final Method method = Narcissus.findMethod(Target.class, "retInt");
        final Method staticMethod = Narcissus.findMethod(Narcissus.class, "findClass", String.class);

        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.getField(null, field);
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.getField(obj, null);
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.setField(null, field, Integer.valueOf(0));
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.setField(obj, null, Integer.valueOf(0));
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.getStaticField(null);
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.setStaticField(null, Integer.valueOf(0));
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.invokeMethod(null, method);
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.invokeMethod(obj, null);
            }
        });
        assertNullPointerException(new Runnable() {
            @Override
            public void run() {
                Narcissus.invokeStaticMethod(null);
            }
        });

        // These are the same objects, used above, that make the calls succeed when they are not null
        assertThat(Narcissus.getField(obj, field)).isEqualTo(Integer.valueOf(0));
        assertThat(Narcissus.getStaticField(staticField)).isEqualTo(Integer.valueOf(0));
        assertThat(Narcissus.invokeMethod(obj, method)).isEqualTo(Integer.valueOf(0));
        assertThat(Narcissus.invokeStaticMethod(staticMethod, "int")).isEqualTo(int.class);
    }

    @Test
    public void testAllocateInstanceRejectsNull() {
        try {
            Narcissus.allocateInstance(null);
            fail("allocateInstance(null) should have thrown NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage()).contains("null");
        }
    }

    @Test
    public void testSneakyThrowRejectsNull() {
        try {
            Narcissus.sneakyThrow(null);
            fail("sneakyThrow(null) should have thrown NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage()).contains("null");
        }
    }

    /**
     * Assert that running the given task throws a {@link NullPointerException}.
     *
     * @param task
     *            the task to run
     */
    private static void assertNullPointerException(final Runnable task) {
        try {
            task.run();
            fail("Expected NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage()).contains("null");
        }
    }
}
