/*
 * This file is part of Narcissus.
 *
 * Hosted at: https://github.com/toolfactory/narcissus
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
#include <jni.h>
#include <stdbool.h>
#include <stdio.h>

// The JNI version required by this library. JNI 1.6 is required for ExceptionCheck(), EnsureLocalCapacity()
// and DeleteLocalRef(), and is supported by every JVM that this library supports (Java 7 and above).
#define NARCISSUS_JNI_VERSION JNI_VERSION_1_6

// Modifier bits, from the JVM specification (java.lang.reflect.Modifier)
#define MODIFIER_STATIC    0x0008
#define MODIFIER_INTERFACE 0x0200
#define MODIFIER_ABSTRACT  0x0400

// The maximum number of parameters a method may declare. The JVM specification limits a method to 255
// argument slots, so no loadable method can have more parameters than this. Method invocation buffers are
// sized by this constant, so that no buffer is ever sized from a caller-supplied argument count.
#define MAX_METHOD_PARAMS 255

// The largest exception message assembled by this library
#define MAX_MSG_LEN 128

// The number of local references that a method invocation needs over and above one per parameter (for the
// boxed varargs array, the receiver's class, and the like)
#define LOCAL_REF_SLACK 16

// -----------------------------------------------------------------------------------------------------------------

// Returns true if an exception is pending in the current thread. Uses ExceptionCheck() rather than
// ExceptionOccurred(), because ExceptionOccurred() returns a new local reference on every call, and this
// function is called several times per JNI entry point.
static bool thrown(JNIEnv* env) {
    return (*env)->ExceptionCheck(env);
}

// -----------------------------------------------------------------------------------------------------------------

// Prelookup of frequently-used classes and methods:

static jclass Class_class;
static jmethodID Class_isArray_methodID;
static jmethodID Class_getComponentType_methodID;
static jmethodID Class_isPrimitive_methodID;
static jmethodID Class_getModifiers_methodID;

static jclass void_class;

static jclass Integer_class;
static jclass int_class;
static jmethodID Integer_intValue_methodID;

static jclass Long_class;
static jclass long_class;
static jmethodID Long_longValue_methodID;

static jclass Short_class;
static jclass short_class;
static jmethodID Short_shortValue_methodID;

static jclass Character_class;
static jclass char_class;
static jmethodID Character_charValue_methodID;

static jclass Boolean_class;
static jclass boolean_class;
static jmethodID Boolean_booleanValue_methodID;

static jclass Byte_class;
static jclass byte_class;
static jmethodID Byte_byteValue_methodID;

static jclass Float_class;
static jclass float_class;
static jmethodID Float_floatValue_methodID;

static jclass Double_class;
static jclass double_class;
static jmethodID Double_doubleValue_methodID;

static jmethodID Method_getDeclaringClass_methodID;
static jmethodID Method_getModifiers_methodID;
static jmethodID Method_getParameterTypes_methodID;
static jmethodID Method_isVarArgs_methodID;
static jmethodID Method_getReturnType_methodID;

static jmethodID Field_getDeclaringClass_methodID;
static jmethodID Field_getModifiers_methodID;
static jmethodID Field_getType_methodID;

// -----------------------------------------------------------------------------------------------------------------

// Look up a class by name, and store a new global reference to it in *out. Returns false if the class could not
// be found, leaving the pending exception in place for the caller to clear.
static bool initGlobalClassRef(JNIEnv* env, const char* class_name, jclass* out) {
    jclass local_ref = (*env)->FindClass(env, class_name);
    if (!local_ref || thrown(env)) { return false; }
    *out = (jclass) (*env)->NewGlobalRef(env, local_ref);
    (*env)->DeleteLocalRef(env, local_ref);
    return *out != NULL;
}

// Read the static TYPE field of a boxed primitive type class (e.g. Integer.TYPE), and store a new global
// reference to the resulting primitive class (e.g. int.class) in *out. Returns false on failure, leaving any
// pending exception in place for the caller to clear.
static bool initGlobalPrimitiveClassRef(JNIEnv* env, jclass boxed_class, jclass* out) {
    jfieldID type_fieldID = (*env)->GetStaticFieldID(env, boxed_class, "TYPE", "Ljava/lang/Class;");
    if (!type_fieldID || thrown(env)) { return false; }
    jobject local_ref = (*env)->GetStaticObjectField(env, boxed_class, type_fieldID);
    if (!local_ref || thrown(env)) { return false; }
    *out = (jclass) (*env)->NewGlobalRef(env, local_ref);
    (*env)->DeleteLocalRef(env, local_ref);
    return *out != NULL;
}

// Look up an instance method, and store its method id in *out. Returns false on failure, leaving any pending
// exception in place for the caller to clear.
static bool initMethodID(JNIEnv* env, jclass cls, const char* method_name, const char* sig, jmethodID* out) {
    *out = (*env)->GetMethodID(env, cls, method_name, sig);
    return *out != NULL && !thrown(env);
}

// Look up a boxed primitive type class, the corresponding primitive type class, and the boxed type's
// xxxValue() method. Returns false on failure, leaving any pending exception in place for the caller to clear.
static bool initBoxedType(JNIEnv* env, const char* boxed_class_name, jclass* boxed_class_out,
        jclass* primitive_class_out, const char* value_method_name, const char* value_method_sig,
        jmethodID* value_methodID_out) {
    return initGlobalClassRef(env, boxed_class_name, boxed_class_out)
            && initGlobalPrimitiveClassRef(env, *boxed_class_out, primitive_class_out)
            && initMethodID(env, *boxed_class_out, value_method_name, value_method_sig, value_methodID_out);
}

// Pre-look-up classes and methods for primitive types and Class, and allocate new global refs for them so they
// can be used across JNI calls
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, NARCISSUS_JNI_VERSION) != JNI_OK || env == NULL) {
        return -1;
    }

    jclass Void_class = NULL;
    jclass Method_class = NULL;
    jclass Field_class = NULL;
    bool ok = initGlobalClassRef(env, "java/lang/Class", &Class_class)
            && initMethodID(env, Class_class, "isArray", "()Z", &Class_isArray_methodID)
            && initMethodID(env, Class_class, "getComponentType", "()Ljava/lang/Class;",
                    &Class_getComponentType_methodID)
            && initMethodID(env, Class_class, "isPrimitive", "()Z", &Class_isPrimitive_methodID)
            && initMethodID(env, Class_class, "getModifiers", "()I", &Class_getModifiers_methodID)

            && initGlobalClassRef(env, "java/lang/Void", &Void_class)
            && initGlobalPrimitiveClassRef(env, Void_class, &void_class)

            && initBoxedType(env, "java/lang/Integer", &Integer_class, &int_class, "intValue", "()I",
                    &Integer_intValue_methodID)
            && initBoxedType(env, "java/lang/Long", &Long_class, &long_class, "longValue", "()J",
                    &Long_longValue_methodID)
            && initBoxedType(env, "java/lang/Short", &Short_class, &short_class, "shortValue", "()S",
                    &Short_shortValue_methodID)
            && initBoxedType(env, "java/lang/Character", &Character_class, &char_class, "charValue", "()C",
                    &Character_charValue_methodID)
            && initBoxedType(env, "java/lang/Boolean", &Boolean_class, &boolean_class, "booleanValue", "()Z",
                    &Boolean_booleanValue_methodID)
            && initBoxedType(env, "java/lang/Byte", &Byte_class, &byte_class, "byteValue", "()B",
                    &Byte_byteValue_methodID)
            && initBoxedType(env, "java/lang/Float", &Float_class, &float_class, "floatValue", "()F",
                    &Float_floatValue_methodID)
            && initBoxedType(env, "java/lang/Double", &Double_class, &double_class, "doubleValue", "()D",
                    &Double_doubleValue_methodID)

            && initGlobalClassRef(env, "java/lang/reflect/Method", &Method_class)
            && initMethodID(env, Method_class, "getDeclaringClass", "()Ljava/lang/Class;",
                    &Method_getDeclaringClass_methodID)
            && initMethodID(env, Method_class, "getModifiers", "()I", &Method_getModifiers_methodID)
            && initMethodID(env, Method_class, "getParameterTypes", "()[Ljava/lang/Class;",
                    &Method_getParameterTypes_methodID)
            && initMethodID(env, Method_class, "isVarArgs", "()Z", &Method_isVarArgs_methodID)
            && initMethodID(env, Method_class, "getReturnType", "()Ljava/lang/Class;",
                    &Method_getReturnType_methodID)

            && initGlobalClassRef(env, "java/lang/reflect/Field", &Field_class)
            && initMethodID(env, Field_class, "getDeclaringClass", "()Ljava/lang/Class;",
                    &Field_getDeclaringClass_methodID)
            && initMethodID(env, Field_class, "getModifiers", "()I", &Field_getModifiers_methodID)
            && initMethodID(env, Field_class, "getType", "()Ljava/lang/Class;", &Field_getType_methodID);

    // Void, Method and Field are only needed during initialization
    if (Void_class) { (*env)->DeleteGlobalRef(env, Void_class); }
    if (Method_class) { (*env)->DeleteGlobalRef(env, Method_class); }
    if (Field_class) { (*env)->DeleteGlobalRef(env, Field_class); }

    if (!ok) {
        // Returning from JNI_OnLoad with an exception pending is not permitted -- the JVM reports the failed
        // return value as an UnsatisfiedLinkError instead
        if (thrown(env)) { (*env)->ExceptionClear(env); }
        return -1;
    }
    return NARCISSUS_JNI_VERSION;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, NARCISSUS_JNI_VERSION) != JNI_OK || env == NULL) {
        // The calling thread is not attached to the JVM, so the global refs cannot be deleted. They are
        // released anyway when the JVM terminates.
        return;
    }

    jclass* global_refs[] = { &Class_class, &void_class, &Integer_class, &int_class, &Long_class, &long_class,
        &Short_class, &short_class, &Character_class, &char_class, &Boolean_class, &boolean_class, &Byte_class,
        &byte_class, &Float_class, &float_class, &Double_class, &double_class };
    for (size_t i = 0; i < sizeof(global_refs) / sizeof(global_refs[0]); i++) {
        if (*global_refs[i]) {
            (*env)->DeleteGlobalRef(env, *global_refs[i]);
            *global_refs[i] = NULL;
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------

// Exception throwing

static void throwException(JNIEnv* env, const char* exception_class_name, const char* msg) {
    jclass cls = (*env)->FindClass(env, exception_class_name);
    if (cls) {
        (*env)->ThrowNew(env, cls, msg);
        (*env)->DeleteLocalRef(env, cls);
    }
}

static void throwIllegalArgumentException(JNIEnv* env, const char* msg) {
    throwException(env, "java/lang/IllegalArgumentException", msg);
}

static void throwNullPointerException(JNIEnv* env, const char* msg) {
    throwException(env, "java/lang/NullPointerException", msg);
}

static void throwInstantiationException(JNIEnv* env, const char* msg) {
    throwException(env, "java/lang/InstantiationException", msg);
}

// -----------------------------------------------------------------------------------------------------------------

// Primitive types

// The primitive types, in a fixed order, used to index the tables below. PRIM_NONE stands for "not a primitive
// type", i.e. a reference type.
typedef enum {
    PRIM_NONE = 0,
    PRIM_BOOLEAN,
    PRIM_BYTE,
    PRIM_CHAR,
    PRIM_SHORT,
    PRIM_INT,
    PRIM_LONG,
    PRIM_FLOAT,
    PRIM_DOUBLE,
    NUM_PRIM_KINDS
} prim_kind;

// The name of the boxed type corresponding to each prim_kind, for use in exception messages
static const char* BOXED_TYPE_NAME[NUM_PRIM_KINDS] = { "", "Boolean", "Byte", "Character", "Short", "Integer",
    "Long", "Float", "Double" };

// For each prim_kind, the set of prim_kinds that a value of that type can be converted to by widening primitive
// conversion (JLS 5.1.2), including the identity conversion. Note that byte does not widen to char, and char
// does not widen to short or byte.
static const int WIDENS_TO[NUM_PRIM_KINDS] = {
    /* PRIM_NONE    */ 0,
    /* PRIM_BOOLEAN */ (1 << PRIM_BOOLEAN),
    /* PRIM_BYTE    */ (1 << PRIM_BYTE) | (1 << PRIM_SHORT) | (1 << PRIM_INT) | (1 << PRIM_LONG)
                       | (1 << PRIM_FLOAT) | (1 << PRIM_DOUBLE),
    /* PRIM_CHAR    */ (1 << PRIM_CHAR) | (1 << PRIM_INT) | (1 << PRIM_LONG) | (1 << PRIM_FLOAT)
                       | (1 << PRIM_DOUBLE),
    /* PRIM_SHORT   */ (1 << PRIM_SHORT) | (1 << PRIM_INT) | (1 << PRIM_LONG) | (1 << PRIM_FLOAT)
                       | (1 << PRIM_DOUBLE),
    /* PRIM_INT     */ (1 << PRIM_INT) | (1 << PRIM_LONG) | (1 << PRIM_FLOAT) | (1 << PRIM_DOUBLE),
    /* PRIM_LONG    */ (1 << PRIM_LONG) | (1 << PRIM_FLOAT) | (1 << PRIM_DOUBLE),
    /* PRIM_FLOAT   */ (1 << PRIM_FLOAT) | (1 << PRIM_DOUBLE),
    /* PRIM_DOUBLE  */ (1 << PRIM_DOUBLE)
};

