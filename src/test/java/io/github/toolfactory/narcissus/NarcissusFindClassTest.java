package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Tests {@link Narcissus#findClass(String)} for every form of class name it accepts. */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFindClassTest {

    /** A nested class, to test lookup of a class whose binary name contains a '$'. */
    public static class Nested {
    }

    /** The names of the primitive types, and void. */
    private static final String[] PRIMITIVE_NAMES = { "boolean", "byte", "char", "short", "int", "long", "float",
            "double", "void" };

    /** The primitive type classes, in the same order as {@link #PRIMITIVE_NAMES}. */
    private static final Class<?>[] PRIMITIVE_CLASSES = { boolean.class, byte.class, char.class, short.class,
            int.class, long.class, float.class, double.class, void.class };

    /** One-dimensional array classes of each primitive type, in the same order as {@link #PRIMITIVE_NAMES}. */
    private static final Class<?>[] PRIMITIVE_ARRAY_CLASSES = { boolean[].class, byte[].class, char[].class,
            short[].class, int[].class, long[].class, float[].class, double[].class, null };

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testFindPrimitiveClasses() {
        for (int i = 0; i < PRIMITIVE_NAMES.length; i++) {
            assertThat(Narcissus.findClass(PRIMITIVE_NAMES[i])).isSameAs(PRIMITIVE_CLASSES[i]);
        }
    }

    @Test
    public void testFindPrimitiveArrayClasses() {
        for (int i = 0; i < PRIMITIVE_NAMES.length; i++) {
            if (PRIMITIVE_ARRAY_CLASSES[i] == null) {
                // There is no void[] type
                continue;
            }
            assertThat(Narcissus.findClass(PRIMITIVE_NAMES[i] + "[]")).isSameAs(PRIMITIVE_ARRAY_CLASSES[i]);
        }
        assertThat(Narcissus.findClass("int[][]")).isSameAs(int[][].class);
        assertThat(Narcissus.findClass("int[][][]")).isSameAs(int[][][].class);
    }

    @Test
    public void testFindReferenceClasses() {
        assertThat(Narcissus.findClass("java.lang.String")).isSameAs(String.class);
        assertThat(Narcissus.findClass("java.util.Map")).isSameAs(Map.class);
        assertThat(Narcissus.findClass(NarcissusFindClassTest.class.getName()))
                .isSameAs(NarcissusFindClassTest.class);
    }

    @Test
    public void testFindNestedClass() {
        // The binary name of a nested class contains a '$'
        assertThat(Narcissus.findClass(Nested.class.getName())).isSameAs(Nested.class);
        assertThat(Narcissus.findClass("java.util.Map$Entry")).isSameAs(Map.Entry.class);
    }

    @Test
    public void testFindReferenceArrayClasses() {
        assertThat(Narcissus.findClass("java.lang.String[]")).isSameAs(String[].class);
        assertThat(Narcissus.findClass("java.lang.String[][]")).isSameAs(String[][].class);
        assertThat(Narcissus.findClass(Nested.class.getName() + "[]")).isSameAs(Nested[].class);
    }

    @Test
    public void testFindClassAcceptsInternalNameForm() {
        // Class names are converted to internal form, so both forms work
        assertThat(Narcissus.findClass("java/lang/String")).isSameAs(String.class);
        assertThat(Narcissus.findClass("java/lang/String[]")).isSameAs(String[].class);
    }

    @Test
    public void testFindClassRejectsNull() {
        try {
            Narcissus.findClass(null);
            fail("findClass(null) should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("null");
        }
    }

    @Test
    public void testFindNonexistentClass() {
        try {
            Narcissus.findClass("no.such.Class");
            fail("findClass() should have thrown NoClassDefFoundError for a nonexistent class");
        } catch (final NoClassDefFoundError e) {
            assertThat(e.getMessage()).contains("no/such/Class");
        }
    }

    @Test
    public void testFindNonexistentArrayClass() {
        try {
            Narcissus.findClass("no.such.Class[]");
            fail("findClass() should have thrown NoClassDefFoundError for a nonexistent array class");
        } catch (final NoClassDefFoundError e) {
            assertThat(e.getMessage()).contains("no/such/Class");
        }
    }

    @Test
    public void testFindClassWithEmptyName() {
        try {
            Narcissus.findClass("");
            fail("findClass(\"\") should have thrown an error");
        } catch (final NoClassDefFoundError e) {
            // Expected
        }
    }
}
