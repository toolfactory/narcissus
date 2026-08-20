package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests the handling of the arguments of a varargs method: trailing arguments passed individually are packed into
 * an array of the declared varargs element type, and a single trailing argument that is already an array of the
 * declared type is passed through unchanged.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusVarargsTest {

    /** A class with varargs methods taking a reference element type, a primitive element type, and Object. */
    public static class Varargs {
        static String join(final String prefix, final String... parts) {
            return prefix + Arrays.toString(parts);
        }

        static int sum(final int... vals) {
            int total = 0;
            for (int i = 0; i < vals.length; i++) {
                total += vals[i];
            }
            return total;
        }

        static long sumLongs(final long... vals) {
            long total = 0;
            for (int i = 0; i < vals.length; i++) {
                total += vals[i];
            }
            return total;
        }

        static double sumDoubles(final double... vals) {
            double total = 0;
            for (int i = 0; i < vals.length; i++) {
                total += vals[i];
            }
            return total;
        }

        static String countBooleans(final boolean... vals) {
            int numTrue = 0;
            for (int i = 0; i < vals.length; i++) {
                if (vals[i]) {
                    numTrue++;
                }
            }
            return numTrue + "/" + vals.length;
        }

        static String chars(final char... vals) {
            return new String(vals);
        }

        static String bytes(final byte... vals) {
            return Arrays.toString(vals);
        }

        static String shorts(final short... vals) {
            return Arrays.toString(vals);
        }

        static String floats(final float... vals) {
            return Arrays.toString(vals);
        }

        static String describe(final Object... vals) {
            return Arrays.deepToString(vals);
        }

        String instanceJoin(final String... parts) {
            return Arrays.toString(parts);
        }
    }

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testTrailingArgsArePackedIntoAnArray() throws Exception {
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        assertThat(Narcissus.invokeStaticMethod(join, "p")).isEqualTo("p[]");
        assertThat(Narcissus.invokeStaticMethod(join, "p", "a")).isEqualTo("p[a]");
        assertThat(Narcissus.invokeStaticMethod(join, "p", "a", "b", "c")).isEqualTo("p[a, b, c]");
    }

    @Test
    public void testPrePackedArrayIsPassedThrough() throws Exception {
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        // The array is passed through as-is, rather than being wrapped in another array
        assertThat(Narcissus.invokeStaticMethod(join, "p", (Object) new String[] { "a", "b" })).isEqualTo("p[a, b]");
        assertThat(Narcissus.invokeStaticMethod(join, "p", (Object) new String[0])).isEqualTo("p[]");
    }

    @Test
    public void testPrePackedPrimitiveArrayIsPassedThrough() throws Exception {
        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        assertThat(Narcissus.invokeStaticMethod(sum, (Object) new int[] { 1, 2, 3 })).isEqualTo(Integer.valueOf(6));
        assertThat(Narcissus.invokeStaticMethod(sum, (Object) new int[0])).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void testArrayOfWrongTypeIsNotPassedThrough() throws Exception {
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        // An Object[] is not a String[], so it is treated as a single trailing arg, and rejected because an
        // Object[] cannot be stored in a String[]
        try {
            Narcissus.invokeStaticMethod(join, "p", (Object) new Object[] { "a" });
            fail("join() should have rejected an Object[] as its varargs argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("incompatible type");
        }
        // A long[] is not an int[], and cannot be unboxed to an int either
        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        try {
            Narcissus.invokeStaticMethod(sum, (Object) new long[] { 1L });
            fail("sum() should have rejected a long[] as its varargs argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong type");
        }
    }

    @Test
    public void testSubtypeArrayIsPassedThrough() throws Exception {
        // A String[] is an Object[], so it is passed through rather than wrapped
        final Method describe = Narcissus.findMethod(Varargs.class, "describe", Object[].class);
        assertThat(Narcissus.invokeStaticMethod(describe, (Object) new String[] { "a", "b" })).isEqualTo("[a, b]");
        // A single non-array arg is wrapped
        assertThat(Narcissus.invokeStaticMethod(describe, "a")).isEqualTo("[a]");
        // Args of mixed type are wrapped in an Object[]
        assertThat(Narcissus.invokeStaticMethod(describe, "a", Integer.valueOf(1))).isEqualTo("[a, 1]");
    }

    @Test
    public void testNullTrailingArgIsWrappedInAnArray() throws Exception {
        // This matches the behavior of a varargs call site of the form join("p", (String) null)
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        assertThat(Narcissus.invokeStaticMethod(join, "p", (Object) null)).isEqualTo("p[null]");
        assertThat(Narcissus.invokeStaticMethod(join, "p", null, "a")).isEqualTo("p[null, a]");
    }

    @Test
    public void testNullTrailingArgIsRejectedForPrimitiveVarargs() throws Exception {
        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        try {
            Narcissus.invokeStaticMethod(sum, (Object) null);
            fail("sum() should have rejected a null varargs argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("null argument");
        }
    }

    @Test
    public void testVarargsArgOfIncompatibleTypeIsRejected() throws Exception {
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        try {
            Narcissus.invokeStaticMethod(join, "p", "a", Integer.valueOf(1));
            fail("join() should have rejected an Integer varargs argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("incompatible type");
        }
    }

    @Test
    public void testTooFewArgsForNonVarargsParameters() throws Exception {
        final Method join = Narcissus.findMethod(Varargs.class, "join", String.class, String[].class);
        try {
            Narcissus.invokeStaticMethod(join);
            fail("join() should have rejected a call with no arguments");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }
    }

    @Test
    public void testWideningConversionOfVarargsArgs() throws Exception {
        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        assertThat(Narcissus.invokeStaticMethod(sum, Byte.valueOf((byte) 1), Short.valueOf((short) 2),
                Character.valueOf((char) 3), Integer.valueOf(4))).isEqualTo(Integer.valueOf(10));

        final Method sumLongs = Narcissus.findMethod(Varargs.class, "sumLongs", long[].class);
        assertThat(Narcissus.invokeStaticMethod(sumLongs, Byte.valueOf((byte) 1), Integer.valueOf(2),
                Long.valueOf(3L))).isEqualTo(Long.valueOf(6L));

        final Method sumDoubles = Narcissus.findMethod(Varargs.class, "sumDoubles", double[].class);
        assertThat(Narcissus.invokeStaticMethod(sumDoubles, Integer.valueOf(1), Float.valueOf(0.5f),
                Double.valueOf(0.25))).isEqualTo(Double.valueOf(1.75));
    }

    @Test
    public void testVarargsOfEveryPrimitiveElementType() throws Exception {
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Varargs.class, "countBooleans",
                boolean[].class), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)).isEqualTo("2/3");
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Varargs.class, "chars", char[].class),
                Character.valueOf('a'), Character.valueOf('b'))).isEqualTo("ab");
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Varargs.class, "bytes", byte[].class),
                Byte.valueOf((byte) 1), Byte.valueOf((byte) 2))).isEqualTo("[1, 2]");
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Varargs.class, "shorts", short[].class),
                Byte.valueOf((byte) 1), Short.valueOf((short) 2))).isEqualTo("[1, 2]");
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Varargs.class, "floats", float[].class),
                Integer.valueOf(1), Float.valueOf(2.5f))).isEqualTo("[1.0, 2.5]");
    }

    @Test
    public void testVarargsArgOfWrongPrimitiveTypeIsRejected() throws Exception {
        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        try {
            Narcissus.invokeStaticMethod(sum, Integer.valueOf(1), Long.valueOf(2L));
            fail("sum() should have rejected a long varargs argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong type");
        }
    }

    @Test
    public void testInstanceVarargsMethod() throws Exception {
        final Varargs obj = new Varargs();
        final Method join = Narcissus.findMethod(Varargs.class, "instanceJoin", String[].class);
        assertThat(Narcissus.invokeMethod(obj, join, "a", "b")).isEqualTo("[a, b]");
        assertThat(Narcissus.invokeMethod(obj, join, (Object) new String[] { "c" })).isEqualTo("[c]");
        assertThat(Narcissus.invokeMethod(obj, join)).isEqualTo("[]");
    }

    @Test
    public void testManyVarargsArgs() throws Exception {
        // Each reference-typed arg needs a JNI local reference, and the default local reference capacity is 16
        final Method describe = Narcissus.findMethod(Varargs.class, "describe", Object[].class);
        final Object[] args = new Object[1000];
        for (int i = 0; i < args.length; i++) {
            args[i] = Integer.valueOf(i);
        }
        final String result = (String) Narcissus.invokeStaticMethod(describe, args);
        assertThat(result).startsWith("[0, 1, 2,").endsWith(", 999]");

        final Method sum = Narcissus.findMethod(Varargs.class, "sum", int[].class);
        assertThat(Narcissus.invokeStaticMethod(sum, args)).isEqualTo(Integer.valueOf(999 * 1000 / 2));
    }
}
