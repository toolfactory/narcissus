package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that an exception thrown by an invoked method propagates to the caller unchanged, rather than being
 * wrapped in an {@link java.lang.reflect.InvocationTargetException}, and tests access to members that reflection
 * would normally refuse: private members, and final fields.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusExceptionAndAccessTest {

    /** An exception type declared by this test, so it cannot be thrown by anything else. */
    @SuppressWarnings("serial")
    public static class TestException extends Exception {
        /**
         * Constructor.
         *
         * @param message
         *            the message
         */
        public TestException(final String message) {
            super(message);
        }
    }

    /** An error type declared by this test, so it cannot be thrown by anything else. */
    @SuppressWarnings("serial")
    public static class TestError extends Error {
        /**
         * Constructor.
         *
         * @param message
         *            the message
         */
        public TestError(final String message) {
            super(message);
        }
    }

    /** A class whose methods throw, and whose members are private or final. */
    public static class Thrower {
        private final int privateFinalIntField = 1;
        private String privateField = "private";
        final String finalField = "final";
        static final String STATIC_FINAL_FIELD;
        private static String privateStaticField = "privateStatic";

        static {
            // Assigned in a static initializer, so it is not a compile-time constant, and reads of it are
            // therefore not inlined by the compiler
            STATIC_FINAL_FIELD = "staticFinal".toUpperCase();
        }

        void throwRuntimeException() {
            throw new IllegalStateException("runtime");
        }

        int throwFromIntMethod() {
            throw new IllegalStateException("int");
        }

        void throwCheckedException() throws TestException {
            throw new TestException("checked");
        }

        void throwError() {
            throw new TestError("error");
        }

        static void throwFromStaticMethod() throws IOException {
            throw new IOException("static");
        }

        private String privateMethod() {
            return "privateMethod";
        }

        private static String privateStaticMethod() {
            return "privateStaticMethod";
        }
    }

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testRuntimeExceptionPropagatesUnwrapped() throws Exception {
        final Thrower obj = new Thrower();
        final Method method = Narcissus.findMethod(Thrower.class, "throwRuntimeException");
        try {
            Narcissus.invokeVoidMethod(obj, method);
            fail("throwRuntimeException() should have thrown");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("runtime");
        }
    }

    @Test
    public void testCheckedExceptionPropagatesUnwrapped() throws Exception {
        final Thrower obj = new Thrower();
        final Method method = Narcissus.findMethod(Thrower.class, "throwCheckedException");
        try {
            Narcissus.invokeVoidMethod(obj, method);
            fail("throwCheckedException() should have thrown");
        } catch (final Throwable t) {
            // A checked exception is thrown without being declared, and without being wrapped
            assertThat(t).isInstanceOf(TestException.class);
            assertThat(t.getMessage()).isEqualTo("checked");
        }
    }

    @Test
    public void testErrorPropagatesUnwrapped() throws Exception {
        final Thrower obj = new Thrower();
        final Method method = Narcissus.findMethod(Thrower.class, "throwError");
        try {
            Narcissus.invokeVoidMethod(obj, method);
            fail("throwError() should have thrown");
        } catch (final TestError e) {
            assertThat(e.getMessage()).isEqualTo("error");
        }
    }

    @Test
    public void testExceptionFromATypedInvokerPropagates() throws Exception {
        final Thrower obj = new Thrower();
        final Method method = Narcissus.findMethod(Thrower.class, "throwFromIntMethod");
        try {
            Narcissus.invokeIntMethod(obj, method);
            fail("throwFromIntMethod() should have thrown");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("int");
        }
        // The exception also propagates through the boxing wrapper
        try {
            Narcissus.invokeMethod(obj, method);
            fail("throwFromIntMethod() should have thrown");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("int");
        }
    }

    @Test
    public void testExceptionFromAStaticMethodPropagates() throws Exception {
        final Method method = Narcissus.findMethod(Thrower.class, "throwFromStaticMethod");
        try {
            Narcissus.invokeStaticVoidMethod(method);
            fail("throwFromStaticMethod() should have thrown");
        } catch (final Throwable t) {
            assertThat(t).isInstanceOf(IOException.class);
            assertThat(t.getMessage()).isEqualTo("static");
        }
    }

    @Test
    public void testInvokePrivateMethods() throws Exception {
        final Thrower obj = new Thrower();
        assertThat(Narcissus.invokeMethod(obj, Narcissus.findMethod(Thrower.class, "privateMethod")))
                .isEqualTo("privateMethod");
        assertThat(Narcissus.invokeStaticMethod(Narcissus.findMethod(Thrower.class, "privateStaticMethod")))
                .isEqualTo("privateStaticMethod");
    }

    @Test
    public void testReadAndWritePrivateFields() throws Exception {
        final Thrower obj = new Thrower();
        final Field privateField = Narcissus.findField(Thrower.class, "privateField");
        assertThat(Narcissus.getField(obj, privateField)).isEqualTo("private");
        Narcissus.setField(obj, privateField, "changed");
        assertThat(Narcissus.getField(obj, privateField)).isEqualTo("changed");

        final Field privateStaticField = Narcissus.findField(Thrower.class, "privateStaticField");
        assertThat(Narcissus.getStaticField(privateStaticField)).isEqualTo("privateStatic");
        Narcissus.setStaticField(privateStaticField, "changedStatic");
        assertThat(Narcissus.getStaticField(privateStaticField)).isEqualTo("changedStatic");
    }

    @Test
    public void testWriteFinalFields() throws Exception {
        final Thrower obj = new Thrower();
        final Field finalField = Narcissus.findField(Thrower.class, "finalField");
        Narcissus.setField(obj, finalField, "changed");
        assertThat(Narcissus.getField(obj, finalField)).isEqualTo("changed");

        final Field privateFinalIntField = Narcissus.findField(Thrower.class, "privateFinalIntField");
        Narcissus.setIntField(obj, privateFinalIntField, 42);
        assertThat(Narcissus.getIntField(obj, privateFinalIntField)).isEqualTo(42);
    }

    @Test
    public void testWriteStaticFinalField() throws Exception {
        final Field field = Narcissus.findField(Thrower.class, "STATIC_FINAL_FIELD");
        final Object originalValue = Narcissus.getStaticField(field);
        assertThat(originalValue).isEqualTo("STATICFINAL");
        try {
            Narcissus.setStaticField(field, "changed");
            assertThat(Narcissus.getStaticField(field)).isEqualTo("changed");
        } finally {
            Narcissus.setStaticField(field, originalValue);
        }
    }
}