// Map a primitive type class (e.g. int.class) to its prim_kind, or PRIM_NONE if cls is a reference type
static prim_kind primKindOfClass(JNIEnv* env, jclass cls) {
    if (cls == NULL) { return PRIM_NONE; }
    if ((*env)->IsSameObject(env, cls, int_class)) { return PRIM_INT; }
    if ((*env)->IsSameObject(env, cls, long_class)) { return PRIM_LONG; }
    if ((*env)->IsSameObject(env, cls, short_class)) { return PRIM_SHORT; }
    if ((*env)->IsSameObject(env, cls, char_class)) { return PRIM_CHAR; }
    if ((*env)->IsSameObject(env, cls, boolean_class)) { return PRIM_BOOLEAN; }
    if ((*env)->IsSameObject(env, cls, byte_class)) { return PRIM_BYTE; }
    if ((*env)->IsSameObject(env, cls, float_class)) { return PRIM_FLOAT; }
    if ((*env)->IsSameObject(env, cls, double_class)) { return PRIM_DOUBLE; }
    return PRIM_NONE;
}

// Map a boxed primitive type class (e.g. Integer.class) to the prim_kind it boxes, or PRIM_NONE if cls is not a
// boxed primitive type
static prim_kind boxedKindOfClass(JNIEnv* env, jclass cls) {
    if (cls == NULL) { return PRIM_NONE; }
    if ((*env)->IsSameObject(env, cls, Integer_class)) { return PRIM_INT; }
    if ((*env)->IsSameObject(env, cls, Long_class)) { return PRIM_LONG; }
    if ((*env)->IsSameObject(env, cls, Short_class)) { return PRIM_SHORT; }
    if ((*env)->IsSameObject(env, cls, Character_class)) { return PRIM_CHAR; }
    if ((*env)->IsSameObject(env, cls, Boolean_class)) { return PRIM_BOOLEAN; }
    if ((*env)->IsSameObject(env, cls, Byte_class)) { return PRIM_BYTE; }
    if ((*env)->IsSameObject(env, cls, Float_class)) { return PRIM_FLOAT; }
    if ((*env)->IsSameObject(env, cls, Double_class)) { return PRIM_DOUBLE; }
    return PRIM_NONE;
}

