package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests the member lookup methods when the member does not exist, and tests the private native lookup methods that
 * {@link Narcissus#findClass(String)} and the class initializer are built on.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFindMemberTest {

    /** A class with a small, known set of members. */
    public static class Members {
        int intField;
        static String staticStringField = "x";

        int method(final int val) {
            return val;
        }

        static int staticMethod(final int val) {
            return val;
        }

        /** Constructor. */
        public Members() {
        }

        /**
         * Constructor.
         *
         * @param val
         *            an argument
         */
        public Members(final int val) {
        }
    }

    /** An interface, which has no constructors. */
    public interface AnInterface {
    }

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Invoke one of the private native lookup methods of {@link Narcissus}, using Narcissus itself.
     *
     * @param methodName
     *            the name of the native method
     * @param paramTypes
     *            the parameter types of the native method
     * @param args
     *            the arguments to pass
     * @return the result of the invocation
     * @throws NoSuchMethodException
     *             if the native method is not found
     */
    private static Object invokeInternal(final String methodName, final Class<?>[] paramTypes, final Object... args)
            throws NoSuchMethodException {
        final Method method = Narcissus.findMethod(Narcissus.class, methodName, paramTypes);
        return Narcissus.invokeStaticObjectMethod(method, args);
    }

    @Test
    public void testFindMethodWithUnknownName() {
        try {
            Narcissus.findMethod(Members.class, "noSuchMethod");
            fail("findMethod() should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            assertThat(e.getMessage()).contains("noSuchMethod");
        }
    }

    @Test
    public void testFindMethodWithWrongParameterTypes() {
        try {
            Narcissus.findMethod(Members.class, "method", String.class);
            fail("findMethod() should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            assertThat(e.getMessage()).contains("method");
        }
        try {
            Narcissus.findMethod(Members.class, "method");
            fail("findMethod() should have thrown NoSuchMethodException when given no parameter types");
        } catch (final NoSuchMethodException e) {
            // Expected
        }
    }

    @Test
    public void testFindFieldWithUnknownName() {
        try {
            Narcissus.findField(Members.class, "noSuchField");
            fail("findField() should have thrown NoSuchFieldException");
        } catch (final NoSuchFieldException e) {
            assertThat(e.getMessage()).contains("noSuchField");
        }
    }

    @Test
    public void testFindConstructorWithWrongParameterTypes() {
        try {
            Narcissus.findConstructor(Members.class, String.class);
            fail("findConstructor() should have thrown NoSuchMethodException");
        } catch (final NoSuchMethodException e) {
            assertThat(e.getMessage()).contains("Members");
        }
    }

    @Test
    public void testFindConstructorOfAnInterface() {
        try {
            Narcissus.findConstructor(AnInterface.class);
            fail("findConstructor() should have thrown NoSuchMethodException for an interface");
        } catch (final NoSuchMethodException e) {
            // Expected
        }
    }

    @Test
    public void testFindConstructorsThatDoExist() throws Exception {
        assertThat(Narcissus.findConstructor(Members.class).getParameterTypes()).isEmpty();
        assertThat(Narcissus.findConstructor(Members.class, int.class).getParameterTypes())
                .containsExactly(int.class);
    }

    @Test
    public void testFindMethodInternal() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        final Method found = (Method) invokeInternal("findMethodInternal", paramTypes, Members.class, "method",
                "(I)I", Boolean.FALSE);
        assertThat(found).isEqualTo(Narcissus.findMethod(Members.class, "method", int.class));

        final Method foundStatic = (Method) invokeInternal("findMethodInternal", paramTypes, Members.class,
                "staticMethod", "(I)I", Boolean.TRUE);
        assertThat(foundStatic).isEqualTo(Narcissus.findMethod(Members.class, "staticMethod", int.class));
    }

    @Test
    public void testFindMethodInternalWithUnknownMethod() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        try {
            invokeInternal("findMethodInternal", paramTypes, Members.class, "noSuchMethod", "()V", Boolean.FALSE);
            fail("findMethodInternal() should have thrown NoSuchMethodError");
        } catch (final NoSuchMethodError e) {
            // Expected -- the pending exception from GetMethodID() is thrown when the native method returns
        }
    }

    @Test
    public void testFindMethodInternalWithWrongStaticModifier() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        try {
            invokeInternal("findMethodInternal", paramTypes, Members.class, "method", "(I)I", Boolean.TRUE);
            fail("findMethodInternal() should have thrown NoSuchMethodError for a non-static method");
        } catch (final NoSuchMethodError e) {
            // Expected
        }
    }

    @Test
    public void testFindMethodInternalWithNullArgs() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        final Object[][] argsWithANull = { { null, "method", "(I)I", Boolean.FALSE },
                { Members.class, null, "(I)I", Boolean.FALSE }, { Members.class, "method", null, Boolean.FALSE } };
        for (int i = 0; i < argsWithANull.length; i++) {
            try {
                invokeInternal("findMethodInternal", paramTypes, argsWithANull[i]);
                fail("findMethodInternal() should have thrown NullPointerException for a null argument");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testFindFieldInternal() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        final Field found = (Field) invokeInternal("findFieldInternal", paramTypes, Members.class, "intField", "I",
                Boolean.FALSE);
        assertThat(found).isEqualTo(Narcissus.findField(Members.class, "intField"));

        final Field foundStatic = (Field) invokeInternal("findFieldInternal", paramTypes, Members.class,
                "staticStringField", "Ljava/lang/String;", Boolean.TRUE);
        assertThat(foundStatic).isEqualTo(Narcissus.findField(Members.class, "staticStringField"));
    }

    @Test
    public void testFindFieldInternalWithUnknownFieldOrWrongSignature() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        try {
            invokeInternal("findFieldInternal", paramTypes, Members.class, "noSuchField", "I", Boolean.FALSE);
            fail("findFieldInternal() should have thrown NoSuchFieldError");
        } catch (final NoSuchFieldError e) {
            // Expected
        }
        try {
            // The field exists, but its type is int, not long
            invokeInternal("findFieldInternal", paramTypes, Members.class, "intField", "J", Boolean.FALSE);
            fail("findFieldInternal() should have thrown NoSuchFieldError for a wrong signature");
        } catch (final NoSuchFieldError e) {
            // Expected
        }
    }

    @Test
    public void testFindFieldInternalWithNullArgs() throws Exception {
        final Class<?>[] paramTypes = { Class.class, String.class, String.class, boolean.class };
        final Object[][] argsWithANull = { { null, "intField", "I", Boolean.FALSE },
                { Members.class, null, "I", Boolean.FALSE }, { Members.class, "intField", null, Boolean.FALSE } };
        for (int i = 0; i < argsWithANull.length; i++) {
            try {
                invokeInternal("findFieldInternal", paramTypes, argsWithANull[i]);
                fail("findFieldInternal() should have thrown NullPointerException for a null argument");
            } catch (final NullPointerException e) {
                assertThat(e.getMessage()).contains("null");
            }
        }
    }

    @Test
    public void testFindClassInternalWithNullName() throws Exception {
        final Class<?>[] paramTypes = { String.class };
        try {
            invokeInternal("findClassInternal", paramTypes, new Object[] { null });
            fail("findClassInternal() should have thrown NullPointerException");
        } catch (final NullPointerException e) {
            assertThat(e.getMessage()).contains("null");
        }
    }

    @Test
    public void testGetDeclaredMembersOfArrayAndPrimitiveClasses() {
        // An array class and a primitive class declare no members of their own
        assertThat(Narcissus.getDeclaredFields(int[].class)).isEmpty();
        assertThat(Narcissus.getDeclaredMethods(int[].class)).isEmpty();
        assertThat(Narcissus.getDeclaredConstructors(int[].class)).isEmpty();
        assertThat(Narcissus.getDeclaredFields(int.class)).isEmpty();
        assertThat(Narcissus.getDeclaredMethods(int.class)).isEmpty();
        assertThat(Narcissus.getDeclaredConstructors(int.class)).isEmpty();
    }
}
