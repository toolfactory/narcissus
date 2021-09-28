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

// -----------------------------------------------------------------------------------------------------------------

bool thrown(JNIEnv* env) {
    return (*env)->ExceptionOccurred(env);
}

// -----------------------------------------------------------------------------------------------------------------

// Prelookup of frequently-used classes and methods:

jclass Integer_class;
jclass int_class;
jmethodID int_value_methodID;

jclass Long_class;
jclass long_class;
jmethodID long_value_methodID;

jclass Short_class;
jclass short_class;
jmethodID short_value_methodID;

jclass Character_class;
jclass char_class;
jmethodID char_value_methodID;

jclass Boolean_class;
jclass boolean_class;
jmethodID boolean_value_methodID;

jclass Byte_class;
jclass byte_class;
jmethodID byte_value_methodID;

jclass Float_class;
jclass float_class;
jmethodID float_value_methodID;

jclass Double_class;
jclass double_class;
jmethodID double_value_methodID;

jmethodID Method_getDeclaringClass_methodID;
jmethodID Method_getModifiers_methodID;

jmethodID Field_getDeclaringClass_methodID;
jmethodID Field_getModifiers_methodID;
jmethodID Field_getType_methodID;

jfieldID AccessibleObject_overrideFieldId;
jfieldID Lookup_allowedModesFieldId;