// -----------------------------------------------------------------------------------------------------------------

// Argument checks. Each of these returns false with a Java exception pending if the check failed.

static bool argIsNull(JNIEnv* env, jobject obj) {
    if (!obj) {
        throwNullPointerException(env, "Argument cannot be null");
        return true;
    }
    return false;
}

static bool checkFieldStaticModifier(JNIEnv* env, jobject field, bool expectStatic) {
    jint mods = (*env)->CallIntMethod(env, field, Field_getModifiers_methodID);
    if (thrown(env)) { return false; }
    if (((mods & MODIFIER_STATIC) != 0) != expectStatic) {
        throwIllegalArgumentException(env, expectStatic ? "Expected static field" : "Expected non-static field");
        return false;
    }
    return true;
}

static bool checkMethodStaticModifier(JNIEnv* env, jobject method, bool expectStatic) {
    jint mods = (*env)->CallIntMethod(env, method, Method_getModifiers_methodID);
    if (thrown(env)) { return false; }
    if (((mods & MODIFIER_STATIC) != 0) != expectStatic) {
        throwIllegalArgumentException(env, expectStatic ? "Expected static method" : "Expected non-static method");
        return false;
    }
    return true;
}

static bool checkFieldReceiver(JNIEnv* env, jobject obj, jobject field) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    if (thrown(env)) { return false; }
    jclass declaringClass = (jclass) (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { (*env)->DeleteLocalRef(env, cls); return false; }
    jboolean assignable = (*env)->IsAssignableFrom(env, cls, declaringClass);
    (*env)->DeleteLocalRef(env, cls);
    (*env)->DeleteLocalRef(env, declaringClass);
    if (!assignable) {
        throwIllegalArgumentException(env, "Object class does not match declaring class of field");
        return false;
    }
    return true;
}

static bool checkMethodReceiver(JNIEnv* env, jobject obj, jobject method) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    if (thrown(env)) { return false; }
    jclass declaringClass = (jclass) (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { (*env)->DeleteLocalRef(env, cls); return false; }
    jboolean assignable = (*env)->IsAssignableFrom(env, cls, declaringClass);
    (*env)->DeleteLocalRef(env, cls);
    (*env)->DeleteLocalRef(env, declaringClass);
    if (!assignable) {
        throwIllegalArgumentException(env, "Object class does not match declaring class of method");
        return false;
    }
    return true;
}

