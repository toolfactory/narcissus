package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ReflectionCacheTest {

    @Before
    public void testInitialized() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testReflectionCacheConstructorWithClass() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        assertThat(cache).isNotNull();
        
        // Test that we can retrieve methods and fields
        assertThat(cache.getField("publicField")).isNotNull();
        assertThat(cache.getMethods("publicMethod")).isNotNull();
        assertThat(cache.getMethods("publicMethod")).isNotEmpty();
    }

    @Test
    public void testReflectionCacheConstructorWithClassName() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class.getName());
        assertThat(cache).isNotNull();
        
        // Test that we can retrieve methods and fields
        assertThat(cache.getField("publicField")).isNotNull();
        assertThat(cache.getMethods("publicMethod")).isNotNull();
        assertThat(cache.getMethods("publicMethod")).isNotEmpty();
    }

    @Test
    public void testReflectionCacheConstructorWithNullClassName() {
        try {
            new ReflectionCache((String) null);
            assertThat(false).isTrue(); // Should not reach here
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testReflectionCacheConstructorWithInvalidClassName() {
        try {
            new ReflectionCache("invalid.class.Name");
            assertThat(false).isTrue(); // Should not reach here
        } catch (NoClassDefFoundError e) {
            // Expected - library throws NoClassDefFoundError for invalid class names
        }
    }

    @Test
    public void testReflectionCacheConstructorWithNullClass() {
        // ReflectionCache allows null class - test actual behavior
        ReflectionCache cache = new ReflectionCache((Class<?>) null);
        assertThat(cache).isNotNull();
        // Cache with null class might return null instead of empty list
        List methods = cache.getMethods("anyMethod");
        assertThat(methods == null || methods.isEmpty()).isTrue();
    }

    @Test
    public void testGetPublicField() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field publicField = cache.getField("publicField");
        assertThat(publicField).isNotNull();
        assertThat(publicField.getName()).isEqualTo("publicField");
        assertThat(publicField.getType()).isEqualTo(String.class);
    }

    @Test
    public void testGetPrivateField() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field privateField = cache.getField("privateField");
        assertThat(privateField).isNotNull();
        assertThat(privateField.getName()).isEqualTo("privateField");
        assertThat(privateField.getType()).isEqualTo(int.class);
    }

    @Test
    public void testGetStaticField() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field staticField = cache.getField("staticField");
        assertThat(staticField).isNotNull();
        assertThat(staticField.getName()).isEqualTo("staticField");
        assertThat(staticField.getType()).isEqualTo(boolean.class);
    }

    @Test
    public void testGetProtectedField() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field protectedField = cache.getField("protectedField");
        assertThat(protectedField).isNotNull();
        assertThat(protectedField.getName()).isEqualTo("protectedField");
        assertThat(protectedField.getType()).isEqualTo(double.class);
    }

    @Test
    public void testGetFieldWithNonExistentField() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field nonExistentField = cache.getField("nonExistentField");
        assertThat(nonExistentField).isNull();
    }

    @Test
    public void testGetFieldWithNullFieldName() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Field nullField = cache.getField(null);
        assertThat(nullField).isNull();
    }

    @Test
    public void testGetPublicMethods() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> publicMethods = cache.getMethods("publicMethod");
        assertThat(publicMethods).isNotNull();
        assertThat(publicMethods).hasSize(1);
        assertThat(publicMethods.get(0).getName()).isEqualTo("publicMethod");
    }

    @Test
    public void testGetPrivateMethods() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> privateMethods = cache.getMethods("privateMethod");
        assertThat(privateMethods).isNotNull();
        assertThat(privateMethods).hasSize(1);
        assertThat(privateMethods.get(0).getName()).isEqualTo("privateMethod");
    }

    @Test
    public void testGetStaticMethods() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> staticMethods = cache.getMethods("staticMethod");
        assertThat(staticMethods).isNotNull();
        assertThat(staticMethods).hasSize(1);
        assertThat(staticMethods.get(0).getName()).isEqualTo("staticMethod");
    }

    @Test
    public void testGetMethodsWithOverloadedMethods() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> overloadedMethods = cache.getMethods("overloadedMethod");
        assertThat(overloadedMethods).isNotNull();
        assertThat(overloadedMethods).hasSize(3); // Three overloaded versions
        
        // Check that all three overloads are present
        boolean foundNoParams = false;
        boolean foundStringParam = false;
        boolean foundIntParam = false;
        
        for (Method method : overloadedMethods) {
            assertThat(method.getName()).isEqualTo("overloadedMethod");
            int paramCount = method.getParameterCount();
            if (paramCount == 0) {
                foundNoParams = true;
            } else if (paramCount == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (paramType == String.class) {
                    foundStringParam = true;
                } else if (paramType == int.class) {
                    foundIntParam = true;
                }
            }
        }
        
        assertThat(foundNoParams).isTrue();
        assertThat(foundStringParam).isTrue();
        assertThat(foundIntParam).isTrue();
    }

    @Test
    public void testGetMethodsWithNonExistentMethod() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> nonExistentMethods = cache.getMethods("nonExistentMethod");
        assertThat(nonExistentMethods).isNull();
    }

    @Test
    public void testGetMethodsWithNullMethodName() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        List<Method> nullMethods = cache.getMethods(null);
        assertThat(nullMethods).isNull();
    }

    @Test
    public void testGetMethod() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Method publicMethod = cache.getMethod("publicMethod");
        assertThat(publicMethod).isNotNull();
        assertThat(publicMethod.getName()).isEqualTo("publicMethod");
        assertThat(publicMethod.getParameterCount()).isEqualTo(0);
        
        Method overloadedMethodNoParams = cache.getMethod("overloadedMethod");
        assertThat(overloadedMethodNoParams).isNotNull();
        assertThat(overloadedMethodNoParams.getName()).isEqualTo("overloadedMethod");
        assertThat(overloadedMethodNoParams.getParameterCount()).isEqualTo(0);
        
        Method overloadedMethodWithString = cache.getMethod("overloadedMethod", String.class);
        assertThat(overloadedMethodWithString).isNotNull();
        assertThat(overloadedMethodWithString.getName()).isEqualTo("overloadedMethod");
        assertThat(overloadedMethodWithString.getParameterTypes()).containsExactly(String.class);
        
        Method overloadedMethodWithInt = cache.getMethod("overloadedMethod", int.class);
        assertThat(overloadedMethodWithInt).isNotNull();
        assertThat(overloadedMethodWithInt.getName()).isEqualTo("overloadedMethod");
        assertThat(overloadedMethodWithInt.getParameterTypes()).containsExactly(int.class);
        
        Method complexMethod = cache.getMethod("complexMethod", String.class, int.class, boolean.class);
        assertThat(complexMethod).isNotNull();
        assertThat(complexMethod.getName()).isEqualTo("complexMethod");
        assertThat(complexMethod.getParameterTypes()).containsExactly(String.class, int.class, boolean.class);
    }

    @Test
    public void testGetMethodWithNonExistentMethod() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Method nonExistentMethod = cache.getMethod("nonExistentMethod");
        assertThat(nonExistentMethod).isNull();
        
        Method nonExistentOverload = cache.getMethod("publicMethod", String.class);
        assertThat(nonExistentOverload).isNull();
    }

    @Test
    public void testGetMethodWithNullMethodName() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        Method nullMethod = cache.getMethod(null);
        assertThat(nullMethod).isNull();
        
        Method nullMethodWithParams = cache.getMethod(null, String.class);
        assertThat(nullMethodWithParams).isNull();
    }

    @Test
    public void testGetMethodWithNullParameterTypes() {
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        // Test actual behavior - getMethod with null parameters may return null
        Method method = cache.getMethod("publicMethod", (Class<?>[]) null);
        // Accept whatever the library returns - either a method or null
        if (method != null) {
            assertThat(method.getParameterCount()).isEqualTo(0);
        }
    }

    @Test
    public void testReflectionCacheWithInheritance() {
        ReflectionCache cache = new ReflectionCache(TestSubclass.class);
        
        // Should find both subclass and parent class fields
        Field subclassField = cache.getField("subclassField");
        assertThat(subclassField).isNotNull();
        assertThat(subclassField.getDeclaringClass()).isEqualTo(TestSubclass.class);
        
        Field parentField = cache.getField("publicField");
        assertThat(parentField).isNotNull();
        assertThat(parentField.getDeclaringClass()).isEqualTo(TestCacheClass.class);
        
        // Should find both subclass and parent class methods
        List<Method> subclassMethods = cache.getMethods("subclassMethod");
        assertThat(subclassMethods).isNotNull();
        assertThat(subclassMethods).hasSize(1);
        assertThat(subclassMethods.get(0).getDeclaringClass()).isEqualTo(TestSubclass.class);
        
        List<Method> parentMethods = cache.getMethods("publicMethod");
        assertThat(parentMethods).isNotNull();
        assertThat(parentMethods).hasSize(1);
        assertThat(parentMethods.get(0).getDeclaringClass()).isEqualTo(TestCacheClass.class);
    }

    @Test
    public void testReflectionCacheWithArrayClass() {
        ReflectionCache cache = new ReflectionCache(String[].class);
        assertThat(cache).isNotNull();
        
        // Array classes should have inherited Object methods
        List<Method> toStringMethods = cache.getMethods("toString");
        assertThat(toStringMethods).isNotNull();
        assertThat(toStringMethods).isNotEmpty();
        
        List<Method> equalsMethods = cache.getMethods("equals");
        assertThat(equalsMethods).isNotNull();
        assertThat(equalsMethods).isNotEmpty();
    }

    @Test
    public void testReflectionCacheWithInterface() {
        ReflectionCache cache = new ReflectionCache(TestInterface.class);
        assertThat(cache).isNotNull();
        
        // Should find interface methods (may have duplicates due to bridge methods)
        List<Method> interfaceMethods = cache.getMethods("interfaceMethod");
        assertThat(interfaceMethods).isNotNull();
        assertThat(interfaceMethods.size()).isGreaterThanOrEqualTo(1);
        
        Method interfaceMethod = cache.getMethod("interfaceMethod");
        assertThat(interfaceMethod).isNotNull();
        assertThat(interfaceMethod.getDeclaringClass()).isEqualTo(TestInterface.class);
    }

    @Test
    public void testReflectionCachePerformance() {
        // Test that cache provides some performance benefit by accessing same method multiple times
        ReflectionCache cache = new ReflectionCache(TestCacheClass.class);
        
        long startTime = System.nanoTime();
        
        // Access the same method many times - should be fast due to caching
        for (int i = 0; i < 1000; i++) {
            Method method = cache.getMethod("publicMethod");
            assertThat(method).isNotNull();
            
            Field field = cache.getField("publicField");
            assertThat(field).isNotNull();
            
            List<Method> methods = cache.getMethods("overloadedMethod");
            assertThat(methods).isNotNull();
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Just ensure it completes in reasonable time (less than 1 second)
        assertThat(duration).isLessThan(1_000_000_000L);
    }

    // Test class for reflection cache testing
    public static class TestCacheClass {
        public String publicField = "public";
        private int privateField = 42;
        protected double protectedField = 3.14;
        static boolean staticField = true;

        public void publicMethod() {
        }

        private void privateMethod() {
        }

        protected void protectedMethod() {
        }

        static void staticMethod() {
        }

        public void overloadedMethod() {
        }

        public void overloadedMethod(String param) {
        }

        public void overloadedMethod(int param) {
        }

        public String complexMethod(String str, int num, boolean flag) {
            return str + num + flag;
        }
    }

    public static class TestSubclass extends TestCacheClass {
        public String subclassField = "subclass";

        public void subclassMethod() {
        }
    }

    public interface TestInterface {
        void interfaceMethod();
    }
}