// Pre-look-up classes and methods for primitive types and Class, and allocate new global refs for them so they can be used across JNI calls
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_1) != JNI_OK) {
        return -1;
    }

    Integer_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Integer"));
    if (thrown(env)) { return -1; }
    int_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Integer_class, (*env)->GetStaticFieldID(env, Integer_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    int_value_methodID = (*env)->GetMethodID(env, Integer_class, "intValue", "()I");
    if (thrown(env)) { return -1; }

    Long_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Long"));
    if (thrown(env)) { return -1; }
    long_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Long_class, (*env)->GetStaticFieldID(env, Long_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    long_value_methodID = (*env)->GetMethodID(env, Long_class, "longValue", "()J");
    if (thrown(env)) { return -1; }

    Short_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Short"));
    if (thrown(env)) { return -1; }
    short_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Short_class, (*env)->GetStaticFieldID(env, Short_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    short_value_methodID = (*env)->GetMethodID(env, Short_class, "shortValue", "()S");
    if (thrown(env)) { return -1; }

    Character_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Character"));
    if (thrown(env)) { return -1; }
    char_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Character_class, (*env)->GetStaticFieldID(env, Character_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    char_value_methodID = (*env)->GetMethodID(env, Character_class, "charValue", "()C");
    if (thrown(env)) { return -1; }

    Boolean_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Boolean"));
    if (thrown(env)) { return -1; }
    boolean_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Boolean_class, (*env)->GetStaticFieldID(env, Boolean_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    boolean_value_methodID = (*env)->GetMethodID(env, Boolean_class, "booleanValue", "()Z");
    if (thrown(env)) { return -1; }

    Byte_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Byte"));
    if (thrown(env)) { return -1; }
    byte_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Byte_class, (*env)->GetStaticFieldID(env, Byte_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    byte_value_methodID = (*env)->GetMethodID(env, Byte_class, "byteValue", "()B");
    if (thrown(env)) { return -1; }

    Float_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Float"));
    if (thrown(env)) { return -1; }
    float_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Float_class, (*env)->GetStaticFieldID(env, Float_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    float_value_methodID = (*env)->GetMethodID(env, Float_class, "floatValue", "()F");
    if (thrown(env)) { return -1; }

    Double_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Double"));
    if (thrown(env)) { return -1; }
    double_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Double_class, (*env)->GetStaticFieldID(env, Double_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    double_value_methodID = (*env)->GetMethodID(env, Double_class, "doubleValue", "()D");
    if (thrown(env)) { return -1; }
    
    jclass Method_class = (*env)->FindClass(env, "java/lang/reflect/Method");
    if (thrown(env)) { return -1; }
    Method_getDeclaringClass_methodID = (*env)->GetMethodID(env, Method_class, "getDeclaringClass", "()Ljava/lang/Class;");
    if (thrown(env)) { return -1; }
    Method_getModifiers_methodID = (*env)->GetMethodID(env, Method_class, "getModifiers", "()I");
    if (thrown(env)) { return -1; }
    
    jclass Field_class = (*env)->FindClass(env, "java/lang/reflect/Field");
    if (thrown(env)) { return -1; }
    Field_getDeclaringClass_methodID = (*env)->GetMethodID(env, Field_class, "getDeclaringClass", "()Ljava/lang/Class;");
    if (thrown(env)) { return -1; }
    Field_getModifiers_methodID = (*env)->GetMethodID(env, Field_class, "getModifiers", "()I");
    if (thrown(env)) { return -1; }
    Field_getType_methodID = (*env)->GetMethodID(env, Field_class, "getType", "()Ljava/lang/Class;");
    if (thrown(env)) { return -1; }

    Lookup_allowedModesFieldId = (*env)->GetFieldID(env, (*env)->FindClass(env, "java/lang/invoke/MethodHandles$Lookup"), "allowedModes", "I");
    if (thrown(env)) { return -1; }
    AccessibleObject_overrideFieldId =(*env)->GetFieldID(env, (*env)->FindClass(env, "java/lang/reflect/AccessibleObject"), "override", "Z");
    if (thrown(env)) { return -1; }
    
    return JNI_VERSION_1_1;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_1);

    (*env)->DeleteGlobalRef(env, Integer_class);
    (*env)->DeleteGlobalRef(env, int_class);

    (*env)->DeleteGlobalRef(env, Long_class);
    (*env)->DeleteGlobalRef(env, long_class);

    (*env)->DeleteGlobalRef(env, Short_class);
    (*env)->DeleteGlobalRef(env, short_class);

    (*env)->DeleteGlobalRef(env, Character_class);
    (*env)->DeleteGlobalRef(env, char_class);

    (*env)->DeleteGlobalRef(env, Boolean_class);
    (*env)->DeleteGlobalRef(env, boolean_class);

    (*env)->DeleteGlobalRef(env, Byte_class);
    (*env)->DeleteGlobalRef(env, byte_class);

    (*env)->DeleteGlobalRef(env, Float_class);
    (*env)->DeleteGlobalRef(env, float_class);

    (*env)->DeleteGlobalRef(env, Double_class);
    (*env)->DeleteGlobalRef(env, double_class);
}

// -----------------------------------------------------------------------------------------------------------------

// Utility functions

void throwIllegalArgumentException(JNIEnv* env, char* msg) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    if (cls) {
        (*env)->ThrowNew(env, cls, msg);
    }
}

void throwNullPointerException(JNIEnv* env, char* msg) {
    jclass cls = (*env)->FindClass(env, "java/lang/NullPointerException");
    if (cls) {
        (*env)->ThrowNew(env, cls, msg);
    }
}

bool argIsNull(JNIEnv* env, jobject obj) {
    if (!obj) {
        throwNullPointerException(env, "Argument cannot be null");
        return true;
    }
    return false;
}

bool checkFieldStaticModifier(JNIEnv* env, jobject field, bool expectStatic) {
    jint mods = (*env)->CallIntMethod(env, field, Field_getModifiers_methodID);
    if (thrown(env)) {
        throwIllegalArgumentException(env, "Could not read field modifiers");
        return false;
    }
    if (((mods & 8) ? 1 : 0) != (expectStatic ? 1 : 0)) {
        throwIllegalArgumentException(env, expectStatic ? "Expected static field" : "Expected non-static field");
        return false;
    }
    return true;
}

bool checkMethodStaticModifier(JNIEnv* env, jobject method, bool expectStatic) {
    jint mods = (*env)->CallIntMethod(env, method, Method_getModifiers_methodID);
    if (thrown(env)) {
        throwIllegalArgumentException(env, "Could not read method modifiers");
        return false;
    }
    if (((mods & 8) ? 1 : 0) != (expectStatic ? 1 : 0)) {
        throwIllegalArgumentException(env, expectStatic ? "Expected static method" : "Expected non-static method");
        return false;
    }
    return true;
}

bool checkFieldReceiver(JNIEnv* env, jobject obj, jobject field) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    if (thrown(env)) { return false; }
    jclass declaringClass = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return false; }
    jboolean assignable = (*env)->IsAssignableFrom(env, cls, declaringClass);
    if (thrown(env)) { return false; }
    if (!assignable) {
        throwIllegalArgumentException(env, "Object class does not match declaring class of field");
        return false;
    }
    return true;
}

bool checkMethodReceiver(JNIEnv* env, jobject obj, jobject method) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    if (thrown(env)) { return false; }
    jclass declaringClass = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return false; }
    jboolean assignable = (*env)->IsAssignableFrom(env, cls, declaringClass);
    if (thrown(env)) { return false; }
    if (!assignable) {
        throwIllegalArgumentException(env, "Object class does not match declaring class of method");
        return false;
    }
    return true;
}

bool checkFieldValType(JNIEnv* env, jobject field, jobject val) {
    if (val != NULL) {
        jclass valCls = (*env)->GetObjectClass(env, val);
        if (thrown(env)) { return false; }
        jclass fieldType = (*env)->CallObjectMethod(env, field, Field_getType_methodID);
        if (thrown(env)) { return false; }
        jboolean assignable = (*env)->IsAssignableFrom(env, valCls, fieldType);
        if (thrown(env)) { return false; }
        if (!assignable) {
            throwIllegalArgumentException(env, "Value cannot be assigned to a field of this type");
            return false;
        }
    }
    return true;
}

// -----------------------------------------------------------------------------------------------------------------

// Unbox a jobjectArray of method invocation args into a jvalue array. Returns 0 if an exception was thrown, or 1 if unboxing succeeded.
int unbox(JNIEnv *env, jobject method, jobjectArray args, jsize num_args, jvalue* arg_jvalues) {
    // Get parameter types of method
    jclass methodClass = (*env)->GetObjectClass(env, method);
    if (thrown(env)) { return 0; }
    jmethodID getParameterTypesMethodID = (*env)->GetMethodID(env, methodClass, "getParameterTypes", "()[Ljava/lang/Class;");
    if (thrown(env)) { return 0; }
    jobject parameterTypes = (*env)->CallObjectMethod(env, method, getParameterTypesMethodID);
    if (thrown(env)) { return 0; }
    jsize num_params = (*env)->GetArrayLength(env, parameterTypes);
    if (thrown(env)) { return 0; }

    // Check arg arity
    if (num_args != num_params) {
        throwIllegalArgumentException(env, "Tried to invoke method with wrong number of arguments");
        return 0;
    }
    
    // Unbox args
    for (jsize i = 0; i < num_args; i++) {
        jobject paramType = (*env)->GetObjectArrayElement(env, parameterTypes, i);
        if (thrown(env)) { return 0; }
        jobject arg = (*env)->GetObjectArrayElement(env, args, i);
        if (thrown(env)) { return 0; }
        jclass argType = arg == NULL ? (jclass) NULL : (*env)->GetObjectClass(env, arg);
        if (thrown(env)) { return 0; }

        if ((*env)->IsSameObject(env, paramType, int_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Integer");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Integer_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Integer");
                return 0;
            } else {
                arg_jvalues[i].i = (*env)->CallIntMethod(env, arg, int_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, long_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Long");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Long_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Long");
                return 0;
            } else {
                arg_jvalues[i].j = (*env)->CallLongMethod(env, arg, long_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, short_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Short");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Short_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Short");
                return 0;
            } else {
                arg_jvalues[i].s = (*env)->CallShortMethod(env, arg, short_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, char_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Character");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Character_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Character");
                return 0;
            } else {
                arg_jvalues[i].c = (*env)->CallCharMethod(env, arg, char_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, boolean_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Boolean");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Boolean_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Boolean");
                return 0;
            } else {
                arg_jvalues[i].z = (*env)->CallBooleanMethod(env, arg, boolean_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, byte_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Byte");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Byte_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Byte");
                return 0;
            } else {
                arg_jvalues[i].b = (*env)->CallByteMethod(env, arg, byte_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, float_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Float");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Float_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Float");
                return 0;
            } else {
                arg_jvalues[i].f = (*env)->CallFloatMethod(env, arg, float_value_methodID);
            }
        } else if ((*env)->IsSameObject(env, paramType, double_class)) {
            if (arg == NULL) {
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected Double");
                return 0;
            } else if (!(*env)->IsSameObject(env, argType, Double_class)) {
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected Double");
                return 0;
            } else {
                arg_jvalues[i].d = (*env)->CallDoubleMethod(env, arg, double_value_methodID);
            }
        } else {
            // Arg does not need unboxing, but we need to check if it is assignable from the parameter type
            if (arg != NULL && !(*env)->IsAssignableFrom(env, argType, paramType)) {
                throwIllegalArgumentException(env, "Tried to invoke function with arg of incompatible type");
                return 0;
            } else {
                arg_jvalues[i].l = arg;
            }
        }
        if (thrown(env)) { return 0; }
    }
    return 1;
}