static bool checkPrimitiveMethodReturnType(JNIEnv* env, jobject method, jclass primitive_class) {
    jclass return_type = (jclass) (*env)->CallObjectMethod(env, method, Method_getReturnType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_correct_return_type = (*env)->IsSameObject(env, return_type, primitive_class);
    (*env)->DeleteLocalRef(env, return_type);
    if (!is_correct_return_type) {
        throwIllegalArgumentException(env, "Return type of method does not match primitive method invocation type");
        return false;
    }
    return true;
}

static bool checkMethodReturnTypeNotVoidOrPrimitive(JNIEnv* env, jobject method) {
    jclass return_type = (jclass) (*env)->CallObjectMethod(env, method, Method_getReturnType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_void_return_type = (*env)->IsSameObject(env, return_type, void_class);
    prim_kind return_kind = primKindOfClass(env, return_type);
    (*env)->DeleteLocalRef(env, return_type);
    if (is_void_return_type) {
        throwIllegalArgumentException(env,
                "Return type of method is void, but tried to invoke as method with Object return type");
        return false;
    }
    if (return_kind != PRIM_NONE) {
        throwIllegalArgumentException(env,
                "Return type of method is of primitive type, but tried to invoke as method with Object return type");
        return false;
    }
    return true;
}

// Check that the declared type of a field is exactly the given primitive type. Without this check, a typed
// accessor such as getIntField() called on a field of a different type reads or writes the wrong number of
// bytes at the wrong offset, which corrupts the heap or crashes the JVM.
static bool checkFieldPrimitiveType(JNIEnv* env, jobject field, jclass primitive_class) {
    jclass field_type = (jclass) (*env)->CallObjectMethod(env, field, Field_getType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_correct_type = (*env)->IsSameObject(env, field_type, primitive_class);
    (*env)->DeleteLocalRef(env, field_type);
    if (!is_correct_type) {
        throwIllegalArgumentException(env, "Field type does not match primitive field accessor type");
        return false;
    }
    return true;
}

// Check that the declared type of a field is a reference type, for the Object-typed field accessors
static bool checkFieldTypeIsReference(JNIEnv* env, jobject field) {
    jclass field_type = (jclass) (*env)->CallObjectMethod(env, field, Field_getType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_primitive = (*env)->CallBooleanMethod(env, field_type, Class_isPrimitive_methodID);
    (*env)->DeleteLocalRef(env, field_type);
    if (thrown(env)) { return false; }
    if (is_primitive) {
        throwIllegalArgumentException(env,
                "Field type is of primitive type, but tried to access as field with Object type");
        return false;
    }
    return true;
}

// Check that a value is assignable to a field of reference type. A null value is assignable to any field of
// reference type -- checkFieldTypeIsReference() has already rejected fields of primitive type.
static bool checkFieldValType(JNIEnv* env, jobject field, jobject val) {
    if (val != NULL) {
        jclass valCls = (*env)->GetObjectClass(env, val);
        if (thrown(env)) { return false; }
        jclass fieldType = (jclass) (*env)->CallObjectMethod(env, field, Field_getType_methodID);
        if (thrown(env)) { (*env)->DeleteLocalRef(env, valCls); return false; }
        jboolean assignable = (*env)->IsAssignableFrom(env, valCls, fieldType);
        (*env)->DeleteLocalRef(env, valCls);
        (*env)->DeleteLocalRef(env, fieldType);
        if (!assignable) {
            throwIllegalArgumentException(env, "Value cannot be assigned to a field of this type");
            return false;
        }
    }
    return true;
}

// -----------------------------------------------------------------------------------------------------------------

// Unboxing of method invocation arguments

// Unbox a single argument into a jvalue of the given primitive type, applying widening primitive conversion
// (JLS 5.1.2) if the boxed type of the argument is narrower than the parameter type. Returns false with a Java
// exception pending if the argument is null, or is not of a type that can be converted to the parameter type.
static bool unboxArg(JNIEnv* env, jobject arg, prim_kind target_kind, jvalue* out) {
    char msg[MAX_MSG_LEN];
    if (arg == NULL) {
        snprintf(msg, sizeof(msg), "Tried to unbox a null argument; expected %s", BOXED_TYPE_NAME[target_kind]);
        throwIllegalArgumentException(env, msg);
        return false;
    }
    jclass arg_type = (*env)->GetObjectClass(env, arg);
    if (thrown(env)) { return false; }
    prim_kind arg_kind = boxedKindOfClass(env, arg_type);
    (*env)->DeleteLocalRef(env, arg_type);
    if (arg_kind == PRIM_NONE || (WIDENS_TO[arg_kind] & (1 << target_kind)) == 0) {
        snprintf(msg, sizeof(msg), "Tried to unbox arg of wrong type; expected %s", BOXED_TYPE_NAME[target_kind]);
        throwIllegalArgumentException(env, msg);
        return false;
    }

    // Read the value out of the box. Integral types (and boolean) are read into a jlong, and floating point
    // types into a jdouble -- the widening table guarantees that the target type can represent the result.
    jlong long_val = 0;
    jdouble double_val = 0.0;
    switch (arg_kind) {
    case PRIM_BOOLEAN:
        long_val = (*env)->CallBooleanMethod(env, arg, Boolean_booleanValue_methodID) ? 1 : 0;
        break;
    case PRIM_BYTE:
        long_val = (*env)->CallByteMethod(env, arg, Byte_byteValue_methodID);
        break;
    case PRIM_CHAR:
        long_val = (*env)->CallCharMethod(env, arg, Character_charValue_methodID);
        break;
    case PRIM_SHORT:
        long_val = (*env)->CallShortMethod(env, arg, Short_shortValue_methodID);
        break;
    case PRIM_INT:
        long_val = (*env)->CallIntMethod(env, arg, Integer_intValue_methodID);
        break;
    case PRIM_LONG:
        long_val = (*env)->CallLongMethod(env, arg, Long_longValue_methodID);
        break;
    case PRIM_FLOAT:
        double_val = (*env)->CallFloatMethod(env, arg, Float_floatValue_methodID);
        break;
    default:
        double_val = (*env)->CallDoubleMethod(env, arg, Double_doubleValue_methodID);
        break;
    }
    if (thrown(env)) { return false; }
    bool arg_is_floating_point = arg_kind == PRIM_FLOAT || arg_kind == PRIM_DOUBLE;

    // Convert the value to the parameter type
    switch (target_kind) {
    case PRIM_BOOLEAN: out->z = (jboolean) (long_val != 0); break;
    case PRIM_BYTE: out->b = (jbyte) long_val; break;
    case PRIM_CHAR: out->c = (jchar) long_val; break;
    case PRIM_SHORT: out->s = (jshort) long_val; break;
    case PRIM_INT: out->i = (jint) long_val; break;
    case PRIM_LONG: out->j = long_val; break;
    case PRIM_FLOAT: out->f = arg_is_floating_point ? (jfloat) double_val : (jfloat) long_val; break;
    default: out->d = arg_is_floating_point ? double_val : (jdouble) long_val; break;
    }
    return true;
}

// Pack the trailing arguments of a varargs invocation into an array of the declared varargs element type, and
// store the array in *out. Returns false with a Java exception pending if any argument cannot be converted to
// the varargs element type.
static bool packVarargs(JNIEnv* env, jobjectArray args, jsize num_non_varargs_params, jsize num_varargs_args,
        jclass varargs_elt_type, jvalue* out) {
    prim_kind varargs_elt_kind = primKindOfClass(env, varargs_elt_type);

// Pack the trailing args into a newly-allocated primitive array. The array elements are released on every
// path out of the loop, with mode JNI_ABORT if unboxing failed, so that a copied array is not written back.
#define UNBOX_VARARGS_CASE(_kind, _prim_type, _Prim_type, _jvalue_field) \
    case _kind: { \
        j ## _prim_type ## Array arr = (*env)->New ## _Prim_type ## Array(env, num_varargs_args); \
        if (!arr) { return false; } \
        out->l = arr; \
        j ## _prim_type* elts = (*env)->Get ## _Prim_type ## ArrayElements(env, arr, NULL); \
        if (!elts) { return false; } \
        bool ok = true; \
        for (jsize i = 0; i < num_varargs_args; i++) { \
            jobject arg = (*env)->GetObjectArrayElement(env, args, i + num_non_varargs_params); \
            if (thrown(env)) { ok = false; break; } \
            jvalue val; \
            ok = unboxArg(env, arg, _kind, &val); \
            if (arg != NULL) { (*env)->DeleteLocalRef(env, arg); } \
            if (!ok) { break; } \
            elts[i] = val._jvalue_field; \
        } \
        (*env)->Release ## _Prim_type ## ArrayElements(env, arr, elts, ok ? 0 : JNI_ABORT); \
        return ok; \
    }

    switch (varargs_elt_kind) {
    UNBOX_VARARGS_CASE(PRIM_BOOLEAN, boolean, Boolean, z)
    UNBOX_VARARGS_CASE(PRIM_BYTE, byte, Byte, b)
    UNBOX_VARARGS_CASE(PRIM_CHAR, char, Char, c)
    UNBOX_VARARGS_CASE(PRIM_SHORT, short, Short, s)
    UNBOX_VARARGS_CASE(PRIM_INT, int, Int, i)
    UNBOX_VARARGS_CASE(PRIM_LONG, long, Long, j)
    UNBOX_VARARGS_CASE(PRIM_FLOAT, float, Float, f)
    UNBOX_VARARGS_CASE(PRIM_DOUBLE, double, Double, d)
    default:
        break;
    }
#undef UNBOX_VARARGS_CASE

    // The varargs element type is a reference type -- check that each trailing arg is assignable to it
    jobjectArray arr = (*env)->NewObjectArray(env, num_varargs_args, varargs_elt_type, NULL);
    if (!arr) { return false; }
    out->l = arr;
    for (jsize i = 0; i < num_varargs_args; i++) {
        jobject arg = (*env)->GetObjectArrayElement(env, args, i + num_non_varargs_params);
        if (thrown(env)) { return false; }
        if (arg != NULL) {
            jclass arg_type = (*env)->GetObjectClass(env, arg);
            if (thrown(env)) { return false; }
            jboolean assignable = (*env)->IsAssignableFrom(env, arg_type, varargs_elt_type);
            (*env)->DeleteLocalRef(env, arg_type);
            if (!assignable) {
                throwIllegalArgumentException(env, "Tried to invoke function with varargs arg of incompatible type");
                return false;
            }
        }
        (*env)->SetObjectArrayElement(env, arr, i, arg);
        if (arg != NULL) { (*env)->DeleteLocalRef(env, arg); }
        if (thrown(env)) { return false; }
    }
    return true;
}

// Convert the args of a method invocation into a jvalue array, checking the arity of the call and the type of
// each arg against the parameter types of the method. Returns false if a Java exception is pending.
static bool unboxArgs(JNIEnv* env, jobjectArray parameterTypes, jsize num_params, bool is_varargs,
        jobjectArray args, jsize num_args, jvalue* arg_jvalues) {
    // A varargs method always has at least one parameter (the varargs array itself), but check defensively,
    // since num_params is read from a Method object that the caller supplied
    if (num_params == 0) { is_varargs = false; }
    jsize num_non_varargs_params = num_params - (is_varargs ? 1 : 0);

    // Check arg arity
    if ((!is_varargs && num_args != num_params) || (is_varargs && num_args < num_non_varargs_params)) {
        throwIllegalArgumentException(env, "Tried to invoke method with wrong number of arguments");
        return false;
    }
    jsize num_varargs_args = num_args - num_non_varargs_params;

    // Each arg of reference type has to stay reachable through a local reference until the method has been
    // invoked, and the default local reference capacity is only 16
    if ((*env)->EnsureLocalCapacity(env, num_params + LOCAL_REF_SLACK) != 0) { return false; }

    // Unbox non-varargs args
    for (jsize i = 0; i < num_non_varargs_params; i++) {
        jclass param_type = (jclass) (*env)->GetObjectArrayElement(env, parameterTypes, i);
        if (thrown(env)) { return false; }
        jobject arg = (*env)->GetObjectArrayElement(env, args, i);
        if (thrown(env)) { return false; }
        prim_kind param_kind = primKindOfClass(env, param_type);
        if (param_kind != PRIM_NONE) {
            bool ok = unboxArg(env, arg, param_kind, &arg_jvalues[i]);
            if (arg != NULL) { (*env)->DeleteLocalRef(env, arg); }
            (*env)->DeleteLocalRef(env, param_type);
            if (!ok) { return false; }
        } else {
            if (arg != NULL) {
                jclass arg_type = (*env)->GetObjectClass(env, arg);
                if (thrown(env)) { return false; }
                jboolean assignable = (*env)->IsAssignableFrom(env, arg_type, param_type);
                (*env)->DeleteLocalRef(env, arg_type);
                if (!assignable) {
                    throwIllegalArgumentException(env, "Tried to invoke function with arg of incompatible type");
                    return false;
                }
            }
            (*env)->DeleteLocalRef(env, param_type);
            // Keep the local reference to arg alive -- it is passed to the method as a jvalue
            arg_jvalues[i].l = arg;
        }
    }
    if (!is_varargs) {
        return true;
    }

    // Get the declared type of the varargs parameter, and of its elements
    jclass varargs_arr_type = (jclass) (*env)->GetObjectArrayElement(env, parameterTypes, num_params - 1);
    if (thrown(env)) { return false; }
    jclass varargs_elt_type = (jclass) (*env)->CallObjectMethod(env, varargs_arr_type,
            Class_getComponentType_methodID);
    if (thrown(env)) { (*env)->DeleteLocalRef(env, varargs_arr_type); return false; }
    if (varargs_elt_type == NULL) {
        // The last parameter of a varargs method is always an array type, so this cannot happen unless the
        // caller supplied a Method object with an inconsistent isVarArgs() flag
        (*env)->DeleteLocalRef(env, varargs_arr_type);
        throwIllegalArgumentException(env, "Varargs parameter is not of array type");
        return false;
    }

    // If a single trailing arg was passed, and it is already an array of the declared varargs type, then pass
    // it through as-is rather than wrapping it in another array. This accepts args in the form produced by
    // the Java compiler at a varargs call site, as well as in the pre-packed form that Method.invoke() takes.
    bool packed = false;
    bool ok = true;
    if (num_varargs_args == 1) {
        jobject arg = (*env)->GetObjectArrayElement(env, args, num_non_varargs_params);
        if (thrown(env)) {
            ok = false;
        } else if (arg != NULL) {
            jclass arg_type = (*env)->GetObjectClass(env, arg);
            if (thrown(env)) {
                ok = false;
            } else {
                jboolean assignable = (*env)->IsAssignableFrom(env, arg_type, varargs_arr_type);
                (*env)->DeleteLocalRef(env, arg_type);
                if (assignable) {
                    // Keep the local reference to arg alive -- it is passed to the method as a jvalue
                    arg_jvalues[num_non_varargs_params].l = arg;
                    packed = true;
                } else {
                    (*env)->DeleteLocalRef(env, arg);
                }
            }
        }
    }
    if (ok && !packed) {
        ok = packVarargs(env, args, num_non_varargs_params, num_varargs_args, varargs_elt_type,
                &arg_jvalues[num_non_varargs_params]);
    }
    (*env)->DeleteLocalRef(env, varargs_elt_type);
    (*env)->DeleteLocalRef(env, varargs_arr_type);
    return ok;
}

// Unbox a jobjectArray of method invocation args into a jvalue array. Returns false if a Java exception is
// pending. arg_jvalues must have room for MAX_METHOD_PARAMS entries.
static bool unbox(JNIEnv* env, jobject method, jobjectArray args, jsize num_args, jvalue* arg_jvalues) {
    jobjectArray parameterTypes = (jobjectArray) (*env)->CallObjectMethod(env, method,
            Method_getParameterTypes_methodID);
    if (thrown(env)) { return false; }
    bool ok = false;
    jsize num_params = (*env)->GetArrayLength(env, parameterTypes);
    if (!thrown(env)) {
        if (num_params > MAX_METHOD_PARAMS) {
            // Cannot happen for a method loaded from a valid classfile, but arg_jvalues is a fixed-size buffer
            throwIllegalArgumentException(env, "Method has too many parameters");
        } else {
            jboolean is_varargs = (*env)->CallBooleanMethod(env, method, Method_isVarArgs_methodID);
            if (!thrown(env)) {
                ok = unboxArgs(env, parameterTypes, num_params, is_varargs ? true : false, args, num_args,
                        arg_jvalues);
            }
        }
    }
    (*env)->DeleteLocalRef(env, parameterTypes);
    return ok;
}

// -----------------------------------------------------------------------------------------------------------------

// Find a class by name. Name should be of the form "java/lang/String", or "[Ljava/lang/Object;" for an array class.
JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_findClassInternal(JNIEnv *env, jclass ignored, jstring class_name) {
    if (argIsNull(env, class_name)) { return NULL; }
    const char* class_name_chars = (*env)->GetStringUTFChars(env, class_name, NULL);
    if (!class_name_chars || thrown(env)) { return NULL; }
    jclass class_ref = (*env)->FindClass(env, class_name_chars);
    (*env)->ReleaseStringUTFChars(env, class_name, class_name_chars);
    if (thrown(env)) { return NULL; }
    return class_ref;
}

// Find a method by name and signature. Signature should be of the form "(Z)[Ljava/lang/Class;"
JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_findMethodInternal(JNIEnv *env, jclass ignored, jclass cls, jstring method_name, jstring sig, jboolean is_static) {
    if (argIsNull(env, cls) || argIsNull(env, method_name) || argIsNull(env, sig)) { return NULL; }
    const char* method_name_chars = (*env)->GetStringUTFChars(env, method_name, NULL);
    if (!method_name_chars || thrown(env)) { return NULL; }
    const char* sig_chars = (*env)->GetStringUTFChars(env, sig, NULL);
    if (!sig_chars || thrown(env)) { 
        (*env)->ReleaseStringUTFChars(env, method_name, method_name_chars);
        return NULL; 
    }
    jmethodID methodID = is_static
            ? (*env)->GetStaticMethodID(env, cls, method_name_chars, sig_chars)
            : (*env)->GetMethodID(env, cls, method_name_chars, sig_chars);
    (*env)->ReleaseStringUTFChars(env, sig, sig_chars);
    (*env)->ReleaseStringUTFChars(env, method_name, method_name_chars);
    if (!methodID) {
        return NULL;
    }
    jobject result = (*env)->ToReflectedMethod(env, cls, methodID, is_static ? JNI_TRUE : JNI_FALSE);
    if (thrown(env)) { return NULL; }
    return result;
}

// Find a field by name and signature. Signature should be of the form "Ljava/lang/String;"
JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_findFieldInternal(JNIEnv *env, jclass ignored, jclass cls, jstring field_name, jstring sig, jboolean is_static) {
    if (argIsNull(env, cls) || argIsNull(env, field_name) || argIsNull(env, sig)) { return NULL; }
    const char* field_name_chars = (*env)->GetStringUTFChars(env, field_name, NULL);
    if (!field_name_chars || thrown(env)) { return NULL; }
    const char* sig_chars = (*env)->GetStringUTFChars(env, sig, NULL);
    if (!sig_chars || thrown(env)) { 
        (*env)->ReleaseStringUTFChars(env, field_name, field_name_chars);
        return NULL; 
    }
    jfieldID fieldID = is_static
            ? (*env)->GetStaticFieldID(env, cls, field_name_chars, sig_chars)
            : (*env)->GetFieldID(env, cls, field_name_chars, sig_chars);
    (*env)->ReleaseStringUTFChars(env, sig, sig_chars);
    (*env)->ReleaseStringUTFChars(env, field_name, field_name_chars);
    if (!fieldID) {
        return NULL;
    }
    jobject result = (*env)->ToReflectedField(env, cls, fieldID, is_static ? JNI_TRUE : JNI_FALSE);
    if (thrown(env)) { return NULL; }
    return result;
}

// -----------------------------------------------------------------------------------------------------------------

// Methods required by jvm-driver

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_allocateInstance(JNIEnv* env, jclass ignored, jclass instanceType) {
    if (argIsNull(env, instanceType)) { return NULL; }
    
    // Check if it's a primitive type first 
    jboolean isPrimitive = (*env)->CallBooleanMethod(env, instanceType, Class_isPrimitive_methodID);
    if (thrown(env)) { return NULL; }
    if (isPrimitive) {
        throwInstantiationException(env, "Cannot instantiate primitive type");
        return NULL;
    }
    
    // Check if it's an array type next (arrays can have interface modifiers)
    jboolean isArray = (*env)->CallBooleanMethod(env, instanceType, Class_isArray_methodID);
    if (thrown(env)) { return NULL; }
    if (isArray) {
        throwInstantiationException(env, "Cannot instantiate array type (use Array.newInstance instead)");
        return NULL;
    }
    
    // Finally check modifiers for abstract/interface classes
    jint modifiers = (*env)->CallIntMethod(env, instanceType, Class_getModifiers_methodID);
    if (thrown(env)) { return NULL; }
    if ((modifiers & MODIFIER_ABSTRACT) || (modifiers & MODIFIER_INTERFACE)) {
        throwInstantiationException(env, "Cannot instantiate abstract class or interface");
        return NULL;
    }
    
    jobject result = (*env)->AllocObject(env, instanceType);
    if (thrown(env)) { return NULL; }
    return result;
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_sneakyThrow(JNIEnv* env, jclass ignored, jthrowable throwable) {
    if (throwable == NULL) {
        throwNullPointerException(env, "throwable is null");
    } else {
        (*env)->Throw(env, throwable);
    }
}

// -----------------------------------------------------------------------------------------------------------------

// Non-static field getters:

#define FIELD_GETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT j ## _prim_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_get ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject obj, jobject field) { \
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field) _extra_check) { return (j ## _prim_type) 0; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    j ## _prim_type result = (*env)->Get ## _Prim_type ## Field(env, obj, fieldID); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    return result; \
}

FIELD_GETTER(int, Int, || !checkFieldPrimitiveType(env, field, int_class) )
FIELD_GETTER(long, Long, || !checkFieldPrimitiveType(env, field, long_class) )
FIELD_GETTER(short, Short, || !checkFieldPrimitiveType(env, field, short_class) )
FIELD_GETTER(char, Char, || !checkFieldPrimitiveType(env, field, char_class) )
FIELD_GETTER(boolean, Boolean, || !checkFieldPrimitiveType(env, field, boolean_class) )
FIELD_GETTER(byte, Byte, || !checkFieldPrimitiveType(env, field, byte_class) )
FIELD_GETTER(float, Float, || !checkFieldPrimitiveType(env, field, float_class) )
FIELD_GETTER(double, Double, || !checkFieldPrimitiveType(env, field, double_class) )
FIELD_GETTER(object, Object, || !checkFieldTypeIsReference(env, field) )

// Non-static field setters:

#define FIELD_SETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_set ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject obj, jobject field, j ## _prim_type val) { \
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field) _extra_check) { return; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return; } \
    (*env)->Set ## _Prim_type ## Field(env, obj, fieldID, val); \
}

FIELD_SETTER(int, Int, || !checkFieldPrimitiveType(env, field, int_class) )
FIELD_SETTER(long, Long, || !checkFieldPrimitiveType(env, field, long_class) )
FIELD_SETTER(short, Short, || !checkFieldPrimitiveType(env, field, short_class) )
FIELD_SETTER(char, Char, || !checkFieldPrimitiveType(env, field, char_class) )
FIELD_SETTER(boolean, Boolean, || !checkFieldPrimitiveType(env, field, boolean_class) )
FIELD_SETTER(byte, Byte, || !checkFieldPrimitiveType(env, field, byte_class) )
FIELD_SETTER(float, Float, || !checkFieldPrimitiveType(env, field, float_class) )
FIELD_SETTER(double, Double, || !checkFieldPrimitiveType(env, field, double_class) )
FIELD_SETTER(object, Object, || !checkFieldTypeIsReference(env, field) || !checkFieldValType(env, field, val) )

// -----------------------------------------------------------------------------------------------------------------

// Static field getters:

#define STATIC_FIELD_GETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT j ## _prim_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStatic ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject field) { \
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true) _extra_check) { return (j ## _prim_type) 0; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    jclass cls = (jclass) (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    j ## _prim_type result = (*env)->GetStatic ## _Prim_type ## Field(env, cls, fieldID); \
    (*env)->DeleteLocalRef(env, cls); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    return result; \
}

STATIC_FIELD_GETTER(int, Int, || !checkFieldPrimitiveType(env, field, int_class) )
STATIC_FIELD_GETTER(long, Long, || !checkFieldPrimitiveType(env, field, long_class) )
STATIC_FIELD_GETTER(short, Short, || !checkFieldPrimitiveType(env, field, short_class) )
STATIC_FIELD_GETTER(char, Char, || !checkFieldPrimitiveType(env, field, char_class) )
STATIC_FIELD_GETTER(boolean, Boolean, || !checkFieldPrimitiveType(env, field, boolean_class) )
STATIC_FIELD_GETTER(byte, Byte, || !checkFieldPrimitiveType(env, field, byte_class) )
STATIC_FIELD_GETTER(float, Float, || !checkFieldPrimitiveType(env, field, float_class) )
STATIC_FIELD_GETTER(double, Double, || !checkFieldPrimitiveType(env, field, double_class) )
STATIC_FIELD_GETTER(object, Object, || !checkFieldTypeIsReference(env, field) )

// Static field setters:

#define STATIC_FIELD_SETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStatic ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject field, j ## _prim_type val) { \
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true) _extra_check) { return; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return; } \
    jclass cls = (jclass) (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID); \
    if (thrown(env)) { return; } \
    (*env)->SetStatic ## _Prim_type ## Field(env, cls, fieldID, val); \
    (*env)->DeleteLocalRef(env, cls); \
}

