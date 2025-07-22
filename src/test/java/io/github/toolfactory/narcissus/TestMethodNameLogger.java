package io.github.toolfactory.narcissus;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that logs the name of each test method when it starts.
 */
public class TestMethodNameLogger implements BeforeEachCallback {
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String className = context.getTestClass().get().getSimpleName();
        String methodName = context.getTestMethod().get().getName();
        System.out.println(">>> RUNNING TEST: " + className + "." + methodName);
        System.out.flush(); // Ensure immediate output (so we can see failure point if JVM crashes)
    }
}