// -----------------------------------------------------------------------------------------------------------------

// Find a class by name with no security checks. Name should be of the form "java/lang/String", or "[Ljava/lang/Object;" for an array class.
JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_findClassInternal(JNIEnv *env, jclass ignored, jstring class_name_internal) {
    if (argIsNull(env, class_name_internal)) { return NULL; }
    const char* class_name_internal_chars = (*env)->GetStringUTFChars(env, class_name_internal, NULL);
    if (!class_name_internal_chars || thrown(env)) { return NULL; }
    jclass class_ref = (*env)->FindClass(env, class_name_internal_chars);
    (*env)->ReleaseStringUTFChars(env, class_name_internal, class_name_internal_chars);
    return class_ref;
}

// -----------------------------------------------------------------------------------------------------------------

// Get declared methods without any visibility checks
JNIEXPORT jobjectArray JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDeclaredMethods(JNIEnv *env, jclass ignored, jclass cls) {
    if (argIsNull(env, cls)) { return NULL; }
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    if (thrown(env)) { return NULL; }
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;");
    if (methodID == 0 || thrown(env)) { return NULL; }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// Get declared constructors without any visibility checks
JNIEXPORT jobjectArray JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDeclaredConstructors(JNIEnv *env, jclass ignored, jclass cls) {
    if (argIsNull(env, cls)) { return NULL; }
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    if (thrown(env)) { return NULL; }
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;");
    if (methodID == 0 || thrown(env)) { return NULL; }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// Get declared fields without any visibility checks
JNIEXPORT jobjectArray JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDeclaredFields(JNIEnv *env, jclass ignored, jclass cls) {
    if (argIsNull(env, cls)) { return NULL; }
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    if (thrown(env)) { return NULL; }
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredFields0", "(Z)[Ljava/lang/reflect/Field;");
    if (methodID == 0 || thrown(env)) { return NULL; }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// -----------------------------------------------------------------------------------------------------------------

// Some methods required by jvm-driver

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setAllowedModes(JNIEnv* env, jclass ignored, jobject consulter, jint modes) {
	(*env)->SetIntField(env, consulter, Lookup_allowedModesFieldId, modes);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setAccessible(JNIEnv* env, jclass ignored, jobject accessibleObject, jboolean flag) {
	(*env)->SetBooleanField(env, accessibleObject, AccessibleObject_overrideFieldId, flag);
}

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_allocateInstance(JNIEnv* env, jclass ignored, jclass instanceType) {
	return (*env)->AllocObject(env, instanceType);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_sneakyThrow(JNIEnv* env, jclass ignored, jthrowable throwable) {
	(*env)->Throw(env, throwable);
}

// -----------------------------------------------------------------------------------------------------------------

// Object field getters:

JNIEXPORT jint JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getIntField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jint) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jint) 0; }
    return (*env)->GetIntField(env, obj, fieldID);
}

JNIEXPORT jlong JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getLongField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jlong) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jlong) 0; }
    return (*env)->GetLongField(env, obj, fieldID);
}

