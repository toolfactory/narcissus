package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive tests for Narcissus library API covering all public methods,
 * field types, method invocation, and error handling to prevent JVM crashes.
 */
public class NarcissusComprehensiveTest {

    @Before
    public void testInitialized() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testLibraryLoadedField() {
        assertThat(Narcissus.libraryLoaded).isTrue();
    }

    // ==== CLASS OPERATIONS TESTS ====

    @Test
    public void testFindClass() {
        Class<?> stringClass = Narcissus.findClass("java.lang.String");
        assertThat(stringClass).isEqualTo(String.class);

        Class<?> arrayClass = Narcissus.findClass("java.lang.String[]");
        assertThat(arrayClass).isEqualTo(String[].class);
    }

    @Test
    public void testFindClassWithNull() {
        try {
            Narcissus.findClass(null);
            assertThat(false).isTrue(); // Should not reach here
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Class name cannot be null");
        }
    }

    @Test
    public void testFindMethod() throws NoSuchMethodException {
        Method toStringMethod = Narcissus.findMethod(TestClass.class, "toString");
        assertThat(toStringMethod).isNotNull();

        Method testMethod = Narcissus.findMethod(TestClass.class, "testMethod", String.class, int.class);
        assertThat(testMethod).isNotNull();
        assertThat(testMethod.getParameterTypes()).containsExactly(String.class, int.class);
    }