STATIC_FIELD_SETTER(int, Int, || !checkFieldPrimitiveType(env, field, int_class) )
STATIC_FIELD_SETTER(long, Long, || !checkFieldPrimitiveType(env, field, long_class) )
STATIC_FIELD_SETTER(short, Short, || !checkFieldPrimitiveType(env, field, short_class) )
STATIC_FIELD_SETTER(char, Char, || !checkFieldPrimitiveType(env, field, char_class) )
STATIC_FIELD_SETTER(boolean, Boolean, || !checkFieldPrimitiveType(env, field, boolean_class) )
STATIC_FIELD_SETTER(byte, Byte, || !checkFieldPrimitiveType(env, field, byte_class) )
STATIC_FIELD_SETTER(float, Float, || !checkFieldPrimitiveType(env, field, float_class) )
STATIC_FIELD_SETTER(double, Double, || !checkFieldPrimitiveType(env, field, double_class) )
STATIC_FIELD_SETTER(object, Object, || !checkFieldTypeIsReference(env, field) || !checkFieldValType(env, field, val) )

// -----------------------------------------------------------------------------------------------------------------

// Invoke non-static methods

#define INVOKE_METHOD(_jni_ret_type, _prim_type, _Prim_type, _assign, _assign_return, _err_return, _extra_check) \
JNIEXPORT _jni_ret_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invoke ## _Prim_type ## Method(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) { \
    if (argIsNull(env, obj) || argIsNull(env, method) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method) _extra_check) { return _err_return; } \
    jmethodID methodID = (*env)->FromReflectedMethod(env, method); \
    if (thrown(env)) { return _err_return; } \
    /* A null args array is treated as an empty args array, as in Method.invoke() */ \
    jsize num_args = 0; \
    if (args != NULL) { \
        num_args = (*env)->GetArrayLength(env, args); \
        if (thrown(env)) { return _err_return; } \
    } \
    jvalue arg_jvalues[MAX_METHOD_PARAMS]; \
    if (!unbox(env, method, args, num_args, arg_jvalues)) { return _err_return; } \
    _assign (*env)->Call ## _Prim_type ## MethodA(env, obj, methodID, arg_jvalues); \
    if (thrown(env)) { return _err_return; } \
    return _assign_return; \
}