JNIEXPORT jshort JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getShortField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jshort) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jshort) 0; }
    return (*env)->GetShortField(env, obj, fieldID);
}

JNIEXPORT jchar JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getCharField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jchar) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jchar) 0; }
    return (*env)->GetCharField(env, obj, fieldID);
}

JNIEXPORT jboolean JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getBooleanField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jboolean) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jboolean) 0; }
    return (*env)->GetBooleanField(env, obj, fieldID);
}

JNIEXPORT jbyte JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getByteField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jbyte) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jbyte) 0; }
    return (*env)->GetByteField(env, obj, fieldID);
}

JNIEXPORT jfloat JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getFloatField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jfloat) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jfloat) 0; }
    return (*env)->GetFloatField(env, obj, fieldID);
}

JNIEXPORT jdouble JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDoubleField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (jdouble) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jdouble) 0; }
    return (*env)->GetDoubleField(env, obj, fieldID);
}

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getObjectField(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return NULL; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return NULL; }
    return (*env)->GetObjectField(env, obj, fieldID);
}

// Object field setters:

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setIntField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jint val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetIntField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setLongField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jlong val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetLongField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setShortField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jshort val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetShortField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setCharField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jchar val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetCharField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setBooleanField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jboolean val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetBooleanField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setByteField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jbyte val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetByteField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setFloatField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jfloat val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetFloatField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setDoubleField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jdouble val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetDoubleField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setObjectField(JNIEnv *env, jclass ignored, jobject obj, jobject field, jobject val) {
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field) || !checkFieldValType(env, field, val)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    (*env)->SetObjectField(env, obj, fieldID, val);
}