    @Test
    public void testFindConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = Narcissus.findConstructor(TestClass.class, String.class);
        assertThat(constructor).isNotNull();
        assertThat(constructor.getParameterTypes()).containsExactly(String.class);
    }

    @Test
    public void testFindField() throws NoSuchFieldException {
        Field field = Narcissus.findField(TestClass.class, "publicField");
        assertThat(field).isNotNull();
        assertThat(field.getType()).isEqualTo(String.class);

        Field privateField = Narcissus.findField(TestClass.class, "privateField");
        assertThat(privateField).isNotNull();
        assertThat(privateField.getType()).isEqualTo(int.class);
    }

    @Test
    public void testEnumerateMethods() {
        List<Method> methods = Narcissus.enumerateMethods(TestClass.class);
        assertThat(methods).isNotEmpty();

        boolean foundTestMethod = false;
        for (Method method : methods) {
            if ("testMethod".equals(method.getName())) {
                foundTestMethod = true;
                break;
            }
        }
        assertThat(foundTestMethod).isTrue();
    }

    @Test
    public void testEnumerateFields() {
        List<Field> fields = Narcissus.enumerateFields(TestClass.class);
        assertThat(fields).isNotEmpty();

        boolean foundPublicField = false;
        boolean foundPrivateField = false;
        for (Field field : fields) {
            if ("publicField".equals(field.getName())) {
                foundPublicField = true;
            } else if ("privateField".equals(field.getName())) {
                foundPrivateField = true;
            }
        }
        assertThat(foundPublicField).isTrue();
        assertThat(foundPrivateField).isTrue();
    }

    // ==== FIELD ACCESS TESTS ====

    @Test
    public void testAllPrimitiveFieldTypes() throws NoSuchFieldException {
        TestFieldsClass obj = new TestFieldsClass();

        // Test int field
        Field intField = Narcissus.findField(TestFieldsClass.class, "intField");
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(42);
        Narcissus.setIntField(obj, intField, 100);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(100);

        // Test long field
        Field longField = Narcissus.findField(TestFieldsClass.class, "longField");
        assertThat(Narcissus.getLongField(obj, longField)).isEqualTo(999L);
        Narcissus.setLongField(obj, longField, 2000L);
        assertThat(Narcissus.getLongField(obj, longField)).isEqualTo(2000L);

        // Test boolean field
        Field booleanField = Narcissus.findField(TestFieldsClass.class, "booleanField");
        assertThat(Narcissus.getBooleanField(obj, booleanField)).isTrue();
        Narcissus.setBooleanField(obj, booleanField, false);
        assertThat(Narcissus.getBooleanField(obj, booleanField)).isFalse();

        // Test char field
        Field charField = Narcissus.findField(TestFieldsClass.class, "charField");
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo('A');
        Narcissus.setCharField(obj, charField, 'X');
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo('X');

        // Test byte field
        Field byteField = Narcissus.findField(TestFieldsClass.class, "byteField");
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo((byte) 127);
        Narcissus.setByteField(obj, byteField, (byte) 64);
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo((byte) 64);

        // Test short field
        Field shortField = Narcissus.findField(TestFieldsClass.class, "shortField");
        assertThat(Narcissus.getShortField(obj, shortField)).isEqualTo((short) 123);
        Narcissus.setShortField(obj, shortField, (short) 456);
        assertThat(Narcissus.getShortField(obj, shortField)).isEqualTo((short) 456);

        // Test float field
        Field floatField = Narcissus.findField(TestFieldsClass.class, "floatField");
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(3.14f);
        Narcissus.setFloatField(obj, floatField, 2.71f);
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(2.71f);

        // Test double field
        Field doubleField = Narcissus.findField(TestFieldsClass.class, "doubleField");
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(2.718);
        Narcissus.setDoubleField(obj, doubleField, 1.414);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(1.414);

        // Test Object field
        Field objectField = Narcissus.findField(TestFieldsClass.class, "objectField");
        assertThat(Narcissus.getObjectField(obj, objectField)).isEqualTo("test");
        Narcissus.setObjectField(obj, objectField, "changed");
        assertThat(Narcissus.getObjectField(obj, objectField)).isEqualTo("changed");
    }

    @Test
    public void testGenericFieldMethods() throws NoSuchFieldException {
        TestFieldsClass obj = new TestFieldsClass();

        // Test generic getField and setField methods with boxing/unboxing
        Field intField = Narcissus.findField(TestFieldsClass.class, "intField");
        Object boxedValue = Narcissus.getField(obj, intField);
        assertThat(boxedValue).isEqualTo(42);
        assertThat(boxedValue).isInstanceOf(Integer.class);

        Narcissus.setField(obj, intField, 200);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(200);
    }

    @Test
    public void testStaticFields() throws NoSuchFieldException {
        Field staticField = Narcissus.findField(TestFieldsClass.class, "staticIntField");

        int originalValue = Narcissus.getStaticIntField(staticField);
        assertThat(originalValue).isEqualTo(999);

        try {
            Narcissus.setStaticIntField(staticField, 1111);
            assertThat(Narcissus.getStaticIntField(staticField)).isEqualTo(1111);

            Object boxedValue = Narcissus.getStaticField(staticField);
            assertThat(boxedValue).isEqualTo(1111);
            assertThat(boxedValue).isInstanceOf(Integer.class);
        } finally {
            // Restore original value
            Narcissus.setStaticIntField(staticField, originalValue);
        }
    }

    // ==== METHOD INVOCATION TESTS ====

    @Test
    public void testAllPrimitiveMethodReturnTypes() throws NoSuchMethodException {
        TestMethodsClass obj = new TestMethodsClass();

        // Test int method
        Method intMethod = Narcissus.findMethod(TestMethodsClass.class, "intMethod");
        assertThat(Narcissus.invokeIntMethod(obj, intMethod)).isEqualTo(42);
        Object boxedResult = Narcissus.invokeMethod(obj, intMethod);
        assertThat(boxedResult).isEqualTo(42);
        assertThat(boxedResult).isInstanceOf(Integer.class);

        // Test long method
        Method longMethod = Narcissus.findMethod(TestMethodsClass.class, "longMethod");
        assertThat(Narcissus.invokeLongMethod(obj, longMethod)).isEqualTo(999L);

        // Test boolean method
        Method booleanMethod = Narcissus.findMethod(TestMethodsClass.class, "booleanMethod");
        assertThat(Narcissus.invokeBooleanMethod(obj, booleanMethod)).isTrue();

        // Test void method
        Method voidMethod = Narcissus.findMethod(TestMethodsClass.class, "voidMethod");
        Narcissus.invokeVoidMethod(obj, voidMethod);
        assertThat(obj.voidMethodCalled).isTrue();

        // Test Object method
        Method objectMethod = Narcissus.findMethod(TestMethodsClass.class, "objectMethod");
        Object result = Narcissus.invokeObjectMethod(obj, objectMethod);
        assertThat(result).isEqualTo("test string");
    }

    @Test
    public void testStaticMethodInvocation() throws NoSuchMethodException {
        Method staticMethod = Narcissus.findMethod(TestMethodsClass.class, "staticMethod");
        TestMethodsClass.staticMethodCalled = false;

        Narcissus.invokeStaticVoidMethod(staticMethod);
        assertThat(TestMethodsClass.staticMethodCalled).isTrue();

        TestMethodsClass.staticMethodCalled = false;
        Object result = Narcissus.invokeStaticMethod(staticMethod);
        assertThat(result).isNull();
        assertThat(TestMethodsClass.staticMethodCalled).isTrue();
    }

    @Test
    public void testMethodWithParameters() throws NoSuchMethodException {
        TestMethodsClass obj = new TestMethodsClass();
        Method methodWithParams = Narcissus.findMethod(TestMethodsClass.class, "methodWithParams", String.class, int.class);

        String result = (String) Narcissus.invokeObjectMethod(obj, methodWithParams, "hello", 5);
        assertThat(result).isEqualTo("hello5");
    }

    // ==== UTILITY METHODS TESTS ====

    @Test
    public void testAllocateInstance() throws Exception {
        TestClass instance = (TestClass) Narcissus.allocateInstance(TestClass.class);
        assertThat(instance).isNotNull();
        assertThat(instance).isInstanceOf(TestClass.class);

        // Constructor should not have been called
        assertThat(instance.publicField).isNull(); // Default value, not constructor-set value
    }

    @Test
    public void testSneakyThrow() {
        Exception testException = new Exception("Test exception");

        try {
            Narcissus.sneakyThrow(testException);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception thrown) {
            assertThat(thrown).isEqualTo(testException);
            assertThat(thrown.getMessage()).isEqualTo("Test exception");
        }
    }

    // ==== ERROR HANDLING AND JVM CRASH PREVENTION ====

    @Test
    public void testNullInputHandling() throws NoSuchFieldException, NoSuchMethodException {
        // Test with null class name - should not crash JVM
        try {
            Narcissus.findClass(null);
            assertThat(false).isTrue(); // Should throw exception
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Test with null object for field access - should not crash JVM
        TestClass obj = new TestClass("test");
        Field field = Narcissus.findField(TestClass.class, "publicField");

        try {
            Narcissus.getObjectField(null, field);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }

        try {
            Narcissus.setObjectField(null, field, "value");
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }

        // Test with null method for invocation - should not crash JVM
        try {
            Narcissus.invokeObjectMethod(obj, null);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }

        // Test with null field parameter - should not crash JVM
        try {
            Narcissus.getObjectField(obj, null);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }

        // Test utility methods with null - should not crash JVM
        try {
            Narcissus.allocateInstance(null);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }

        try {
            Narcissus.sneakyThrow(null);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected - should not crash JVM
        }
    }

    @Test
    public void testStaticInstanceConfusion() throws NoSuchFieldException, NoSuchMethodException {
        TestClass obj = new TestClass("test");
        Field staticField = Narcissus.findField(TestClass.class, "staticField");
        Field instanceField = Narcissus.findField(TestClass.class, "publicField");
        Method staticMethod = Narcissus.findMethod(TestClass.class, "staticMethod");
        Method instanceMethod = Narcissus.findMethod(TestClass.class, "testMethod", String.class, int.class);

        // Try to use instance methods on static context - should not crash JVM
        try {
            Narcissus.invokeStaticMethod(instanceMethod);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected
        }

        // Try to use static methods on instance context - should not crash JVM
        try {
            Narcissus.invokeMethod(obj, staticMethod);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected
        }

        // Try to use instance field getters on static context - should not crash JVM
        try {
            Narcissus.getStaticField(instanceField);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected
        }

        // Try to use static field getters on instance context - should not crash JVM
        try {
            Narcissus.getField(obj, staticField);
            assertThat(false).isTrue(); // Should throw exception
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testExtremeValues() throws NoSuchFieldException {
        TestFieldsClass obj = new TestFieldsClass();

        // Test with extreme integer values
        Field intField = Narcissus.findField(TestFieldsClass.class, "intField");
        Narcissus.setIntField(obj, intField, Integer.MAX_VALUE);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(Integer.MAX_VALUE);

        Narcissus.setIntField(obj, intField, Integer.MIN_VALUE);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(Integer.MIN_VALUE);

        // Test with special double values
        Field doubleField = Narcissus.findField(TestFieldsClass.class, "doubleField");
        Narcissus.setDoubleField(obj, doubleField, Double.POSITIVE_INFINITY);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(Double.POSITIVE_INFINITY);

        Narcissus.setDoubleField(obj, doubleField, Double.NaN);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isNaN();
    }

    // ==== REFLECTION CACHE TESTS ====

    @Test
    public void testReflectionCache() {
        ReflectionCache cache = new ReflectionCache(TestClass.class);
        assertThat(cache).isNotNull();

        Field field = cache.getField("publicField");
        assertThat(field).isNotNull();
        assertThat(field.getType()).isEqualTo(String.class);

        List<Method> methods = cache.getMethods("testMethod");
        assertThat(methods).isNotNull();
        assertThat(methods.size()).isGreaterThan(0);

        Method method = cache.getMethod("testMethod", String.class, int.class);
        assertThat(method).isNotNull();
        assertThat(method.getParameterTypes()).containsExactly(String.class, int.class);
    }

    @Test
    public void testReflectionCacheWithNulls() {
        ReflectionCache cache = new ReflectionCache(TestClass.class);

        Field nullField = cache.getField(null);
        assertThat(nullField).isNull();

        Field nonExistentField = cache.getField("nonExistentField");
        assertThat(nonExistentField).isNull();

        List<Method> nullMethods = cache.getMethods(null);
        assertThat(nullMethods).isNull();

        Method nullMethod = cache.getMethod(null);
        assertThat(nullMethod).isNull();
    }

    // Test classes
    public static class TestClass {
        public String publicField;
        private int privateField = 42;
        public static double staticField = 3.14;

        public TestClass() {
        }

        public TestClass(String value) {
            this.publicField = value;
        }

        public String testMethod(String str, int num) {
            return str + num;
        }

        public static void staticMethod() {
        }
    }

    public static class TestFieldsClass {
        public int intField = 42;
        public long longField = 999L;
        public boolean booleanField = true;
        public char charField = 'A';
        public byte byteField = 127;
        public short shortField = 123;
        public float floatField = 3.14f;
        public double doubleField = 2.718;
        public String objectField = "test";

        public static int staticIntField = 999;
    }

    public static class TestMethodsClass {
        public boolean voidMethodCalled = false;
        public static boolean staticMethodCalled = false;

        public void voidMethod() {
            voidMethodCalled = true;
        }

        public int intMethod() {
            return 42;
        }

        public long longMethod() {
            return 999L;
        }

        public boolean booleanMethod() {
            return true;
        }

        public String objectMethod() {
            return "test string";
        }

        public String methodWithParams(String str, int num) {
            return str + num;
        }

        public static void staticMethod() {
            staticMethodCalled = true;
        }
    }
}