INVOKE_METHOD(void, void, Void, , , , || !checkPrimitiveMethodReturnType(env, method, void_class) )
INVOKE_METHOD(jint, int, Int, jint return_val = , return_val, (jint) 0, || !checkPrimitiveMethodReturnType(env, method, int_class) )
INVOKE_METHOD(jlong, long, Long, jlong return_val = , return_val, (jlong) 0, || !checkPrimitiveMethodReturnType(env, method, long_class) )
INVOKE_METHOD(jshort, short, Short, jshort return_val = , return_val, (jshort) 0, || !checkPrimitiveMethodReturnType(env, method, short_class) )
INVOKE_METHOD(jchar, char, Char, jchar return_val = , return_val, (jchar) 0, || !checkPrimitiveMethodReturnType(env, method, char_class) )
INVOKE_METHOD(jboolean, boolean, Boolean, jboolean return_val = , return_val, (jboolean) 0, || !checkPrimitiveMethodReturnType(env, method, boolean_class) )
INVOKE_METHOD(jbyte, byte, Byte, jbyte return_val = , return_val, (jbyte) 0, || !checkPrimitiveMethodReturnType(env, method, byte_class) )
INVOKE_METHOD(jfloat, float, Float, jfloat return_val = , return_val, (jfloat) 0, || !checkPrimitiveMethodReturnType(env, method, float_class) )
INVOKE_METHOD(jdouble, double, Double, jdouble return_val = , return_val, (jdouble) 0, || !checkPrimitiveMethodReturnType(env, method, double_class) )
INVOKE_METHOD(jobject, object, Object, jobject return_val = , return_val, NULL, || !checkMethodReturnTypeNotVoidOrPrimitive(env, method) )

