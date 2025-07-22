package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class NarcissusEdgeCasesTest {

    @Before
    public void testInitialized() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    // ==== EXTREME VALUE TESTS ====

    @Test
    public void testIntegerMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field intField = Narcissus.findField(TestEdgeCasesClass.class, "intField");

        Narcissus.setIntField(obj, intField, Integer.MAX_VALUE);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testIntegerMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field intField = Narcissus.findField(TestEdgeCasesClass.class, "intField");

        Narcissus.setIntField(obj, intField, Integer.MIN_VALUE);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testIntegerZeroValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field intField = Narcissus.findField(TestEdgeCasesClass.class, "intField");

        Narcissus.setIntField(obj, intField, 0);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(0);
    }

    @Test
    public void testIntegerNegativeOneValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field intField = Narcissus.findField(TestEdgeCasesClass.class, "intField");

        Narcissus.setIntField(obj, intField, -1);
        assertThat(Narcissus.getIntField(obj, intField)).isEqualTo(-1);
    }

    @Test
    public void testLongMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field longField = Narcissus.findField(TestEdgeCasesClass.class, "longField");

        Narcissus.setLongField(obj, longField, Long.MAX_VALUE);
        assertThat(Narcissus.getLongField(obj, longField)).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testLongMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field longField = Narcissus.findField(TestEdgeCasesClass.class, "longField");

        Narcissus.setLongField(obj, longField, Long.MIN_VALUE);
        assertThat(Narcissus.getLongField(obj, longField)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testLongZeroValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field longField = Narcissus.findField(TestEdgeCasesClass.class, "longField");

        Narcissus.setLongField(obj, longField, 0L);
        assertThat(Narcissus.getLongField(obj, longField)).isEqualTo(0L);
    }

    @Test
    public void testDoubleMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, Double.MAX_VALUE);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    public void testDoubleMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, Double.MIN_VALUE);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    public void testDoublePositiveInfinity() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, Double.POSITIVE_INFINITY);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testDoubleNegativeInfinity() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, Double.NEGATIVE_INFINITY);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testDoubleNaNValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, Double.NaN);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isNaN();
    }

    @Test
    public void testDoubleZeroValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, 0.0);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(0.0);
    }

    @Test
    public void testDoubleNegativeZeroValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field doubleField = Narcissus.findField(TestEdgeCasesClass.class, "doubleField");

        Narcissus.setDoubleField(obj, doubleField, -0.0);
        assertThat(Narcissus.getDoubleField(obj, doubleField)).isEqualTo(-0.0);
    }

    @Test
    public void testFloatMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field floatField = Narcissus.findField(TestEdgeCasesClass.class, "floatField");

        Narcissus.setFloatField(obj, floatField, Float.MAX_VALUE);
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(Float.MAX_VALUE);
    }

    @Test
    public void testFloatMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field floatField = Narcissus.findField(TestEdgeCasesClass.class, "floatField");

        Narcissus.setFloatField(obj, floatField, Float.MIN_VALUE);
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    public void testFloatPositiveInfinity() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field floatField = Narcissus.findField(TestEdgeCasesClass.class, "floatField");

        Narcissus.setFloatField(obj, floatField, Float.POSITIVE_INFINITY);
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    public void testFloatNegativeInfinity() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field floatField = Narcissus.findField(TestEdgeCasesClass.class, "floatField");

        Narcissus.setFloatField(obj, floatField, Float.NEGATIVE_INFINITY);
        assertThat(Narcissus.getFloatField(obj, floatField)).isEqualTo(Float.NEGATIVE_INFINITY);
    }

    @Test
    public void testFloatNaNValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field floatField = Narcissus.findField(TestEdgeCasesClass.class, "floatField");

        Narcissus.setFloatField(obj, floatField, Float.NaN);
        assertThat(Narcissus.getFloatField(obj, floatField)).isNaN();
    }

    @Test
    public void testCharacterMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field charField = Narcissus.findField(TestEdgeCasesClass.class, "charField");

        Narcissus.setCharField(obj, charField, Character.MAX_VALUE);
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo(Character.MAX_VALUE);
    }

    @Test
    public void testCharacterMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field charField = Narcissus.findField(TestEdgeCasesClass.class, "charField");

        Narcissus.setCharField(obj, charField, Character.MIN_VALUE);
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo(Character.MIN_VALUE);
    }

    @Test
    public void testCharacterNullValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field charField = Narcissus.findField(TestEdgeCasesClass.class, "charField");

        Narcissus.setCharField(obj, charField, '\u0000'); // Null character
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo('\u0000');
    }

    @Test
    public void testCharacterMaxUnicode() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field charField = Narcissus.findField(TestEdgeCasesClass.class, "charField");

        Narcissus.setCharField(obj, charField, '\uFFFF'); // Max Unicode
        assertThat(Narcissus.getCharField(obj, charField)).isEqualTo('\uFFFF');
    }

    @Test
    public void testByteMaxValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field byteField = Narcissus.findField(TestEdgeCasesClass.class, "byteField");

        Narcissus.setByteField(obj, byteField, Byte.MAX_VALUE);
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo(Byte.MAX_VALUE);
    }

    @Test
    public void testByteMinValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field byteField = Narcissus.findField(TestEdgeCasesClass.class, "byteField");

        Narcissus.setByteField(obj, byteField, Byte.MIN_VALUE);
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    public void testByteZeroValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field byteField = Narcissus.findField(TestEdgeCasesClass.class, "byteField");

        Narcissus.setByteField(obj, byteField, (byte) 0);
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo((byte) 0);
    }

    @Test
    public void testByteNegativeOneValue() throws NoSuchFieldException {
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        Field byteField = Narcissus.findField(TestEdgeCasesClass.class, "byteField");

        Narcissus.setByteField(obj, byteField, (byte) -1);
        assertThat(Narcissus.getByteField(obj, byteField)).isEqualTo((byte) -1);
    }

    // ==== COMPLEX CLASS HIERARCHIES ====

    @Test
    public void testDiamondInheritanceFieldFinding() throws NoSuchFieldException {
        DiamondSubclass obj = new DiamondSubclass();
        
        // Should find fields from all levels of hierarchy
        Field baseField = Narcissus.findField(DiamondSubclass.class, "baseField");
        assertThat(baseField).isNotNull();
        assertThat(baseField.getDeclaringClass()).isEqualTo(DiamondBase.class);
        
        Field leftField = Narcissus.findField(DiamondSubclass.class, "leftField");
        assertThat(leftField).isNotNull();
        assertThat(leftField.getDeclaringClass()).isEqualTo(DiamondLeft.class);
    }

    @Test
    public void testDiamondInheritanceMethodFinding() throws NoSuchMethodException {
        DiamondSubclass obj = new DiamondSubclass();
        
        // Should find methods from all levels
        Method baseMethod = Narcissus.findMethod(DiamondSubclass.class, "baseMethod");
        assertThat(baseMethod).isNotNull();
        
        Method leftMethod = Narcissus.findMethod(DiamondSubclass.class, "leftMethod");
        assertThat(leftMethod).isNotNull();
    }

    @Test
    public void testInterfaceMethodFinding() throws NoSuchMethodException {
        ConcreteImplementation obj = new ConcreteImplementation();
        
        // Should find interface methods
        Method interfaceMethod1 = Narcissus.findMethod(ConcreteImplementation.class, "interfaceMethod1");
        assertThat(interfaceMethod1).isNotNull();
        
        Method interfaceMethod2 = Narcissus.findMethod(ConcreteImplementation.class, "interfaceMethod2");
        assertThat(interfaceMethod2).isNotNull();
    }

    @Test
    public void testInterfaceMethodInvocation() throws NoSuchMethodException {
        ConcreteImplementation obj = new ConcreteImplementation();
        
        Method interfaceMethod1 = Narcissus.findMethod(ConcreteImplementation.class, "interfaceMethod1");
        Method interfaceMethod2 = Narcissus.findMethod(ConcreteImplementation.class, "interfaceMethod2");
        
        // Should be able to invoke interface methods
        Object result1 = Narcissus.invokeMethod(obj, interfaceMethod1);
        assertThat(result1).isEqualTo("interface1");
        
        Object result2 = Narcissus.invokeMethod(obj, interfaceMethod2);
        assertThat(result2).isEqualTo("interface2");
    }

    @Test
    public void testGenericClassFieldAccess() throws NoSuchFieldException {
        GenericTestClass<String> obj = new GenericTestClass<String>("test");
        
        // Should find generic field
        Field genericField = Narcissus.findField(GenericTestClass.class, "value");
        assertThat(genericField).isNotNull();
        
        // Should be able to access generic field
        Object fieldValue = Narcissus.getObjectField(obj, genericField);
        assertThat(fieldValue).isEqualTo("test");
    }

    @Test
    public void testGenericClassMethodInvocation() throws NoSuchMethodException {
        GenericTestClass<String> obj = new GenericTestClass<String>("test");
        
        // Should find generic method
        Method genericMethod = Narcissus.findMethod(GenericTestClass.class, "getValue");
        assertThat(genericMethod).isNotNull();
        
        // Should be able to invoke generic method
        Object methodResult = Narcissus.invokeObjectMethod(obj, genericMethod);
        assertThat(methodResult).isEqualTo("test");
    }

    // ==== ANONYMOUS AND INNER CLASSES ====

    @Test
    public void testAnonymousClassMethodFinding() throws NoSuchMethodException {
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                // Anonymous class implementation
            }
        };
        
        // Should be able to find methods in anonymous classes
        Method runMethod = Narcissus.findMethod(anonymous.getClass(), "run");
        assertThat(runMethod).isNotNull();
    }

    @Test
    public void testAnonymousClassMethodInvocation() throws NoSuchMethodException {
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                // Anonymous class implementation
            }
        };
        
        Method runMethod = Narcissus.findMethod(anonymous.getClass(), "run");
        
        // Should be able to invoke methods on anonymous classes
        Narcissus.invokeVoidMethod(anonymous, runMethod);
    }

    @Test
    public void testAnonymousClassInsteadOfLambda() {
        // Test with anonymous class instead of lambda for Java 7 compatibility
        Runnable anonymousRunnable = new Runnable() {
            @Override
            public void run() {
                // Anonymous class implementation for Java 7
            }
        };
        
        // Should be able to work with anonymous classes (similar to lambda classes)
        Class<?> anonymousClass = anonymousRunnable.getClass();
        assertThat(anonymousClass).isNotNull();
        
        // Anonymous classes have methods
        Method[] methods = Narcissus.getDeclaredMethods(anonymousClass);
        assertThat(methods).isNotEmpty();
    }

    @Test
    public void testLocalInnerClassFieldAccess() throws NoSuchFieldException {
        class LocalInnerClass {
            private String localField = "local";
            
            public String getLocalField() {
                return localField;
            }
        }
        
        LocalInnerClass obj = new LocalInnerClass();
        
        // Should find local inner class field
        Field localField = Narcissus.findField(LocalInnerClass.class, "localField");
        assertThat(localField).isNotNull();
        
        // Should be able to access local inner class field
        Object fieldValue = Narcissus.getObjectField(obj, localField);
        assertThat(fieldValue).isEqualTo("local");
    }

    @Test
    public void testLocalInnerClassMethodInvocation() throws NoSuchMethodException {
        class LocalInnerClass {
            private String localField = "local";
            
            public String getLocalField() {
                return localField;
            }
        }
        
        LocalInnerClass obj = new LocalInnerClass();
        
        // Should find local inner class method
        Method localMethod = Narcissus.findMethod(LocalInnerClass.class, "getLocalField");
        assertThat(localMethod).isNotNull();
        
        // Should be able to invoke local inner class method
        Object methodResult = Narcissus.invokeObjectMethod(obj, localMethod);
        assertThat(methodResult).isEqualTo("local");
    }

    // ==== SYNTHETIC AND BRIDGE METHODS ====

    @Test
    public void testSyntheticMembers() {
        // Test with enum which has synthetic members
        TestEnum enumValue = TestEnum.VALUE1;
        
        Field[] fields = Narcissus.getDeclaredFields(TestEnum.class);
        Method[] methods = Narcissus.getDeclaredMethods(TestEnum.class);
        
        // Enums have synthetic fields and methods
        assertThat(fields).isNotEmpty();
        assertThat(methods).isNotEmpty();
        
        // Should be able to access enum fields
        boolean foundValueField = false;
        for (Field field : fields) {
            if (field.getName().equals("value")) {
                Object fieldValue = Narcissus.getObjectField(enumValue, field);
                assertThat(fieldValue).isEqualTo("test1");
                foundValueField = true;
                break;
            }
        }
        assertThat(foundValueField).isTrue();
    }

    // ==== PRIMITIVE ARRAY CLASSES ====

    @Test
    public void testIntArrayClass() {
        Class<?> intArrayClass = Narcissus.findClass("[I");
        assertThat(intArrayClass).isEqualTo(int[].class);
        assertThat(intArrayClass.getName()).isEqualTo("[I");
    }

    @Test
    public void testDoubleArrayClass() {
        Class<?> doubleArrayClass = Narcissus.findClass("[D");
        assertThat(doubleArrayClass).isEqualTo(double[].class);
        assertThat(doubleArrayClass.getName()).isEqualTo("[D");
    }

    @Test
    public void testBooleanArrayClass() {
        Class<?> booleanArrayClass = Narcissus.findClass("[Z");
        assertThat(booleanArrayClass).isEqualTo(boolean[].class);
        assertThat(booleanArrayClass.getName()).isEqualTo("[Z");
    }

    // ==== SPECIAL METHOD SIGNATURES ====

    @Test
    public void testVarargsMethod() throws NoSuchMethodException {
        Method varargsMethod = Narcissus.findMethod(TestEdgeCasesClass.class, "varargsMethod", String[].class);
        assertThat(varargsMethod).isNotNull();
        assertThat(varargsMethod.isVarArgs()).isTrue();
        
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        String result = (String) Narcissus.invokeObjectMethod(obj, varargsMethod, "a", "b", "c");
        assertThat(result).isEqualTo("abc");
    }

    @Test
    public void testMethodWithArrayParameters() throws NoSuchMethodException {
        Method arrayMethod = Narcissus.findMethod(TestEdgeCasesClass.class, "arrayMethod", int[].class);
        assertThat(arrayMethod).isNotNull();
        
        TestEdgeCasesClass obj = new TestEdgeCasesClass();
        int[] testArray = {1, 2, 3, 4, 5};
        int result = Narcissus.invokeIntMethod(obj, arrayMethod, testArray);
        assertThat(result).isEqualTo(15); // Sum of array elements
    }

    // ==== CONCURRENT ACCESS TESTS ====

    @Test
    public void testConcurrentFieldAccess() throws NoSuchFieldException, InterruptedException {
        final TestEdgeCasesClass obj = new TestEdgeCasesClass();
        final Field intField = Narcissus.findField(TestEdgeCasesClass.class, "intField");
        
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++) {
                        Narcissus.setIntField(obj, intField, threadId * 100 + j);
                        int value = Narcissus.getIntField(obj, intField);
                        assertThat(value).isGreaterThanOrEqualTo(0);
                    }
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
    }

    // ==== STRESS TESTS ====

    @Test
    public void testMassiveClassHierarchyFieldEnumeration() {
        // Test field enumeration with a class that has many levels of inheritance
        DeepSubclass obj = new DeepSubclass();
        
        // Should be able to enumerate all fields from entire hierarchy
        List<Field> fields = Narcissus.enumerateFields(DeepSubclass.class);
        assertThat(fields.size()).isGreaterThan(3); // At least 4 levels of inheritance
    }

    @Test
    public void testMassiveClassHierarchyFieldFinding() {
        // Test field finding across multiple levels of inheritance
        DeepSubclass obj = new DeepSubclass();
        
        // Should find fields from each level
        List<Field> fields = Narcissus.enumerateFields(DeepSubclass.class);
        boolean foundLevel1Field = false;
        boolean foundLevel2Field = false;
        boolean foundLevel3Field = false;
        boolean foundLevel4Field = false;
        
        for (Field field : fields) {
            if ("level1Field".equals(field.getName())) {
                foundLevel1Field = true;
            } else if ("level2Field".equals(field.getName())) {
                foundLevel2Field = true;
            } else if ("level3Field".equals(field.getName())) {
                foundLevel3Field = true;
            } else if ("level4Field".equals(field.getName())) {
                foundLevel4Field = true;
            }
        }
        
        assertThat(foundLevel1Field).isTrue();
        assertThat(foundLevel2Field).isTrue();
        assertThat(foundLevel3Field).isTrue();
        assertThat(foundLevel4Field).isTrue();
    }

    // Test classes for edge cases
    public static class TestEdgeCasesClass {
        public int intField;
        public long longField;
        public double doubleField;
        public float floatField;
        public char charField;
        public byte byteField;
        
        public String varargsMethod(String... args) {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg);
            }
            return sb.toString();
        }
        
        public int arrayMethod(int[] array) {
            int sum = 0;
            for (int value : array) {
                sum += value;
            }
            return sum;
        }
    }

    // Complex inheritance hierarchy for testing
    public static class DiamondBase {
        public String baseField = "base";
        public void baseMethod() {}
    }

    public static class DiamondLeft extends DiamondBase {
        public String leftField = "left";
        public void leftMethod() {}
    }

    public static class DiamondSubclass extends DiamondLeft {
        public String subField = "sub";
    }

    // Interface testing
    public interface TestInterface1 {
        String interfaceMethod1();
    }

    public interface TestInterface2 {
        String interfaceMethod2();
    }

    public static class ConcreteImplementation implements TestInterface1, TestInterface2 {
        @Override
        public String interfaceMethod1() {
            return "interface1";
        }

        @Override
        public String interfaceMethod2() {
            return "interface2";
        }
    }

    // Generic class testing
    public static class GenericTestClass<T> {
        private T value;
        
        public GenericTestClass(T value) {
            this.value = value;
        }
        
        public T getValue() {
            return value;
        }
    }

    // Enum for synthetic member testing
    public enum TestEnum {
        VALUE1("test1"),
        VALUE2("test2");
        
        private final String value;
        
        TestEnum(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }

    // Deep inheritance hierarchy
    public static class Level1 {
        public String level1Field = "level1";
    }

    public static class Level2 extends Level1 {
        public String level2Field = "level2";
    }

    public static class Level3 extends Level2 {
        public String level3Field = "level3";
    }

    public static class DeepSubclass extends Level3 {
        public String level4Field = "level4";
    }
}