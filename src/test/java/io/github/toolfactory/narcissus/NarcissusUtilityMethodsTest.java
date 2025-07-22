package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for Narcissus utility methods: allocateInstance and sneakyThrow.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusUtilityMethodsTest {

    @BeforeEach
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    @Test
    public void testAllocateInstance() {
        TestClass instance = (TestClass) Narcissus.allocateInstance(TestClass.class);
        assertThat(instance).isNotNull();
        assertThat(instance).isInstanceOf(TestClass.class);
        
        // allocateInstance should bypass constructor
        assertThat(instance.constructorCalled).isFalse();
        assertThat(instance.value).isEqualTo(0); // default primitive value
        
        // Instance should be functional
        instance.setValue(42);
        assertThat(instance.getValue()).isEqualTo(42);
    }

    @Test
    public void testAllocateInstanceWithInterface() {
        try {
            Narcissus.allocateInstance(TestInterface.class);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            assertThat(e).isInstanceOf(InstantiationException.class);
            assertThat(e.getMessage()).contains("TestInterface");
        }
    }

    @Test
    public void testAllocateInstanceWithAbstractClass() {
        try {
            Narcissus.allocateInstance(TestAbstractClass.class);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            assertThat(e).isInstanceOf(InstantiationException.class);
            assertThat(e.getMessage()).contains("TestAbstractClass");
        }
    }

    @Test
    public void testAllocateInstanceWithArrayClass() {
        try {
            Narcissus.allocateInstance(Object[].class);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            assertThat(e).isInstanceOf(InstantiationException.class);
            assertThat(e.getMessage()).contains("[Ljava.lang.Object;");
        }
    }

    @Test
    public void testAllocateInstanceWithPrimitiveClass() {
        try {
            Narcissus.allocateInstance(int.class);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            // Expected - primitive types cannot be instantiated
        }
    }

    @Test
    public void testAllocateInstanceComparison() {
        // Test that allocateInstance behaves differently from constructor
        TestClass constructed = new TestClass(99);
        assertThat(constructed.constructorCalled).isTrue();
        assertThat(constructed.value).isEqualTo(99);
        
        TestClass allocated = (TestClass) Narcissus.allocateInstance(TestClass.class);
        assertThat(allocated.constructorCalled).isFalse();
        assertThat(allocated.value).isEqualTo(0);
    }

    @Test
    public void testAllocateInstanceStress() {
        // Allocate many instances to test for memory leaks or crashes
        for (int i = 0; i < 1000; i++) {
            TestClass instance = (TestClass) Narcissus.allocateInstance(TestClass.class);
            assertThat(instance).isNotNull();
            assertThat(instance.constructorCalled).isFalse();
        }
    }

    @Test
    public void testSneakyThrow() {
        Exception testException = new Exception("Test exception message");
        
        try {
            Narcissus.sneakyThrow(testException);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Exception e) {
            // sneakyThrow throws the exact exception passed to it
            assertThat(e).isSameAs(testException);
            assertThat(e.getMessage()).isEqualTo("Test exception message");
        }
    }

    @Test
    public void testSneakyThrowWithRuntimeException() {
        RuntimeException runtimeException = new RuntimeException("Runtime exception");
        
        try {
            Narcissus.sneakyThrow(runtimeException);
            assertThat(false).isTrue(); // Should not reach here
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(runtimeException);
            assertThat(e.getMessage()).isEqualTo("Runtime exception");
        }
    }

    @Test
    public void testSneakyThrowWithError() {
        Error error = new Error("Test error");
        
        try {
            Narcissus.sneakyThrow(error);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Error e) {
            assertThat(e).isSameAs(error);
            assertThat(e.getMessage()).isEqualTo("Test error");
        }
    }

    @Test
    public void testSneakyThrowWithCustomException() {
        CustomCheckedException customException = new CustomCheckedException("Custom checked exception");
        
        try {
            Narcissus.sneakyThrow(customException);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            assertThat(e).isSameAs(customException);
            assertThat(e.getMessage()).isEqualTo("Custom checked exception");
        }
    }

    @Test
    public void testSneakyThrowStress() {
        // Test many sneaky throws to ensure stability
        for (int i = 0; i < 100; i++) {
            Exception testException = new Exception("Test " + i);
            try {
                Narcissus.sneakyThrow(testException);
                assertThat(false).isTrue(); // Should not reach here
            } catch (Exception e) {
                // sneakyThrow throws the exact exception passed to it
                assertThat(e).isSameAs(testException);
                assertThat(e.getMessage()).isEqualTo("Test " + i);
            }
        }
    }

    @Test
    public void testSneakyThrowNoThrowsDeclaration() {
        // This method tests that we can throw checked exceptions without throws declaration
        // This is the main purpose of sneakyThrow - to bypass compile-time exception checking
        CustomCheckedException exception = new CustomCheckedException("No throws declaration needed");
        
        try {
            throwWithoutDeclaration(exception);
            assertThat(false).isTrue(); // Should not reach here
        } catch (Throwable e) {
            assertThat(e).isSameAs(exception);
        }
    }

    // Helper method that throws checked exception without declaring it
    private void throwWithoutDeclaration(Throwable throwable) {
        // No throws declaration, but sneakyThrow allows this
        Narcissus.sneakyThrow(throwable);
    }

    // Test classes for utility methods
    public static class TestClass {
        public boolean constructorCalled = false;
        public int value = 0;
        
        public TestClass() {
            constructorCalled = true;
        }
        
        public TestClass(int value) {
            constructorCalled = true;
            this.value = value;
        }
        
        public void setValue(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public interface TestInterface {
        void interfaceMethod();
    }
    
    public static abstract class TestAbstractClass {
        public abstract void abstractMethod();
    }
    
    public static class CustomCheckedException extends Exception {
        public CustomCheckedException(String message) {
            super(message);
        }
    }
}