// -----------------------------------------------------------------------------------------------------------------

// Invoke static methods

#define INVOKE_STATIC_METHOD(_jni_ret_type, _prim_type, _Prim_type, _assign, _assign_return, _err_return, _extra_check) \
JNIEXPORT _jni_ret_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStatic ## _Prim_type ## Method(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) { \
    if (argIsNull(env, method) || !checkMethodStaticModifier(env, method, true) _extra_check) { return _err_return; } \
    jclass cls = (jclass) (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID); \
    if (thrown(env)) { return _err_return; } \
    jmethodID methodID = (*env)->FromReflectedMethod(env, method); \
    if (thrown(env)) { return _err_return; } \
    /* A null args array is treated as an empty args array, as in Method.invoke() */ \
    jsize num_args = 0; \
    if (args != NULL) { \
        num_args = (*env)->GetArrayLength(env, args); \
        if (thrown(env)) { return _err_return; } \
    } \
    jvalue arg_jvalues[MAX_METHOD_PARAMS]; \
    if (!unbox(env, method, args, num_args, arg_jvalues)) { return _err_return; } \
    _assign (*env)->CallStatic ## _Prim_type ## MethodA(env, cls, methodID, arg_jvalues); \
    if (thrown(env)) { return _err_return; } \
    return _assign_return; \
}

