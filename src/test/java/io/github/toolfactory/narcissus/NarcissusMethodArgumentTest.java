package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests the conversion of method invocation arguments: widening primitive conversion of boxed arguments, rejection
 * of arguments of the wrong type, rejection of a null argument for a primitive parameter, and rejection of a call
 * with the wrong number of arguments.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusMethodArgumentTest {

    /** A class with one method taking a single parameter of each primitive type, plus reference-typed methods. */
    public static class Params {
        static boolean takeBoolean(final boolean val) {
            return val;
        }

        static byte takeByte(final byte val) {
            return val;
        }

        static char takeChar(final char val) {
            return val;
        }

        static short takeShort(final short val) {
            return val;
        }

        static int takeInt(final int val) {
            return val;
        }

        static long takeLong(final long val) {
            return val;
        }

        static float takeFloat(final float val) {
            return val;
        }

        static double takeDouble(final double val) {
            return val;
        }

        static String takeCharSequence(final CharSequence val) {
            return String.valueOf(val);
        }

        static String takeNothing() {
            return "nothing";
        }

        static String takeTwo(final int first, final String second) {
            return first + ":" + second;
        }

        int instanceTakeInt(final int val) {
            return val;
        }
    }

    /** The names of the primitive types, in the order they are indexed by the loops below. */
    private static final String[] KINDS = { "boolean", "byte", "char", "short", "int", "long", "float", "double" };

    /** The names of the methods of {@link Params} taking one primitive parameter, in the same order. */
    private static final String[] METHOD_NAMES = { "takeBoolean", "takeByte", "takeChar", "takeShort", "takeInt",
            "takeLong", "takeFloat", "takeDouble" };

    /** The parameter types of those methods, in the same order. */
    private static final Class<?>[] PARAM_TYPES = { boolean.class, byte.class, char.class, short.class, int.class,
            long.class, float.class, double.class };

    /** One boxed argument value of each primitive type, in the same order as {@link #KINDS}. */
    private static final Object[] ARG_VALUES = { Boolean.TRUE, Byte.valueOf((byte) 7), Character.valueOf('A'),
            Short.valueOf((short) 7), Integer.valueOf(7), Long.valueOf(7L), Float.valueOf(7.5f),
            Double.valueOf(7.5) };

    /**
     * {@code WIDENS_TO[argKind]} has bit {@code paramKind} set if an argument of type {@code argKind} can be passed
     * to a parameter of type {@code paramKind} by widening primitive conversion (JLS 5.1.2).
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
     * Convert the argument value of the given kind to the boxed form the parameter of the given kind should receive
     * it in.
     *
     * @param argKind
     *            the index into {@link #KINDS} of the type of the argument
     * @param paramKind
     *            the index into {@link #KINDS} of the type of the parameter
     * @return the value the invoked method is expected to return
     */
    private static Object widened(final int argKind, final int paramKind) {
        if (paramKind == 0) {
            return Boolean.TRUE;
        }
        final double numericVal = argKind == 2 ? 'A' : ((Number) ARG_VALUES[argKind]).doubleValue();
        switch (paramKind) {
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
    public void testWideningPrimitiveConversionOfArguments() throws Exception {
        for (int paramKind = 0; paramKind < KINDS.length; paramKind++) {
            final Method method = Narcissus.findMethod(Params.class, METHOD_NAMES[paramKind],
                    PARAM_TYPES[paramKind]);
            for (int argKind = 0; argKind < KINDS.length; argKind++) {
                final boolean widens = (WIDENS_TO[argKind] & (1 << paramKind)) != 0;
                if (widens) {
                    assertThat(Narcissus.invokeStaticMethod(method, ARG_VALUES[argKind]))
                            .isEqualTo(widened(argKind, paramKind));
                } else {
                    try {
                        Narcissus.invokeStaticMethod(method, ARG_VALUES[argKind]);
                        fail(METHOD_NAMES[paramKind] + "() should have rejected a " + KINDS[argKind] + " argument");
                    } catch (final IllegalArgumentException e) {
                        assertThat(e.getMessage()).contains("wrong type");
                    }
                }
            }
        }
    }

    @Test
    public void testPrimitiveParameterRejectsNullArgument() throws Exception {
        for (int paramKind = 0; paramKind < KINDS.length; paramKind++) {
            final Method method = Narcissus.findMethod(Params.class, METHOD_NAMES[paramKind],
                    PARAM_TYPES[paramKind]);
            try {
                Narcissus.invokeStaticMethod(method, new Object[] { null });
                fail(METHOD_NAMES[paramKind] + "() should have rejected a null argument");
            } catch (final IllegalArgumentException e) {
                assertThat(e.getMessage()).contains("null argument");
            }
        }
    }

    @Test
    public void testPrimitiveParameterRejectsNonBoxedArgument() throws Exception {
        // Types that are Numbers, but are not boxed primitive types, must not be unboxed
        final Object[] nonBoxedValues = { "7", new java.math.BigInteger("7"), new java.math.BigDecimal("7.5"),
                new AtomicInteger(7), new int[] { 7 }, new Object() };
        for (int paramKind = 0; paramKind < KINDS.length; paramKind++) {
            final Method method = Narcissus.findMethod(Params.class, METHOD_NAMES[paramKind],
                    PARAM_TYPES[paramKind]);
            for (int i = 0; i < nonBoxedValues.length; i++) {
                try {
                    Narcissus.invokeStaticMethod(method, nonBoxedValues[i]);
                    fail(METHOD_NAMES[paramKind] + "() should have rejected an argument of type "
                            + nonBoxedValues[i].getClass().getName());
                } catch (final IllegalArgumentException e) {
                    assertThat(e.getMessage()).contains("wrong type");
                }
            }
        }
    }

    @Test
    public void testWrongNumberOfArguments() throws Exception {
        final Method takeInt = Narcissus.findMethod(Params.class, "takeInt", int.class);
        try {
            Narcissus.invokeStaticMethod(takeInt);
            fail("takeInt() should have rejected a call with no arguments");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }
        try {
            Narcissus.invokeStaticMethod(takeInt, Integer.valueOf(1), Integer.valueOf(2));
            fail("takeInt() should have rejected a call with two arguments");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }

        final Method takeNothing = Narcissus.findMethod(Params.class, "takeNothing");
        try {
            Narcissus.invokeStaticMethod(takeNothing, Integer.valueOf(1));
            fail("takeNothing() should have rejected a call with one argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }
    }

    @Test
    public void testNullArgsArrayIsTreatedAsNoArguments() throws Exception {
        final Method takeNothing = Narcissus.findMethod(Params.class, "takeNothing");
        assertThat(Narcissus.invokeStaticObjectMethod(takeNothing, (Object[]) null)).isEqualTo("nothing");

        final Method takeInt = Narcissus.findMethod(Params.class, "takeInt", int.class);
        try {
            Narcissus.invokeStaticIntMethod(takeInt, (Object[]) null);
            fail("takeInt() should have rejected a null args array");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }
    }

    @Test
    public void testReferenceParameterAcceptsNullAndSubtypes() throws Exception {
        final Method method = Narcissus.findMethod(Params.class, "takeCharSequence", CharSequence.class);
        assertThat(Narcissus.invokeStaticMethod(method, new Object[] { null })).isEqualTo("null");
        assertThat(Narcissus.invokeStaticMethod(method, "abc")).isEqualTo("abc");
        assertThat(Narcissus.invokeStaticMethod(method, new StringBuilder("def"))).isEqualTo("def");
    }

    @Test
    public void testReferenceParameterRejectsIncompatibleType() throws Exception {
        final Method method = Narcissus.findMethod(Params.class, "takeCharSequence", CharSequence.class);
        try {
            Narcissus.invokeStaticMethod(method, Integer.valueOf(1));
            fail("takeCharSequence() should have rejected an Integer argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("incompatible type");
        }
    }

    @Test
    public void testMultipleParameters() throws Exception {
        final Method method = Narcissus.findMethod(Params.class, "takeTwo", int.class, String.class);
        assertThat(Narcissus.invokeStaticMethod(method, Byte.valueOf((byte) 3), "x")).isEqualTo("3:x");
        try {
            Narcissus.invokeStaticMethod(method, "x", Integer.valueOf(3));
            fail("takeTwo() should have rejected arguments in the wrong order");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong type");
        }
    }

    @Test
    public void testArgumentsOfInstanceMethodAreConvertedTheSameWay() throws Exception {
        final Params obj = new Params();
        final Method method = Narcissus.findMethod(Params.class, "instanceTakeInt", int.class);
        assertThat(Narcissus.invokeIntMethod(obj, method, Short.valueOf((short) 9))).isEqualTo(9);
        try {
            Narcissus.invokeIntMethod(obj, method, Long.valueOf(9L));
            fail("instanceTakeInt() should have rejected a long argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong type");
        }
    }
}
