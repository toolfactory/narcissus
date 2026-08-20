package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that the typed method invokers reject a method whose return type does not match the invoker's return type,
 * reject a method with the wrong static modifier, and reject a receiver that is not an instance of the class that
 * declares the method.
 *
 * <p>
 * Without these checks, the JNI {@code Call<Type>MethodA} function reads the return value of a method as if it had
 * the invoker's return type, which reads whatever is left in the return register or on the stack.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusMethodReturnTypeTest {

    /** A class with one instance method and one static method returning every possible return type. */
    public static class Returns {
        void retVoid() {
        }

        int retInt() {
            return 1;
        }

        long retLong() {
            return 2L;
        }

        short retShort() {
            return 3;
        }

        char retChar() {
            return 'a';
        }

        boolean retBoolean() {
            return true;
        }

        byte retByte() {
            return 4;
        }

        float retFloat() {
            return 5.0f;
        }

        double retDouble() {
            return 6.0;
        }

        Object retObject() {
            return "seven";
        }

        static void staticRetVoid() {
        }

        static int staticRetInt() {
            return 1;
        }

        static long staticRetLong() {
            return 2L;
        }

        static short staticRetShort() {
            return 3;
        }

        static char staticRetChar() {
            return 'a';
        }

        static boolean staticRetBoolean() {
            return true;
        }

        static byte staticRetByte() {
            return 4;
        }

        static float staticRetFloat() {
            return 5.0f;
        }

        static double staticRetDouble() {
            return 6.0;
        }

        static Object staticRetObject() {
            return "seven";
        }
    }

    /** A class that declares none of the methods of {@link Returns}, for use as a mismatched receiver. */
    public static class Unrelated {
    }

    /** A subclass of {@link Returns}, which is a valid receiver for the methods of {@link Returns}. */
    public static class ReturnsSubclass extends Returns {
    }

    /** The names of the invoker types, in the order they are indexed by the loops below. */
    private static final String[] KINDS = { "void", "int", "long", "short", "char", "boolean", "byte", "float",
            "double", "object" };

    /** The names of the instance methods of {@link Returns}, in the same order as {@link #KINDS}. */
    private static final String[] INSTANCE_METHOD_NAMES = { "retVoid", "retInt", "retLong", "retShort", "retChar",
            "retBoolean", "retByte", "retFloat", "retDouble", "retObject" };

    /** The names of the static methods of {@link Returns}, in the same order as {@link #KINDS}. */
    private static final String[] STATIC_METHOD_NAMES = { "staticRetVoid", "staticRetInt", "staticRetLong",
            "staticRetShort", "staticRetChar", "staticRetBoolean", "staticRetByte", "staticRetFloat",
            "staticRetDouble", "staticRetObject" };

    /** The values the instance methods of {@link Returns} return, in the same order as {@link #KINDS}. */
    private static final Object[] RETURN_VALUES = { null, Integer.valueOf(1), Long.valueOf(2L),
            Short.valueOf((short) 3), Character.valueOf('a'), Boolean.TRUE, Byte.valueOf((byte) 4),
            Float.valueOf(5.0f), Double.valueOf(6.0), "seven" };

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Invoke an instance method using the invoker of the given kind, and return the result boxed (or null for the
     * void invoker).
     *
     * @param kind
     *            the index into {@link #KINDS} of the invoker to use
     * @param obj
     *            the object to invoke the method on
     * @param method
     *            the method to invoke
     * @return the boxed return value, or null if the void invoker was used
     */
    private static Object invoke(final int kind, final Object obj, final Method method) {
        switch (kind) {
        case 0:
            Narcissus.invokeVoidMethod(obj, method);
            return null;
        case 1:
            return Integer.valueOf(Narcissus.invokeIntMethod(obj, method));
        case 2:
            return Long.valueOf(Narcissus.invokeLongMethod(obj, method));
        case 3:
            return Short.valueOf(Narcissus.invokeShortMethod(obj, method));
        case 4:
            return Character.valueOf(Narcissus.invokeCharMethod(obj, method));
        case 5:
            return Boolean.valueOf(Narcissus.invokeBooleanMethod(obj, method));
        case 6:
            return Byte.valueOf(Narcissus.invokeByteMethod(obj, method));
        case 7:
            return Float.valueOf(Narcissus.invokeFloatMethod(obj, method));
        case 8:
            return Double.valueOf(Narcissus.invokeDoubleMethod(obj, method));
        default:
            return Narcissus.invokeObjectMethod(obj, method);
        }
    }

    /**
     * Invoke a static method using the invoker of the given kind, and return the result boxed (or null for the void
     * invoker).
     *
     * @param kind
     *            the index into {@link #KINDS} of the invoker to use
     * @param method
     *            the method to invoke
     * @return the boxed return value, or null if the void invoker was used
     */
    private static Object invokeStatic(final int kind, final Method method) {
        switch (kind) {
        case 0:
            Narcissus.invokeStaticVoidMethod(method);
            return null;
        case 1:
            return Integer.valueOf(Narcissus.invokeStaticIntMethod(method));
        case 2:
            return Long.valueOf(Narcissus.invokeStaticLongMethod(method));
        case 3:
            return Short.valueOf(Narcissus.invokeStaticShortMethod(method));
        case 4:
            return Character.valueOf(Narcissus.invokeStaticCharMethod(method));
        case 5:
            return Boolean.valueOf(Narcissus.invokeStaticBooleanMethod(method));
        case 6:
            return Byte.valueOf(Narcissus.invokeStaticByteMethod(method));
        case 7:
            return Float.valueOf(Narcissus.invokeStaticFloatMethod(method));
        case 8:
            return Double.valueOf(Narcissus.invokeStaticDoubleMethod(method));
        default:
            return Narcissus.invokeStaticObjectMethod(method);
        }
    }

    @Test
    public void testInstanceInvokerRequiresMatchingReturnType() throws Exception {
        final Returns obj = new Returns();
        for (int methodKind = 0; methodKind < KINDS.length; methodKind++) {
            final Method method = Narcissus.findMethod(Returns.class, INSTANCE_METHOD_NAMES[methodKind]);
            for (int invokerKind = 0; invokerKind < KINDS.length; invokerKind++) {
                if (invokerKind == methodKind) {
                    assertThat(invoke(invokerKind, obj, method)).isEqualTo(RETURN_VALUES[methodKind]);
                } else {
                    try {
                        invoke(invokerKind, obj, method);
                        fail("invoke" + KINDS[invokerKind] + "Method() should have rejected a method returning "
                                + KINDS[methodKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testStaticInvokerRequiresMatchingReturnType() throws Exception {
        for (int methodKind = 0; methodKind < KINDS.length; methodKind++) {
            final Method method = Narcissus.findMethod(Returns.class, STATIC_METHOD_NAMES[methodKind]);
            for (int invokerKind = 0; invokerKind < KINDS.length; invokerKind++) {
                if (invokerKind == methodKind) {
                    assertThat(invokeStatic(invokerKind, method)).isEqualTo(RETURN_VALUES[methodKind]);
                } else {
                    try {
                        invokeStatic(invokerKind, method);
                        fail("invokeStatic" + KINDS[invokerKind]
                                + "Method() should have rejected a method returning " + KINDS[methodKind]);
                    } catch (final IllegalArgumentException e) {
                        // Expected
                    }
                }
            }
        }
    }

    @Test
    public void testInstanceInvokersRejectStaticMethod() throws Exception {
        final Returns obj = new Returns();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Method method = Narcissus.findMethod(Returns.class, STATIC_METHOD_NAMES[kind]);
            try {
                invoke(kind, obj, method);
                fail("invoke" + KINDS[kind] + "Method() should have rejected a static method");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testStaticInvokersRejectInstanceMethod() throws Exception {
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Method method = Narcissus.findMethod(Returns.class, INSTANCE_METHOD_NAMES[kind]);
            try {
                invokeStatic(kind, method);
                fail("invokeStatic" + KINDS[kind] + "Method() should have rejected a non-static method");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testInstanceInvokersRejectUnrelatedReceiver() throws Exception {
        final Unrelated obj = new Unrelated();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Method method = Narcissus.findMethod(Returns.class, INSTANCE_METHOD_NAMES[kind]);
            try {
                invoke(kind, obj, method);
                fail("invoke" + KINDS[kind] + "Method() should have rejected an unrelated receiver");
            } catch (final IllegalArgumentException e) {
                // Expected
            }
        }
    }

    @Test
    public void testInstanceInvokersAcceptSubclassReceiver() throws Exception {
        final ReturnsSubclass obj = new ReturnsSubclass();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Method method = Narcissus.findMethod(Returns.class, INSTANCE_METHOD_NAMES[kind]);
            assertThat(invoke(kind, obj, method)).isEqualTo(RETURN_VALUES[kind]);
        }
    }

    @Test
    public void testInvokeMethodBoxesEveryReturnType() throws Exception {
        final Returns obj = new Returns();
        for (int kind = 0; kind < KINDS.length; kind++) {
            final Method instanceMethod = Narcissus.findMethod(Returns.class, INSTANCE_METHOD_NAMES[kind]);
            assertThat(Narcissus.invokeMethod(obj, instanceMethod)).isEqualTo(RETURN_VALUES[kind]);
            final Method staticMethod = Narcissus.findMethod(Returns.class, STATIC_METHOD_NAMES[kind]);
            assertThat(Narcissus.invokeStaticMethod(staticMethod)).isEqualTo(RETURN_VALUES[kind]);
        }
    }

    @Test
    public void testInvokeMethodRejectsStaticMethod() throws Exception {
        final Returns obj = new Returns();
        final Method method = Narcissus.findMethod(Returns.class, "staticRetInt");
        try {
            Narcissus.invokeMethod(obj, method);
            fail("invokeMethod() should have rejected a static method");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("invokeStaticMethod");
        }
    }

    @Test
    public void testInvokeStaticMethodRejectsInstanceMethod() throws Exception {
        final Method method = Narcissus.findMethod(Returns.class, "retInt");
        try {
            Narcissus.invokeStaticMethod(method);
            fail("invokeStaticMethod() should have rejected a non-static method");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("invokeMethod");
        }
    }
}