INVOKE_STATIC_METHOD(void, void, Void, , , , || !checkPrimitiveMethodReturnType(env, method, void_class) )
INVOKE_STATIC_METHOD(jint, int, Int, jint return_val = , return_val, (jint) 0, || !checkPrimitiveMethodReturnType(env, method, int_class) )
INVOKE_STATIC_METHOD(jlong, long, Long, jlong return_val = , return_val, (jlong) 0, || !checkPrimitiveMethodReturnType(env, method, long_class) )
INVOKE_STATIC_METHOD(jshort, short, Short, jshort return_val = , return_val, (jshort) 0, || !checkPrimitiveMethodReturnType(env, method, short_class) )
INVOKE_STATIC_METHOD(jchar, char, Char, jchar return_val = , return_val, (jchar) 0, || !checkPrimitiveMethodReturnType(env, method, char_class) )
INVOKE_STATIC_METHOD(jboolean, boolean, Boolean, jboolean return_val = , return_val, (jboolean) 0, || !checkPrimitiveMethodReturnType(env, method, boolean_class) )
INVOKE_STATIC_METHOD(jbyte, byte, Byte, jbyte return_val = , return_val, (jbyte) 0, || !checkPrimitiveMethodReturnType(env, method, byte_class) )
INVOKE_STATIC_METHOD(jfloat, float, Float, jfloat return_val = , return_val, (jfloat) 0, || !checkPrimitiveMethodReturnType(env, method, float_class) )
INVOKE_STATIC_METHOD(jdouble, double, Double, jdouble return_val = , return_val, (jdouble) 0, || !checkPrimitiveMethodReturnType(env, method, double_class) )
INVOKE_STATIC_METHOD(jobject, object, Object, jobject return_val = , return_val, NULL, || !checkMethodReturnTypeNotVoidOrPrimitive(env, method) )
