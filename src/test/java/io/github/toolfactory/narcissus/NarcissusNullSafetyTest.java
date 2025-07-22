package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.findClass(null);
            }
        });
        assertThat(exception.getMessage()).isEqualTo("Class name cannot be null");
    }

    @Test
    public void testFindClassWithEmptyString() {
        assertThrows(NoClassDefFoundError.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.findClass("");
            }
        });
    }

    @Test
    public void testFindClassWithInvalidClassName() {
        assertThrows(NoClassDefFoundError.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.findClass("this.is.not.a.valid.Class");
            }
        });
    }

    @Test
    public void testGetIntFieldWithNullObject() throws NoSuchFieldException {
        // Test getIntField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getIntField(null, testField);
            }
        });
    }

    @Test
    public void testGetLongFieldWithNullObject() throws NoSuchFieldException {
        // Test getLongField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getLongField(null, testField);
            }
        });
    }

    @Test
    public void testGetShortFieldWithNullObject() throws NoSuchFieldException {
        // Test getShortField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getShortField(null, testField);
            }
        });
    }

    @Test
    public void testGetCharFieldWithNullObject() throws NoSuchFieldException {
        // Test getCharField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getCharField(null, testField);
            }
        });
    }

    @Test
    public void testGetByteFieldWithNullObject() throws NoSuchFieldException {
        // Test getByteField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getByteField(null, testField);
            }
        });
    }

    @Test
    public void testGetBooleanFieldWithNullObject() throws NoSuchFieldException {
        // Test getBooleanField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getBooleanField(null, testField);
            }
        });
    }

    @Test
    public void testGetFloatFieldWithNullObject() throws NoSuchFieldException {
        // Test getFloatField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getFloatField(null, testField);
            }
        });
    }

    @Test
    public void testGetDoubleFieldWithNullObject() throws NoSuchFieldException {
        // Test getDoubleField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getDoubleField(null, testField);
            }
        });
    }

    @Test
    public void testGetObjectFieldWithNullObject() throws NoSuchFieldException {
        // Test getObjectField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getObjectField(null, testField);
            }
        });
    }

    @Test
    public void testGetIntFieldWithNullField() {
        // Test getIntField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getIntField(testObject, null);
            }
        });
    }

    @Test
    public void testGetLongFieldWithNullField() {
        // Test getLongField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getLongField(testObject, null);
            }
        });
    }

    @Test
    public void testGetShortFieldWithNullField() {
        // Test getShortField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getShortField(testObject, null);
            }
        });
    }

    @Test
    public void testGetCharFieldWithNullField() {
        // Test getCharField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getCharField(testObject, null);
            }
        });
    }

    @Test
    public void testGetByteFieldWithNullField() {
        // Test getByteField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getByteField(testObject, null);
            }
        });
    }

    @Test
    public void testGetBooleanFieldWithNullField() {
        // Test getBooleanField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getBooleanField(testObject, null);
            }
        });
    }

    @Test
    public void testGetFloatFieldWithNullField() {
        // Test getFloatField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getFloatField(testObject, null);
            }
        });
    }

    @Test
    public void testGetDoubleFieldWithNullField() {
        // Test getDoubleField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getDoubleField(testObject, null);
            }
        });
    }

    @Test
    public void testGetObjectFieldWithNullField() {
        // Test getObjectField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getObjectField(testObject, null);
            }
        });
    }

    @Test
    public void testSetIntFieldWithNullObject() throws NoSuchFieldException {
        // Test setIntField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setIntField(null, testField, 42);
            }
        });
    }

    @Test
    public void testSetLongFieldWithNullObject() throws NoSuchFieldException {
        // Test setLongField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setLongField(null, testField, 42L);
            }
        });
    }

    @Test
    public void testSetShortFieldWithNullObject() throws NoSuchFieldException {
        // Test setShortField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setShortField(null, testField, (short) 42);
            }
        });
    }

    @Test
    public void testSetCharFieldWithNullObject() throws NoSuchFieldException {
        // Test setCharField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setCharField(null, testField, 'A');
            }
        });
    }

    @Test
    public void testSetByteFieldWithNullObject() throws NoSuchFieldException {
        // Test setByteField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setByteField(null, testField, (byte) 42);
            }
        });
    }

    @Test
    public void testSetBooleanFieldWithNullObject() throws NoSuchFieldException {
        // Test setBooleanField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setBooleanField(null, testField, true);
            }
        });
    }

    @Test
    public void testSetFloatFieldWithNullObject() throws NoSuchFieldException {
        // Test setFloatField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setFloatField(null, testField, 3.14f);
            }
        });
    }

    @Test
    public void testSetDoubleFieldWithNullObject() throws NoSuchFieldException {
        // Test setDoubleField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setDoubleField(null, testField, 3.14);
            }
        });
    }

    @Test
    public void testSetObjectFieldWithNullObject() throws NoSuchFieldException {
        // Test setObjectField with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setObjectField(null, testField, "test");
            }
        });
    }

    @Test
    public void testSetIntFieldWithNullField() {
        // Test setIntField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setIntField(testObject, null, 42);
            }
        });
    }

    @Test
    public void testSetLongFieldWithNullField() {
        // Test setLongField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setLongField(testObject, null, 42L);
            }
        });
    }

    @Test
    public void testSetShortFieldWithNullField() {
        // Test setShortField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setShortField(testObject, null, (short) 42);
            }
        });
    }

    @Test
    public void testSetCharFieldWithNullField() {
        // Test setCharField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setCharField(testObject, null, 'A');
            }
        });
    }

    @Test
    public void testSetByteFieldWithNullField() {
        // Test setByteField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setByteField(testObject, null, (byte) 42);
            }
        });
    }

    @Test
    public void testSetBooleanFieldWithNullField() {
        // Test setBooleanField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setBooleanField(testObject, null, true);
            }
        });
    }

    @Test
    public void testSetFloatFieldWithNullField() {
        // Test setFloatField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setFloatField(testObject, null, 3.14f);
            }
        });
    }

    @Test
    public void testSetDoubleFieldWithNullField() {
        // Test setDoubleField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setDoubleField(testObject, null, 3.14);
            }
        });
    }

    @Test
    public void testSetObjectFieldWithNullField() {
        // Test setObjectField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setObjectField(testObject, null, "test");
            }
        });
    }

    @Test
    public void testGetStaticIntFieldWithNullField() {
        // Test getStaticIntField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticIntField(null);
            }
        });
    }

    @Test
    public void testGetStaticLongFieldWithNullField() {
        // Test getStaticLongField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticLongField(null);
            }
        });
    }

    @Test
    public void testGetStaticShortFieldWithNullField() {
        // Test getStaticShortField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticShortField(null);
            }
        });
    }

    @Test
    public void testGetStaticCharFieldWithNullField() {
        // Test getStaticCharField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticCharField(null);
            }
        });
    }

    @Test
    public void testGetStaticByteFieldWithNullField() {
        // Test getStaticByteField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticByteField(null);
            }
        });
    }

    @Test
    public void testGetStaticBooleanFieldWithNullField() {
        // Test getStaticBooleanField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticBooleanField(null);
            }
        });
    }

    @Test
    public void testGetStaticFloatFieldWithNullField() {
        // Test getStaticFloatField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticFloatField(null);
            }
        });
    }

    @Test
    public void testGetStaticDoubleFieldWithNullField() {
        // Test getStaticDoubleField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticDoubleField(null);
            }
        });
    }

    @Test
    public void testGetStaticObjectFieldWithNullField() {
        // Test getStaticObjectField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.getStaticObjectField(null);
            }
        });
    }

    @Test
    public void testSetStaticIntFieldWithNullField() {
        // Test setStaticIntField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticIntField(null, 42);
            }
        });
    }

    @Test
    public void testSetStaticLongFieldWithNullField() {
        // Test setStaticLongField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticLongField(null, 42L);
            }
        });
    }

    @Test
    public void testSetStaticShortFieldWithNullField() {
        // Test setStaticShortField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticShortField(null, (short) 42);
            }
        });
    }

    @Test
    public void testSetStaticCharFieldWithNullField() {
        // Test setStaticCharField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticCharField(null, 'A');
            }
        });
    }

    @Test
    public void testSetStaticByteFieldWithNullField() {
        // Test setStaticByteField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticByteField(null, (byte) 42);
            }
        });
    }

    @Test
    public void testSetStaticBooleanFieldWithNullField() {
        // Test setStaticBooleanField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticBooleanField(null, true);
            }
        });
    }

    @Test
    public void testSetStaticFloatFieldWithNullField() {
        // Test setStaticFloatField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticFloatField(null, 3.14f);
            }
        });
    }

    @Test
    public void testSetStaticDoubleFieldWithNullField() {
        // Test setStaticDoubleField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticDoubleField(null, 3.14);
            }
        });
    }

    @Test
    public void testSetStaticObjectFieldWithNullField() {
        // Test setStaticObjectField with null field - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.setStaticObjectField(null, "test");
            }
        });
    }

    @Test
    public void testInvokeVoidMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeVoidMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeVoidMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeIntMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeIntMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeIntMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeLongMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeLongMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeLongMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeShortMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeShortMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeShortMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeCharMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeCharMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeCharMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeByteMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeByteMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeByteMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeBooleanMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeBooleanMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeBooleanMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeFloatMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeFloatMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeFloatMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeDoubleMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeDoubleMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeDoubleMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeObjectMethodWithNullObject() throws NoSuchMethodException {
        // Test invokeObjectMethod with null object - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeObjectMethod(null, testMethod);
            }
        });
    }

    @Test
    public void testInvokeVoidMethodWithNullMethod() {
        // Test invokeVoidMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeVoidMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeIntMethodWithNullMethod() {
        // Test invokeIntMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeIntMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeLongMethodWithNullMethod() {
        // Test invokeLongMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeLongMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeShortMethodWithNullMethod() {
        // Test invokeShortMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeShortMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeCharMethodWithNullMethod() {
        // Test invokeCharMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeCharMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeByteMethodWithNullMethod() {
        // Test invokeByteMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeByteMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeBooleanMethodWithNullMethod() {
        // Test invokeBooleanMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeBooleanMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeFloatMethodWithNullMethod() {
        // Test invokeFloatMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeFloatMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeDoubleMethodWithNullMethod() {
        // Test invokeDoubleMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeDoubleMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeObjectMethodWithNullMethod() {
        // Test invokeObjectMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeObjectMethod(testObject, null);
            }
        });
    }

    @Test
    public void testInvokeStaticVoidMethodWithNullMethod() {
        // Test invokeStaticVoidMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticVoidMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticIntMethodWithNullMethod() {
        // Test invokeStaticIntMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticIntMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticLongMethodWithNullMethod() {
        // Test invokeStaticLongMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticLongMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticShortMethodWithNullMethod() {
        // Test invokeStaticShortMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticShortMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticCharMethodWithNullMethod() {
        // Test invokeStaticCharMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticCharMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticByteMethodWithNullMethod() {
        // Test invokeStaticByteMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticByteMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticBooleanMethodWithNullMethod() {
        // Test invokeStaticBooleanMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticBooleanMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticFloatMethodWithNullMethod() {
        // Test invokeStaticFloatMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticFloatMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticDoubleMethodWithNullMethod() {
        // Test invokeStaticDoubleMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticDoubleMethod(null);
            }
        });
    }

    @Test
    public void testInvokeStaticObjectMethodWithNullMethod() {
        // Test invokeStaticObjectMethod with null method - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.invokeStaticObjectMethod(null);
            }
        });
    }

    @Test
    public void testUtilityMethodsWithNullInputs() {
        // Test allocateInstance with null class - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.allocateInstance(null);
            }
        });
        
        // Test sneakyThrow with null throwable - should not crash JVM
        assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Narcissus.sneakyThrow(null);
            }
        });
    }

    @Test
    public void testMassiveNullInputStress() {
        // Rapid-fire null input tests to check for race conditions or crashes
        for (int i = 0; i < 1000; i++) {
            assertThrows(RuntimeException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    Narcissus.findClass(null);
                }
            });
            
            assertThrows(RuntimeException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    Narcissus.getIntField(null, null);
                }
            });
            
            assertThrows(RuntimeException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    Narcissus.invokeVoidMethod(null, null);
                }
            });
            
            assertThrows(RuntimeException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    Narcissus.allocateInstance(null);
                }
            });
            
            assertThrows(RuntimeException.class, new Executable() {
                @Override
                public void execute() throws Throwable {
                    Narcissus.sneakyThrow(null);
                }
            });
        }
    }

    // Test class for null safety testing
    public static class TestNullSafetyClass {
        public int testValue;
        
        public void testMethod() {
        }
    }
}