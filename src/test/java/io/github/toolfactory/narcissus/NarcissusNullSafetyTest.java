package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for Narcissus null safety and error handling.
 * Critical for preventing JNI crashes with null inputs.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusNullSafetyTest {

    private TestNullSafetyClass testObject;
    private Field testField;
    private Method testMethod;

    @BeforeEach
    public void setUp() throws Exception {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
        
        testObject = new TestNullSafetyClass();
        Class<?> cls = TestNullSafetyClass.class;
        testField = cls.getDeclaredField("testValue");
        testMethod = cls.getDeclaredMethod("testMethod");
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
    public void testFindClassWithEmptyString() {
        try {
            Narcissus.findClass("");
            assertThat(false).isTrue(); // Should not reach here
        } catch (NoClassDefFoundError e) {
            // Expected - library throws NoClassDefFoundError for empty string
        }
    }

    @Test
    public void testFindClassWithInvalidClassName() {
        try {
            Narcissus.findClass("this.is.not.a.valid.Class");
            assertThat(false).isTrue(); // Should not reach here
        } catch (NoClassDefFoundError e) {
            // Expected - library throws NoClassDefFoundError for invalid class names
        }
    }

    @Test
    public void testGetIntFieldWithNullObject() throws NoSuchFieldException {
        // Test getIntField with null object - should not crash JVM
        try {
            Narcissus.getIntField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetLongFieldWithNullObject() throws NoSuchFieldException {
        // Test getLongField with null object - should not crash JVM
        try {
            Narcissus.getLongField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetShortFieldWithNullObject() throws NoSuchFieldException {
        // Test getShortField with null object - should not crash JVM
        try {
            Narcissus.getShortField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetCharFieldWithNullObject() throws NoSuchFieldException {
        // Test getCharField with null object - should not crash JVM
        try {
            Narcissus.getCharField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetByteFieldWithNullObject() throws NoSuchFieldException {
        // Test getByteField with null object - should not crash JVM
        try {
            Narcissus.getByteField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetBooleanFieldWithNullObject() throws NoSuchFieldException {
        // Test getBooleanField with null object - should not crash JVM
        try {
            Narcissus.getBooleanField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetFloatFieldWithNullObject() throws NoSuchFieldException {
        // Test getFloatField with null object - should not crash JVM
        try {
            Narcissus.getFloatField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetDoubleFieldWithNullObject() throws NoSuchFieldException {
        // Test getDoubleField with null object - should not crash JVM
        try {
            Narcissus.getDoubleField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetObjectFieldWithNullObject() throws NoSuchFieldException {
        // Test getObjectField with null object - should not crash JVM
        try {
            Narcissus.getObjectField(null, testField);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetIntFieldWithNullField() {
        // Test getIntField with null field - should not crash JVM
        try {
            Narcissus.getIntField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetLongFieldWithNullField() {
        // Test getLongField with null field - should not crash JVM
        try {
            Narcissus.getLongField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetShortFieldWithNullField() {
        // Test getShortField with null field - should not crash JVM
        try {
            Narcissus.getShortField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetCharFieldWithNullField() {
        // Test getCharField with null field - should not crash JVM
        try {
            Narcissus.getCharField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetByteFieldWithNullField() {
        // Test getByteField with null field - should not crash JVM
        try {
            Narcissus.getByteField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetBooleanFieldWithNullField() {
        // Test getBooleanField with null field - should not crash JVM
        try {
            Narcissus.getBooleanField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetFloatFieldWithNullField() {
        // Test getFloatField with null field - should not crash JVM
        try {
            Narcissus.getFloatField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetDoubleFieldWithNullField() {
        // Test getDoubleField with null field - should not crash JVM
        try {
            Narcissus.getDoubleField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetObjectFieldWithNullField() {
        // Test getObjectField with null field - should not crash JVM
        try {
            Narcissus.getObjectField(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetIntFieldWithNullObject() throws NoSuchFieldException {
        // Test setIntField with null object - should not crash JVM
        try {
            Narcissus.setIntField(null, testField, 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetLongFieldWithNullObject() throws NoSuchFieldException {
        // Test setLongField with null object - should not crash JVM
        try {
            Narcissus.setLongField(null, testField, 42L);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetShortFieldWithNullObject() throws NoSuchFieldException {
        // Test setShortField with null object - should not crash JVM
        try {
            Narcissus.setShortField(null, testField, (short) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetCharFieldWithNullObject() throws NoSuchFieldException {
        // Test setCharField with null object - should not crash JVM
        try {
            Narcissus.setCharField(null, testField, 'A');
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetByteFieldWithNullObject() throws NoSuchFieldException {
        // Test setByteField with null object - should not crash JVM
        try {
            Narcissus.setByteField(null, testField, (byte) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetBooleanFieldWithNullObject() throws NoSuchFieldException {
        // Test setBooleanField with null object - should not crash JVM
        try {
            Narcissus.setBooleanField(null, testField, true);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetFloatFieldWithNullObject() throws NoSuchFieldException {
        // Test setFloatField with null object - should not crash JVM
        try {
            Narcissus.setFloatField(null, testField, 3.14f);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetDoubleFieldWithNullObject() throws NoSuchFieldException {
        // Test setDoubleField with null object - should not crash JVM
        try {
            Narcissus.setDoubleField(null, testField, 3.14);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetObjectFieldWithNullObject() throws NoSuchFieldException {
        // Test setObjectField with null object - should not crash JVM
        try {
            Narcissus.setObjectField(null, testField, "test");
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetIntFieldWithNullField() {
        // Test setIntField with null field - should not crash JVM
        try {
            Narcissus.setIntField(testObject, null, 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetLongFieldWithNullField() {
        // Test setLongField with null field - should not crash JVM
        try {
            Narcissus.setLongField(testObject, null, 42L);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetShortFieldWithNullField() {
        // Test setShortField with null field - should not crash JVM
        try {
            Narcissus.setShortField(testObject, null, (short) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetCharFieldWithNullField() {
        // Test setCharField with null field - should not crash JVM
        try {
            Narcissus.setCharField(testObject, null, 'A');
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetByteFieldWithNullField() {
        // Test setByteField with null field - should not crash JVM
        try {
            Narcissus.setByteField(testObject, null, (byte) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetBooleanFieldWithNullField() {
        // Test setBooleanField with null field - should not crash JVM
        try {
            Narcissus.setBooleanField(testObject, null, true);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetFloatFieldWithNullField() {
        // Test setFloatField with null field - should not crash JVM
        try {
            Narcissus.setFloatField(testObject, null, 3.14f);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetDoubleFieldWithNullField() {
        // Test setDoubleField with null field - should not crash JVM
        try {
            Narcissus.setDoubleField(testObject, null, 3.14);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetObjectFieldWithNullField() {
        // Test setObjectField with null field - should not crash JVM
        try {
            Narcissus.setObjectField(testObject, null, "test");
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticIntFieldWithNullField() {
        // Test getStaticIntField with null field - should not crash JVM
        try {
            Narcissus.getStaticIntField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticLongFieldWithNullField() {
        // Test getStaticLongField with null field - should not crash JVM
        try {
            Narcissus.getStaticLongField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticShortFieldWithNullField() {
        // Test getStaticShortField with null field - should not crash JVM
        try {
            Narcissus.getStaticShortField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticCharFieldWithNullField() {
        // Test getStaticCharField with null field - should not crash JVM
        try {
            Narcissus.getStaticCharField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticByteFieldWithNullField() {
        // Test getStaticByteField with null field - should not crash JVM
        try {
            Narcissus.getStaticByteField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticBooleanFieldWithNullField() {
        // Test getStaticBooleanField with null field - should not crash JVM
        try {
            Narcissus.getStaticBooleanField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticFloatFieldWithNullField() {
        // Test getStaticFloatField with null field - should not crash JVM
        try {
            Narcissus.getStaticFloatField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticDoubleFieldWithNullField() {
        // Test getStaticDoubleField with null field - should not crash JVM
        try {
            Narcissus.getStaticDoubleField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testGetStaticObjectFieldWithNullField() {
        // Test getStaticObjectField with null field - should not crash JVM
        try {
            Narcissus.getStaticObjectField(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticIntFieldWithNullField() {
        // Test setStaticIntField with null field - should not crash JVM
        try {
            Narcissus.setStaticIntField(null, 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticLongFieldWithNullField() {
        // Test setStaticLongField with null field - should not crash JVM
        try {
            Narcissus.setStaticLongField(null, 42L);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticShortFieldWithNullField() {
        // Test setStaticShortField with null field - should not crash JVM
        try {
            Narcissus.setStaticShortField(null, (short) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticCharFieldWithNullField() {
        // Test setStaticCharField with null field - should not crash JVM
        try {
            Narcissus.setStaticCharField(null, 'A');
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticByteFieldWithNullField() {
        // Test setStaticByteField with null field - should not crash JVM
        try {
            Narcissus.setStaticByteField(null, (byte) 42);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticBooleanFieldWithNullField() {
        // Test setStaticBooleanField with null field - should not crash JVM
        try {
            Narcissus.setStaticBooleanField(null, true);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticFloatFieldWithNullField() {
        // Test setStaticFloatField with null field - should not crash JVM
        try {
            Narcissus.setStaticFloatField(null, 3.14f);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticDoubleFieldWithNullField() {
        // Test setStaticDoubleField with null field - should not crash JVM
        try {
            Narcissus.setStaticDoubleField(null, 3.14);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testSetStaticObjectFieldWithNullField() {
        // Test setStaticObjectField with null field - should not crash JVM
        try {
            Narcissus.setStaticObjectField(null, "test");
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeVoidMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeVoidMethod with null object - should not crash JVM
        try {
            Narcissus.invokeVoidMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeIntMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeIntMethod with null object - should not crash JVM
        try {
            Narcissus.invokeIntMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeLongMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeLongMethod with null object - should not crash JVM
        try {
            Narcissus.invokeLongMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeShortMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeShortMethod with null object - should not crash JVM
        try {
            Narcissus.invokeShortMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeCharMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeCharMethod with null object - should not crash JVM
        try {
            Narcissus.invokeCharMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeByteMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeByteMethod with null object - should not crash JVM
        try {
            Narcissus.invokeByteMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeBooleanMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeBooleanMethod with null object - should not crash JVM
        try {
            Narcissus.invokeBooleanMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeFloatMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeFloatMethod with null object - should not crash JVM
        try {
            Narcissus.invokeFloatMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeDoubleMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeDoubleMethod with null object - should not crash JVM
        try {
            Narcissus.invokeDoubleMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeObjectMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeObjectMethod with null object - should not crash JVM
        try {
            Narcissus.invokeObjectMethod(null, testMethod);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeVoidMethodWithNullMethod() {
        // Test invokeVoidMethod with null method - should not crash JVM
        try {
            Narcissus.invokeVoidMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeIntMethodWithNullMethod() {
        // Test invokeIntMethod with null method - should not crash JVM
        try {
            Narcissus.invokeIntMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeLongMethodWithNullMethod() {
        // Test invokeLongMethod with null method - should not crash JVM
        try {
            Narcissus.invokeLongMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeShortMethodWithNullMethod() {
        // Test invokeShortMethod with null method - should not crash JVM
        try {
            Narcissus.invokeShortMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeCharMethodWithNullMethod() {
        // Test invokeCharMethod with null method - should not crash JVM
        try {
            Narcissus.invokeCharMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeByteMethodWithNullMethod() {
        // Test invokeByteMethod with null method - should not crash JVM
        try {
            Narcissus.invokeByteMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeBooleanMethodWithNullMethod() {
        // Test invokeBooleanMethod with null method - should not crash JVM
        try {
            Narcissus.invokeBooleanMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeFloatMethodWithNullMethod() {
        // Test invokeFloatMethod with null method - should not crash JVM
        try {
            Narcissus.invokeFloatMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeDoubleMethodWithNullMethod() {
        // Test invokeDoubleMethod with null method - should not crash JVM
        try {
            Narcissus.invokeDoubleMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeObjectMethodWithNullMethod() {
        // Test invokeObjectMethod with null method - should not crash JVM
        try {
            Narcissus.invokeObjectMethod(testObject, null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticVoidMethodWithNullMethod() {
        // Test invokeStaticVoidMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticVoidMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticIntMethodWithNullMethod() {
        // Test invokeStaticIntMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticIntMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticLongMethodWithNullMethod() {
        // Test invokeStaticLongMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticLongMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticShortMethodWithNullMethod() {
        // Test invokeStaticShortMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticShortMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticCharMethodWithNullMethod() {
        // Test invokeStaticCharMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticCharMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticByteMethodWithNullMethod() {
        // Test invokeStaticByteMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticByteMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticBooleanMethodWithNullMethod() {
        // Test invokeStaticBooleanMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticBooleanMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticFloatMethodWithNullMethod() {
        // Test invokeStaticFloatMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticFloatMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticDoubleMethodWithNullMethod() {
        // Test invokeStaticDoubleMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticDoubleMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testInvokeStaticObjectMethodWithNullMethod() {
        // Test invokeStaticObjectMethod with null method - should not crash JVM
        try {
            Narcissus.invokeStaticObjectMethod(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testUtilityMethodsWithNullInputs() {
        // Test allocateInstance with null class - should not crash JVM
        try {
            Narcissus.allocateInstance(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
        
        // Test sneakyThrow with null throwable - should not crash JVM
        try {
            Narcissus.sneakyThrow(null);
        } catch (NullPointerException | IllegalArgumentException e) {
            // Expected exception, not a crash
        }
    }

    @Test
    public void testMassiveNullInputStress() {
        // Rapid-fire null input tests to check for race conditions or crashes
        for (int i = 0; i < 1000; i++) {
            try {
                Narcissus.findClass(null);
            } catch (NullPointerException | IllegalArgumentException e) {
                // Expected
            }
            
            try {
                Narcissus.getIntField(null, null);
            } catch (NullPointerException | IllegalArgumentException e) {
                // Expected
            }
            
            try {
                Narcissus.invokeVoidMethod(null, null);
            } catch (NullPointerException | IllegalArgumentException e) {
                // Expected
            }
            
            try {
                Narcissus.allocateInstance(null);
            } catch (NullPointerException | IllegalArgumentException e) {
                // Expected
            }
            
            try {
                Narcissus.sneakyThrow(null);
            } catch (NullPointerException | IllegalArgumentException e) {
                // Expected
            }
        }
    }

    // Test class for null safety testing
    public static class TestNullSafetyClass {
        public int testValue;
        
        public void testMethod() {
        }
    }
}