// -----------------------------------------------------------------------------------------------------------------

// Static field getters:

JNIEXPORT jint JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticIntField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jint) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jint) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jint) 0; }
    return (*env)->GetStaticIntField(env, cls, fieldID);
}

JNIEXPORT jlong JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticLongField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jlong) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jlong) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jlong) 0; }
    return (*env)->GetStaticLongField(env, cls, fieldID);
}

JNIEXPORT jshort JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticShortField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jshort) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jshort) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jshort) 0; }
    return (*env)->GetStaticShortField(env, cls, fieldID);
}

JNIEXPORT jchar JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticCharField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jchar) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jchar) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jchar) 0; }
    return (*env)->GetStaticCharField(env, cls, fieldID);
}

JNIEXPORT jboolean JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticBooleanField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jboolean) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jboolean) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jboolean) 0; }
    return (*env)->GetStaticBooleanField(env, cls, fieldID);
}

JNIEXPORT jbyte JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticByteField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jbyte) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jbyte) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jbyte) 0; }
    return (*env)->GetStaticByteField(env, cls, fieldID);
}

JNIEXPORT jfloat JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticFloatField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jfloat) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jfloat) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jfloat) 0; }
    return (*env)->GetStaticFloatField(env, cls, fieldID);
}

JNIEXPORT jdouble JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticDoubleField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (jdouble) 0; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return (jdouble) 0; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return (jdouble) 0; }
    return (*env)->GetStaticDoubleField(env, cls, fieldID);
}

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStaticObjectField(JNIEnv *env, jclass ignored, jobject field) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return NULL; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return NULL; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return NULL; }
    return (*env)->GetStaticObjectField(env, cls, fieldID);
}

// Static field setters:

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticIntField(JNIEnv *env, jclass ignored, jobject field, jint val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticIntField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticLongField(JNIEnv *env, jclass ignored, jobject field, jlong val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticLongField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticShortField(JNIEnv *env, jclass ignored, jobject field, jshort val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticShortField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticCharField(JNIEnv *env, jclass ignored, jobject field, jchar val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticCharField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticBooleanField(JNIEnv *env, jclass ignored, jobject field, jboolean val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticBooleanField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticByteField(JNIEnv *env, jclass ignored, jobject field, jbyte val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticByteField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticFloatField(JNIEnv *env, jclass ignored, jobject field, jfloat val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticFloatField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticDoubleField(JNIEnv *env, jclass ignored, jobject field, jdouble val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticDoubleField(env, cls, fieldID, val);
}

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStaticObjectField(JNIEnv *env, jclass ignored, jobject field, jobject val) {
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true) || !checkFieldValType(env, field, val)) { return; }
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    if (thrown(env)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    (*env)->SetStaticObjectField(env, cls, fieldID, val);
}

// -----------------------------------------------------------------------------------------------------------------

// Invoke object methods

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeVoidMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return; }
    if (num_args == 0) {
        (*env)->CallVoidMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        if (unbox(env, method, args, num_args, arg_jvalues)) {
            (*env)->CallVoidMethodA(env, obj, methodID, arg_jvalues);
        }
    }
}

JNIEXPORT jint JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeIntMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jint) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jint) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jint) 0; }
    if (num_args == 0) {
        return (*env)->CallIntMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallIntMethodA(env, obj, methodID, arg_jvalues) : (jint) 0;
    }
}

JNIEXPORT jlong JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeLongMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jlong) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jlong) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jlong) 0; }
    if (num_args == 0) {
        return (*env)->CallLongMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallLongMethodA(env, obj, methodID, arg_jvalues) : (jlong) 0;
    }
}

JNIEXPORT jshort JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeShortMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jshort) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jshort) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jshort) 0; }
    if (num_args == 0) {
        return (*env)->CallShortMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallShortMethodA(env, obj, methodID, arg_jvalues) : (jshort) 0;
    }
}

JNIEXPORT jchar JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeCharMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jchar) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jchar) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jchar) 0; }
    if (num_args == 0) {
        return (*env)->CallCharMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallCharMethodA(env, obj, methodID, arg_jvalues) : (jchar) 0;
    }
}

