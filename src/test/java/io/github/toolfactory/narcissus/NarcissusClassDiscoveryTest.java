package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for Narcissus class discovery and reflection operations.
 * Tests findClass, findMethod, findField, findConstructor, and enumeration methods.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusClassDiscoveryTest {

    @BeforeEach
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testLibraryLoadedField() {
        assertThat(Narcissus.libraryLoaded).isTrue();
    }

    @Test
    public void testFindStringClass() {
        Class<?> stringClass = Narcissus.findClass("java.lang.String");
        assertThat(stringClass).isEqualTo(String.class);
    }

    @Test
    public void testFindIntegerClass() {
        Class<?> integerClass = Narcissus.findClass("java.lang.Integer");
        assertThat(integerClass).isEqualTo(Integer.class);
    }

    @Test
    public void testFindObjectClass() {
        Class<?> objectClass = Narcissus.findClass("java.lang.Object");
        assertThat(objectClass).isEqualTo(Object.class);
    }

    @Test
    public void testFindStringArrayClass() {
        Class<?> stringArrayClass = Narcissus.findClass("[Ljava.lang.String;");
        assertThat(stringArrayClass).isEqualTo(String[].class);
        assertThat(stringArrayClass.getName()).isEqualTo("[Ljava.lang.String;");
    }

    @Test
    public void testFindIntArrayClass() {
        Class<?> intArrayClass = Narcissus.findClass("[I");
        assertThat(intArrayClass).isEqualTo(int[].class);
        assertThat(intArrayClass.getName()).isEqualTo("[I");
    }

    @Test
    public void testFindMultiDimensionalArrayClass() {
        Class<?> multiDimArrayClass = Narcissus.findClass("[[Ljava.lang.Object;");
        assertThat(multiDimArrayClass).isEqualTo(Object[][].class);
        assertThat(multiDimArrayClass.getName()).isEqualTo("[[Ljava.lang.Object;");
    }

    @Test
    public void testFindIntPrimitiveArrayClass() {
        Class<?> intArrayClass = Narcissus.findClass("[I");
        assertThat(intArrayClass).isEqualTo(int[].class);
        assertThat(intArrayClass.getName()).isEqualTo("[I");
    }

    @Test
    public void testFindDoublePrimitiveArrayClass() {
        Class<?> doubleArrayClass = Narcissus.findClass("[D");
        assertThat(doubleArrayClass).isEqualTo(double[].class);
        assertThat(doubleArrayClass.getName()).isEqualTo("[D");
    }

    @Test
    public void testFindBooleanPrimitiveArrayClass() {
        Class<?> booleanArrayClass = Narcissus.findClass("[Z");
        assertThat(booleanArrayClass).isEqualTo(boolean[].class);
        assertThat(booleanArrayClass.getName()).isEqualTo("[Z");
    }

    @Test
    public void testFindCharPrimitiveArrayClass() {
        Class<?> charArrayClass = Narcissus.findClass("[C");
        assertThat(charArrayClass).isEqualTo(char[].class);
        assertThat(charArrayClass.getName()).isEqualTo("[C");
    }

    @Test
    public void testFindBytePrimitiveArrayClass() {
        Class<?> byteArrayClass = Narcissus.findClass("[B");
        assertThat(byteArrayClass).isEqualTo(byte[].class);
        assertThat(byteArrayClass.getName()).isEqualTo("[B");
    }

    @Test
    public void testFindMethodWithNoParameters() throws NoSuchMethodException {
        Method method = Narcissus.findMethod(String.class, "length");
        assertThat(method).isNotNull();
        assertThat(method.getName()).isEqualTo("length");
        assertThat(method.getDeclaringClass()).isEqualTo(String.class);
    }

    @Test
    public void testFindMethodWithOneParameter() throws NoSuchMethodException {
        Method method = Narcissus.findMethod(String.class, "charAt", int.class);
        assertThat(method).isNotNull();
        assertThat(method.getName()).isEqualTo("charAt");
        assertThat(method.getParameterTypes()).containsExactly(int.class);
    }

    @Test
    public void testFindMethodWithTwoParameters() throws NoSuchMethodException {
        Method method = Narcissus.findMethod(String.class, "substring", int.class, int.class);
        assertThat(method).isNotNull();
        assertThat(method.getName()).isEqualTo("substring");
        assertThat(method.getParameterTypes()).containsExactly(int.class, int.class);
    }

    @Test
    public void testFindMethodWithInheritance() throws NoSuchMethodException {
        // Test finding inherited method
        Method method = Narcissus.findMethod(String.class, "toString");
        assertThat(method).isNotNull();
        assertThat(method.getName()).isEqualTo("toString");
        
        method = Narcissus.findMethod(String.class, "equals", Object.class);
        assertThat(method).isNotNull();
        assertThat(method.getName()).isEqualTo("equals");
        assertThat(method.getParameterTypes()).containsExactly(Object.class);
    }

    @Test
    public void testFindPublicField() throws NoSuchFieldException {
        Field field = Narcissus.findField(TestDiscoveryClass.class, "publicField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("publicField");
        assertThat(field.getDeclaringClass()).isEqualTo(TestDiscoveryClass.class);
    }

    @Test
    public void testFindPrivateField() throws NoSuchFieldException {
        Field field = Narcissus.findField(TestDiscoveryClass.class, "privateField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("privateField");
    }

    @Test
    public void testFindStaticField() throws NoSuchFieldException {
        Field field = Narcissus.findField(TestDiscoveryClass.class, "staticField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("staticField");
    }

    @Test
    public void testFindFieldWithInheritance() throws NoSuchFieldException {
        // Test finding inherited field
        Field field = Narcissus.findField(TestSubclass.class, "parentField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("parentField");
        assertThat(field.getDeclaringClass()).isEqualTo(TestParentClass.class);
        
        field = Narcissus.findField(TestSubclass.class, "childField");
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("childField");
        assertThat(field.getDeclaringClass()).isEqualTo(TestSubclass.class);
    }

    @Test
    public void testFindDefaultConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = Narcissus.findConstructor(String.class);
        assertThat(constructor).isNotNull();
        assertThat(constructor.getDeclaringClass()).isEqualTo(String.class);
        assertThat(constructor.getParameterTypes()).isEmpty();
    }

    @Test
    public void testFindParameterizedStringConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = Narcissus.findConstructor(String.class, String.class);
        assertThat(constructor).isNotNull();
        assertThat(constructor.getParameterTypes()).containsExactly(String.class);
    }

    @Test
    public void testFindParameterizedTestClassConstructor() throws NoSuchMethodException {
        Constructor<?> constructor = Narcissus.findConstructor(TestDiscoveryClass.class, int.class);
        assertThat(constructor).isNotNull();
        assertThat(constructor.getParameterTypes()).containsExactly(int.class);
    }

    @Test
    public void testEnumerateMethods() {
        List<Method> methods = Narcissus.enumerateMethods(TestDiscoveryClass.class);
        assertThat(methods).isNotNull();
        assertThat(methods).isNotEmpty();
        
        // Should find public method
        boolean foundPublicMethod = false;
        for (Method method : methods) {
            if ("publicMethod".equals(method.getName())) {
                foundPublicMethod = true;
                break;
            }
        }
        assertThat(foundPublicMethod).isTrue();
        
        // Should find private method
        boolean foundPrivateMethod = false;
        for (Method method : methods) {
            if ("privateMethod".equals(method.getName())) {
                foundPrivateMethod = true;
                break;
            }
        }
        assertThat(foundPrivateMethod).isTrue();
    }

    @Test
    public void testEnumerateMethodsWithInheritance() {
        List<Method> methods = Narcissus.enumerateMethods(TestSubclass.class);
        assertThat(methods).isNotNull();
        assertThat(methods).isNotEmpty();
        
        // Should find parent method
        boolean foundParentMethod = false;
        for (Method method : methods) {
            if ("parentMethod".equals(method.getName())) {
                foundParentMethod = true;
                break;
            }
        }
        assertThat(foundParentMethod).isTrue();
        
        // Should find child method
        boolean foundChildMethod = false;
        for (Method method : methods) {
            if ("childMethod".equals(method.getName())) {
                foundChildMethod = true;
                break;
            }
        }
        assertThat(foundChildMethod).isTrue();
    }

    @Test
    public void testEnumerateFields() {
        List<Field> fields = Narcissus.enumerateFields(TestDiscoveryClass.class);
        assertThat(fields).isNotNull();
        assertThat(fields).isNotEmpty();
        
        // Should find public field
        boolean foundPublicField = false;
        for (Field field : fields) {
            if ("publicField".equals(field.getName())) {
                foundPublicField = true;
                break;
            }
        }
        assertThat(foundPublicField).isTrue();
        
        // Should find private field
        boolean foundPrivateField = false;
        for (Field field : fields) {
            if ("privateField".equals(field.getName())) {
                foundPrivateField = true;
                break;
            }
        }
        assertThat(foundPrivateField).isTrue();
    }

    @Test
    public void testEnumerateFieldsWithInheritance() {
        List<Field> fields = Narcissus.enumerateFields(TestSubclass.class);
        assertThat(fields).isNotNull();
        assertThat(fields).isNotEmpty();
        
        // Should find parent field
        boolean foundParentField = false;
        for (Field field : fields) {
            if ("parentField".equals(field.getName())) {
                foundParentField = true;
                break;
            }
        }
        assertThat(foundParentField).isTrue();
        
        // Should find child field
        boolean foundChildField = false;
        for (Field field : fields) {
            if ("childField".equals(field.getName())) {
                foundChildField = true;
                break;
            }
        }
        assertThat(foundChildField).isTrue();
    }

    @Test
    public void testGetDeclaredMethods() {
        Method[] methods = Narcissus.getDeclaredMethods(TestDiscoveryClass.class);
        assertThat(methods).isNotNull();
        assertThat(methods.length).isGreaterThan(0);
        
        // Should find public method
        boolean foundPublicMethod = false;
        for (Method method : methods) {
            if ("publicMethod".equals(method.getName())) {
                foundPublicMethod = true;
                break;
            }
        }
        assertThat(foundPublicMethod).isTrue();
    }

    @Test
    public void testGetDeclaredConstructors() {
        Constructor<?>[] constructors = Narcissus.getDeclaredConstructors(TestDiscoveryClass.class);
        assertThat(constructors).isNotNull();
        assertThat(constructors.length).isGreaterThan(0);
        
        // Should have default constructor and parameterized constructor
        boolean foundDefaultConstructor = false;
        boolean foundParamConstructor = false;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                foundDefaultConstructor = true;
            } else if (constructor.getParameterTypes().length == 1 && 
                       constructor.getParameterTypes()[0] == int.class) {
                foundParamConstructor = true;
            }
        }
        assertThat(foundDefaultConstructor).isTrue();
        assertThat(foundParamConstructor).isTrue();
    }

    @Test
    public void testGetDeclaredFields() {
        Field[] fields = Narcissus.getDeclaredFields(TestDiscoveryClass.class);
        assertThat(fields).isNotNull();
        assertThat(fields.length).isGreaterThan(0);
        
        // Should find public field
        boolean foundPublicField = false;
        for (Field field : fields) {
            if ("publicField".equals(field.getName())) {
                foundPublicField = true;
                break;
            }
        }
        assertThat(foundPublicField).isTrue();
    }

    // Test classes for discovery operations
    public static class TestDiscoveryClass {
        public int publicField;
        private String privateField;
        public static int staticField;
        
        public TestDiscoveryClass() {
        }
        
        public TestDiscoveryClass(int value) {
            this.publicField = value;
        }
        
        public void publicMethod() {
        }
        
        private void privateMethod() {
        }
        
        public static void staticMethod() {
        }
    }
    
    public static class TestParentClass {
        public int parentField;
        
        public void parentMethod() {
        }
    }
    
    public static class TestSubclass extends TestParentClass {
        public String childField;
        
        public void childMethod() {
        }
    }
}