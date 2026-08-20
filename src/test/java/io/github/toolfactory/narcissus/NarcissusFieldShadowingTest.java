package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that when a field of a subclass shadows a field of the same name in a superclass, the field of the
 * subclass is the one found, both by {@link Narcissus#findField(Class, String)} and by
 * {@link ReflectionCache#getField(String)}.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusFieldShadowingTest {

    /** A superclass with a field that a subclass shadows. */
    public static class Base {
        String shadowed = "base";
        String notShadowed = "base";
        static String staticShadowed = "base";
    }

    /** A subclass that shadows two of the fields of {@link Base}. */
    public static class Sub extends Base {
        String shadowed = "sub";
        static String staticShadowed = "sub";
    }

    /** A subclass of {@link Sub}, so that a field is shadowed twice over. */
    public static class SubSub extends Sub {
        String shadowed = "subsub";
    }

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testFindFieldReturnsTheShadowingField() throws Exception {
        assertThat(Narcissus.findField(Sub.class, "shadowed").getDeclaringClass()).isSameAs(Sub.class);
        assertThat(Narcissus.findField(SubSub.class, "shadowed").getDeclaringClass()).isSameAs(SubSub.class);
        assertThat(Narcissus.findField(Base.class, "shadowed").getDeclaringClass()).isSameAs(Base.class);
        assertThat(Narcissus.findField(Sub.class, "notShadowed").getDeclaringClass()).isSameAs(Base.class);
    }

    @Test
    public void testReflectionCacheReturnsTheShadowingField() {
        assertThat(new ReflectionCache(Sub.class).getField("shadowed").getDeclaringClass()).isSameAs(Sub.class);
        assertThat(new ReflectionCache(SubSub.class).getField("shadowed").getDeclaringClass())
                .isSameAs(SubSub.class);
        assertThat(new ReflectionCache(Base.class).getField("shadowed").getDeclaringClass()).isSameAs(Base.class);
        assertThat(new ReflectionCache(Sub.class).getField("notShadowed").getDeclaringClass()).isSameAs(Base.class);
        assertThat(new ReflectionCache(Sub.class).getField("staticShadowed").getDeclaringClass())
                .isSameAs(Sub.class);
    }

    @Test
    public void testReflectionCacheAndFindFieldAgree() throws Exception {
        final ReflectionCache cache = new ReflectionCache(SubSub.class);
        final String[] fieldNames = { "shadowed", "notShadowed", "staticShadowed" };
        for (int i = 0; i < fieldNames.length; i++) {
            assertThat(cache.getField(fieldNames[i])).isEqualTo(Narcissus.findField(SubSub.class, fieldNames[i]));
        }
    }

    @Test
    public void testTheShadowingAndShadowedFieldsAreDistinctStorage() throws Exception {
        final SubSub obj = new SubSub();
        final Field subSubField = Narcissus.findField(SubSub.class, "shadowed");
        final Field subField = Narcissus.findField(Sub.class, "shadowed");
        final Field baseField = Narcissus.findField(Base.class, "shadowed");
        assertThat(Narcissus.getField(obj, subSubField)).isEqualTo("subsub");
        assertThat(Narcissus.getField(obj, subField)).isEqualTo("sub");
        assertThat(Narcissus.getField(obj, baseField)).isEqualTo("base");

        Narcissus.setField(obj, baseField, "changed");
        assertThat(Narcissus.getField(obj, baseField)).isEqualTo("changed");
        assertThat(Narcissus.getField(obj, subField)).isEqualTo("sub");
        assertThat(Narcissus.getField(obj, subSubField)).isEqualTo("subsub");
    }

    @Test
    public void testEnumerateFieldsListsSubclassFieldsFirst() {
        final List<Field> fields = Narcissus.enumerateFields(SubSub.class);
        int subSubIdx = -1;
        int subIdx = -1;
        int baseIdx = -1;
        for (int i = 0; i < fields.size(); i++) {
            final Field field = fields.get(i);
            if (field.getName().equals("shadowed")) {
                if (field.getDeclaringClass() == SubSub.class) {
                    subSubIdx = i;
                } else if (field.getDeclaringClass() == Sub.class) {
                    subIdx = i;
                } else if (field.getDeclaringClass() == Base.class) {
                    baseIdx = i;
                }
            }
        }
        // All three fields named "shadowed" are listed, subclass first
        assertThat(subSubIdx).isGreaterThanOrEqualTo(0);
        assertThat(subIdx).isGreaterThan(subSubIdx);
        assertThat(baseIdx).isGreaterThan(subIdx);
    }
}