JNIEXPORT jboolean JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeBooleanMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jboolean) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jboolean) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jboolean) 0; }
    if (num_args == 0) {
        return (*env)->CallBooleanMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallBooleanMethodA(env, obj, methodID, arg_jvalues) : (jboolean) 0;
    }
}

JNIEXPORT jbyte JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeByteMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jbyte) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jbyte) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jbyte) 0; }
    if (num_args == 0) {
        return (*env)->CallByteMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallByteMethodA(env, obj, methodID, arg_jvalues) : (jbyte) 0;
    }
}

JNIEXPORT jfloat JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeFloatMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jfloat) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jfloat) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jfloat) 0; }
    if (num_args == 0) {
        return (*env)->CallFloatMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallFloatMethodA(env, obj, methodID, arg_jvalues) : (jfloat) 0;
    }
}

JNIEXPORT jdouble JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeDoubleMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return (jdouble) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jdouble) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jdouble) 0; }
    if (num_args == 0) {
        return (*env)->CallDoubleMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallDoubleMethodA(env, obj, methodID, arg_jvalues) : (jdouble) 0;
    }
}

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeObjectMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) {
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method)) { return NULL; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return NULL; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return NULL; }
    if (num_args == 0) {
        return (*env)->CallObjectMethod(env, obj, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallObjectMethodA(env, obj, methodID, arg_jvalues) : (jobject) NULL;
    }
}

// -----------------------------------------------------------------------------------------------------------------

// Invoke static methods

JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticVoidMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return; }
    if (num_args == 0) {
        (*env)->CallStaticVoidMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        if (unbox(env, method, args, num_args, arg_jvalues)) {
            (*env)->CallStaticVoidMethodA(env, cls, methodID, arg_jvalues);
        }
    }
}

JNIEXPORT jint JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticIntMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jint) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jint) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jint) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jint) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticIntMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticIntMethodA(env, cls, methodID, arg_jvalues) : (jint) 0;
    }
}

JNIEXPORT jlong JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticLongMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jlong) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jlong) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jlong) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jlong) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticLongMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticLongMethodA(env, cls, methodID, arg_jvalues) : (jlong) 0;
    }
}

JNIEXPORT jshort JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticShortMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jshort) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jshort) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jshort) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jshort) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticShortMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticShortMethodA(env, cls, methodID, arg_jvalues) : (jshort) 0;
    }
}

JNIEXPORT jchar JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticCharMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jchar) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jchar) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jchar) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jchar) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticCharMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticCharMethodA(env, cls, methodID, arg_jvalues) : (jchar) 0;
    }
}

JNIEXPORT jboolean JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticBooleanMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jboolean) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jboolean) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jboolean) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jboolean) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticBooleanMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticBooleanMethodA(env, cls, methodID, arg_jvalues) : (jboolean) 0;
    }
}

JNIEXPORT jbyte JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticByteMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jbyte) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jbyte) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jbyte) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jbyte) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticByteMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticByteMethodA(env, cls, methodID, arg_jvalues) : (jbyte) 0;
    }
}

JNIEXPORT jfloat JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticFloatMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jfloat) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jfloat) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jfloat) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jfloat) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticFloatMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticFloatMethodA(env, cls, methodID, arg_jvalues) : (jfloat) 0;
    }
}

JNIEXPORT jdouble JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticDoubleMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return (jdouble) 0; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return (jdouble) 0; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return (jdouble) 0; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return (jdouble) 0; }
    if (num_args == 0) {
        return (*env)->CallStaticDoubleMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticDoubleMethodA(env, cls, methodID, arg_jvalues) : (jdouble) 0;
    }
}

JNIEXPORT jobject JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invokeStaticObjectMethod(JNIEnv *env, jclass ignored, jobject method, jobjectArray args) {
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true)) { return NULL; }
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID);
    if (thrown(env)) { return NULL; }
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    if (thrown(env)) { return NULL; }
    jsize num_args = (*env)->GetArrayLength(env, args);
    if (thrown(env)) { return NULL; }
    if (num_args == 0) {
        return (*env)->CallStaticObjectMethod(env, cls, methodID);
    } else {
        jvalue arg_jvalues[num_args];
        return unbox(env, method, args, num_args, arg_jvalues) ? (*env)->CallStaticObjectMethodA(env, cls, methodID, arg_jvalues) : (jobject) NULL;
    }
}

