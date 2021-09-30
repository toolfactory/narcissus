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

// -----------------------------------------------------------------------------------------------------------------

bool thrown(JNIEnv* env) {
    return (*env)->ExceptionOccurred(env);
}

// -----------------------------------------------------------------------------------------------------------------

// Prelookup of frequently-used classes and methods:

jclass Class_class;
jmethodID Class_isArray_methodID;
jmethodID Class_getComponentType_methodID;

jclass void_class;

jclass Integer_class;
jclass int_class;
jmethodID Integer_intValue_methodID;

jclass Long_class;
jclass long_class;
jmethodID Long_longValue_methodID;

jclass Short_class;
jclass short_class;
jmethodID Short_shortValue_methodID;

jclass Character_class;
jclass char_class;
jmethodID Character_charValue_methodID;

jclass Boolean_class;
jclass boolean_class;
jmethodID Boolean_booleanValue_methodID;

jclass Byte_class;
jclass byte_class;
jmethodID Byte_byteValue_methodID;

jclass Float_class;
jclass float_class;
jmethodID Float_floatValue_methodID;

jclass Double_class;
jclass double_class;
jmethodID Double_doubleValue_methodID;

jmethodID Method_getDeclaringClass_methodID;
jmethodID Method_getModifiers_methodID;
jmethodID Method_getParameterTypes_methodID;
jmethodID Method_isVarArgs_methodID;
jmethodID Method_getReturnType_methodID;

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

    Class_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Class"));
    if (thrown(env)) { return -1; }
    Class_isArray_methodID = (*env)->GetMethodID(env, Class_class, "isArray", "()Z");
    if (thrown(env)) { return -1; }
    Class_getComponentType_methodID = (*env)->GetMethodID(env, Class_class, "getComponentType", "()Ljava/lang/Class;");
    if (thrown(env)) { return -1; }

    jclass Void_class = (*env)->FindClass(env, "java/lang/Void");
    if (thrown(env)) { return -1; }
    void_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Void_class, (*env)->GetStaticFieldID(env, Void_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    
    Integer_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Integer"));
    if (thrown(env)) { return -1; }
    int_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Integer_class, (*env)->GetStaticFieldID(env, Integer_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Integer_intValue_methodID = (*env)->GetMethodID(env, Integer_class, "intValue", "()I");
    if (thrown(env)) { return -1; }

    Long_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Long"));
    if (thrown(env)) { return -1; }
    long_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Long_class, (*env)->GetStaticFieldID(env, Long_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Long_longValue_methodID = (*env)->GetMethodID(env, Long_class, "longValue", "()J");
    if (thrown(env)) { return -1; }

    Short_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Short"));
    if (thrown(env)) { return -1; }
    short_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Short_class, (*env)->GetStaticFieldID(env, Short_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Short_shortValue_methodID = (*env)->GetMethodID(env, Short_class, "shortValue", "()S");
    if (thrown(env)) { return -1; }

    Character_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Character"));
    if (thrown(env)) { return -1; }
    char_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Character_class, (*env)->GetStaticFieldID(env, Character_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Character_charValue_methodID = (*env)->GetMethodID(env, Character_class, "charValue", "()C");
    if (thrown(env)) { return -1; }

    Boolean_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Boolean"));
    if (thrown(env)) { return -1; }
    boolean_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Boolean_class, (*env)->GetStaticFieldID(env, Boolean_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Boolean_booleanValue_methodID = (*env)->GetMethodID(env, Boolean_class, "booleanValue", "()Z");
    if (thrown(env)) { return -1; }

    Byte_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Byte"));
    if (thrown(env)) { return -1; }
    byte_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Byte_class, (*env)->GetStaticFieldID(env, Byte_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Byte_byteValue_methodID = (*env)->GetMethodID(env, Byte_class, "byteValue", "()B");
    if (thrown(env)) { return -1; }

    Float_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Float"));
    if (thrown(env)) { return -1; }
    float_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Float_class, (*env)->GetStaticFieldID(env, Float_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Float_floatValue_methodID = (*env)->GetMethodID(env, Float_class, "floatValue", "()F");
    if (thrown(env)) { return -1; }

    Double_class = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "java/lang/Double"));
    if (thrown(env)) { return -1; }
    double_class = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, Double_class, (*env)->GetStaticFieldID(env, Double_class, "TYPE", "Ljava/lang/Class;")));
    if (thrown(env)) { return -1; }
    Double_doubleValue_methodID = (*env)->GetMethodID(env, Double_class, "doubleValue", "()D");
    if (thrown(env)) { return -1; }
    
    jclass Method_class = (*env)->FindClass(env, "java/lang/reflect/Method");
    if (thrown(env)) { return -1; }
    Method_getDeclaringClass_methodID = (*env)->GetMethodID(env, Method_class, "getDeclaringClass", "()Ljava/lang/Class;");
    if (thrown(env)) { return -1; }
    Method_getModifiers_methodID = (*env)->GetMethodID(env, Method_class, "getModifiers", "()I");
    if (thrown(env)) { return -1; }
    Method_getParameterTypes_methodID = (*env)->GetMethodID(env, Method_class, "getParameterTypes", "()[Ljava/lang/Class;");
    if (thrown(env)) { return -1; }
    Method_isVarArgs_methodID = (*env)->GetMethodID(env, Method_class, "isVarArgs", "()Z");
    if (thrown(env)) { return -1; }
    Method_getReturnType_methodID = (*env)->GetMethodID(env, Method_class, "getReturnType", "()Ljava/lang/Class;");
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

    (*env)->DeleteGlobalRef(env, Class_class);
    
    (*env)->DeleteGlobalRef(env, void_class);

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

void printClassName(JNIEnv* env, jclass cls) {
    jstring strObj = (jstring) (*env)->CallObjectMethod(env, cls, (*env)->GetMethodID(env, Class_class, "getName", "()Ljava/lang/String;"));
    const char* str = (*env)->GetStringUTFChars(env, strObj, NULL);
    printf("%s\n", str);
    (*env)->ReleaseStringUTFChars(env, strObj, str);
}

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

bool checkPrimitiveMethodReturnType(JNIEnv* env, jobject method, jclass primitive_class) {
    jclass return_type = (*env)->CallObjectMethod(env, method, Method_getReturnType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_correct_return_type = (*env)->IsSameObject(env, return_type, primitive_class);
    if (thrown(env)) { return false; }
    if (!is_correct_return_type) {
        throwIllegalArgumentException(env, "Return type of method does not match primitive method invocation type");
        return false;
    }
    return true;
}

bool checkMethodReturnTypeNotVoidOrPrimitive(JNIEnv* env, jobject method) {
    jclass return_type = (*env)->CallObjectMethod(env, method, Method_getReturnType_methodID);
    if (thrown(env)) { return false; }
    jboolean is_void_return_type = (*env)->IsSameObject(env, return_type, void_class);
    if (thrown(env)) { return false; }
    if (is_void_return_type) {
        throwIllegalArgumentException(env, "Return type of method is void, but tried to invoke as method with Object return type");
        return false;
    }
    jboolean is_primitive_return_type = (*env)->IsSameObject(env, return_type, int_class)
            || (*env)->IsSameObject(env, return_type, long_class)
            || (*env)->IsSameObject(env, return_type, short_class)
            || (*env)->IsSameObject(env, return_type, char_class)
            || (*env)->IsSameObject(env, return_type, boolean_class)
            || (*env)->IsSameObject(env, return_type, byte_class)
            || (*env)->IsSameObject(env, return_type, float_class)
            || (*env)->IsSameObject(env, return_type, double_class);
    if (thrown(env)) { return false; }
    if (is_primitive_return_type) {
        throwIllegalArgumentException(env, "Return type of method is of primitive type, but tried to invoke as method with Object return type");
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
    jobject parameterTypes = (*env)->CallObjectMethod(env, method, Method_getParameterTypes_methodID);
    if (thrown(env)) { return 0; }
    jsize num_params = (*env)->GetArrayLength(env, parameterTypes);
    if (thrown(env)) { return 0; }
    jboolean is_varargs = (*env)->CallBooleanMethod(env, method, Method_isVarArgs_methodID);
    if (thrown(env)) { return 0; }
    jclass varargs_elt_type = NULL;
    if (is_varargs && num_params > 0) {
        jclass varargs_arr_type = (*env)->GetObjectArrayElement(env, parameterTypes, num_params - 1);
        if (thrown(env)) { return 0; }
        varargs_elt_type = (*env)->CallObjectMethod(env, varargs_arr_type, Class_getComponentType_methodID);
        if (thrown(env)) { return 0; }
    }
    jsize num_non_varargs_params = num_params - (is_varargs ? 1 : 0);

    // Check arg arity
    if ((!is_varargs && num_args != num_params) || (is_varargs && num_args < num_non_varargs_params)) {
        throwIllegalArgumentException(env, "Tried to invoke method with wrong number of arguments");
        return 0;
    }
    jsize num_varargs_args = num_args - num_non_varargs_params;
        
    // Unbox non-varargs args
    for (jsize i = 0; i < num_non_varargs_params; i++) {
        jobject param_type = (*env)->GetObjectArrayElement(env, parameterTypes, i);
        if (thrown(env)) { return 0; }
        jobject arg = (*env)->GetObjectArrayElement(env, args, i);
        if (thrown(env)) { return 0; }
        jclass arg_type = arg == NULL ? (jclass) NULL : (*env)->GetObjectClass(env, arg);
        if (thrown(env)) { return 0; }

#define TRY_UNBOX_ARG(_prim_type, _Prim_type, _Boxed_type, _jvalue_field) \
        if ((*env)->IsSameObject(env, param_type, _prim_type ## _class)) { \
            if (arg == NULL) { \
                throwIllegalArgumentException(env, "Tried to unbox a null argument; expected " #_Boxed_type); \
            } else if (!(*env)->IsSameObject(env, arg_type, _Boxed_type ## _class)) { \
                throwIllegalArgumentException(env, "Tried to unbox arg of wrong type; expected " #_Boxed_type); \
            } else { \
                arg_jvalues[i]._jvalue_field = (*env)->Call ## _Prim_type ## Method(env, arg, _Boxed_type ## _ ## _prim_type ## Value_methodID); \
            } \
        }
        TRY_UNBOX_ARG(int, Int, Integer, i)
        else TRY_UNBOX_ARG(long, Long, Long, j)
        else TRY_UNBOX_ARG(short, Short, Short, s)
        else TRY_UNBOX_ARG(char, Char, Character, c)
        else TRY_UNBOX_ARG(boolean, Boolean, Boolean, z)
        else TRY_UNBOX_ARG(byte, Byte, Byte, b)
        else TRY_UNBOX_ARG(float, Float, Float, f)
        else TRY_UNBOX_ARG(double, Double, Double, d)
        else {
            // Parameter type is not primitive -- check if arg is assignable from the parameter type
            if (arg != NULL && !(*env)->IsAssignableFrom(env, arg_type, param_type)) {
                throwIllegalArgumentException(env, "Tried to invoke function with arg of incompatible type");
            } else {
                arg_jvalues[i].l = arg;
            }
        }
        if (thrown(env)) { return 0; }
    }
    if (is_varargs) {
        // Unbox varargs, if varargs_elt_type is primitive
#define TRY_UNBOX_VARARGS(_prim_type, _Prim_type, _Boxed_type, _jvalue_field) \
        if ((*env)->IsSameObject(env, varargs_elt_type, _prim_type ## _class)) { \
            j ## _prim_type ## Array arr = (*env)->New ## _Prim_type ## Array(env, num_varargs_args); \
            arg_jvalues[num_non_varargs_params].l = arr; \
            if (thrown(env)) { return 0; } \
            j ## _prim_type *elts = (*env)->Get ## _Prim_type ## ArrayElements(env, arr, NULL); \
            if (!elts) { return 0; } \
            for (jsize i = 0; i < num_varargs_args; i++) { \
                jobject arg = (*env)->GetObjectArrayElement(env, args, i + num_non_varargs_params); \
                if (thrown(env)) { return 0; } \
                if (arg == NULL) { \
                    throwIllegalArgumentException(env, "Tried to unbox a null argument"); \
                    return 0; \
                } \
                jclass arg_type = (*env)->GetObjectClass(env, arg); \
                if (thrown(env)) { return 0; } \
                if (!(*env)->IsSameObject(env, arg_type, _Boxed_type ## _class)) { \
                    throwIllegalArgumentException(env, "Tried to unbox arg of wrong type"); \
                    return 0; \
                } else { \
                    elts[i] = (*env)->Call ## _Prim_type ## Method(env, arg, _Boxed_type ## _ ## _prim_type ## Value_methodID); \
                } \
            } \
            (*env)->Release ## _Prim_type ## ArrayElements(env, arr, elts, 0); \
        }
        TRY_UNBOX_VARARGS(int, Int, Integer, i)
        else TRY_UNBOX_VARARGS(long, Long, Long, j)
        else TRY_UNBOX_VARARGS(short, Short, Short, s)
        else TRY_UNBOX_VARARGS(char, Char, Character, c)
        else TRY_UNBOX_VARARGS(boolean, Boolean, Boolean, z)
        else TRY_UNBOX_VARARGS(byte, Byte, Byte, b)
        else TRY_UNBOX_VARARGS(float, Float, Float, f)
        else TRY_UNBOX_VARARGS(double, Double, Double, d)
        else {
            // varargs_elt_type is non-primitive -- check if each element of remaining args is assignable from varargs element type
            jobjectArray arr = arg_jvalues[num_non_varargs_params].l = (*env)->NewObjectArray(env, num_varargs_args, varargs_elt_type, NULL);
            if (thrown(env)) { return 0; }
            for (jsize i = 0; i < num_varargs_args; i++) {
                jobject arg = (*env)->GetObjectArrayElement(env, args, i + num_non_varargs_params);
                if (thrown(env)) { return 0; }
                jclass arg_type = arg == NULL ? (jclass) NULL : (*env)->GetObjectClass(env, arg);
                if (thrown(env)) { return 0; }
                if (arg != NULL && !(*env)->IsAssignableFrom(env, arg_type, varargs_elt_type)) {
                    throwIllegalArgumentException(env, "Tried to invoke function with varargs arg of incompatible type");
                } else {
                    (*env)->SetObjectArrayElement(env, arr, i, arg);
                }
                if (thrown(env)) { return 0; }
            }
        }
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
    const jmethodID methodID = (*env)->GetMethodID(env, Class_class, "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;");
    if (methodID == 0 || thrown(env)) { return NULL; }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// Get declared constructors without any visibility checks
JNIEXPORT jobjectArray JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDeclaredConstructors(JNIEnv *env, jclass ignored, jclass cls) {
    if (argIsNull(env, cls)) { return NULL; }
    const jmethodID methodID = (*env)->GetMethodID(env, Class_class, "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;");
    if (methodID == 0 || thrown(env)) { return NULL; }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// Get declared fields without any visibility checks
JNIEXPORT jobjectArray JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getDeclaredFields(JNIEnv *env, jclass ignored, jclass cls) {
    if (argIsNull(env, cls)) { return NULL; }
    const jmethodID methodID = (*env)->GetMethodID(env, Class_class, "getDeclaredFields0", "(Z)[Ljava/lang/reflect/Field;");
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

// Non-static field getters:

#define FIELD_GETTER(_prim_type, _Prim_type) \
JNIEXPORT j ## _prim_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_get ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject obj, jobject field) { \
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field)) { return (j ## _prim_type) 0; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return (jint) 0; } \
    return (*env)->Get ## _Prim_type ## Field(env, obj, fieldID); \
}

FIELD_GETTER(int, Int)

FIELD_GETTER(long, Long)

FIELD_GETTER(short, Short)

FIELD_GETTER(char, Char)

FIELD_GETTER(boolean, Boolean)

FIELD_GETTER(byte, Byte)

FIELD_GETTER(float, Float)

FIELD_GETTER(double, Double)

FIELD_GETTER(object, Object)

// Non-static field setters:

#define FIELD_SETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_set ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject obj, jobject field, j ## _prim_type val) { \
    if (argIsNull(env, obj) || argIsNull(env, field) || !checkFieldStaticModifier(env, field, false) || !checkFieldReceiver(env, obj, field) _extra_check) { return; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return; } \
    (*env)->Set ## _Prim_type ## Field(env, obj, fieldID, val); \
}

FIELD_SETTER(int, Int, )

FIELD_SETTER(long, Long, )

FIELD_SETTER(short, Short, )

FIELD_SETTER(char, Char, )

FIELD_SETTER(boolean, Boolean, )

FIELD_SETTER(byte, Byte, )

FIELD_SETTER(float, Float, )

FIELD_SETTER(double, Double, )

FIELD_SETTER(object, Object, || !checkFieldValType(env, field, val) )

// -----------------------------------------------------------------------------------------------------------------

// Static field getters:

#define STATIC_FIELD_GETTER(_prim_type, _Prim_type) \
JNIEXPORT j ## _prim_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_getStatic ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject field) { \
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true)) { return (j ## _prim_type) 0; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID); \
    if (thrown(env)) { return (j ## _prim_type) 0; } \
    return (*env)->GetStatic ## _Prim_type ## Field(env, cls, fieldID); \
}

STATIC_FIELD_GETTER(int, Int)

STATIC_FIELD_GETTER(long, Long)

STATIC_FIELD_GETTER(short, Short)

STATIC_FIELD_GETTER(char, Char)

STATIC_FIELD_GETTER(boolean, Boolean)

STATIC_FIELD_GETTER(byte, Byte)

STATIC_FIELD_GETTER(float, Float)

STATIC_FIELD_GETTER(double, Double)

STATIC_FIELD_GETTER(object, Object)

// Static field setters:

#define STATIC_FIELD_SETTER(_prim_type, _Prim_type, _extra_check) \
JNIEXPORT void JNICALL Java_io_github_toolfactory_narcissus_Narcissus_setStatic ## _Prim_type ## Field(JNIEnv *env, jclass ignored, jobject field, j ## _prim_type val) { \
    if (argIsNull(env, field) || !checkFieldStaticModifier(env, field, true) _extra_check) { return; } \
    jfieldID fieldID = (*env)->FromReflectedField(env, field); \
    if (thrown(env)) { return; } \
    jclass cls = (*env)->CallObjectMethod(env, field, Field_getDeclaringClass_methodID); \
    if (thrown(env)) { return; } \
    (*env)->SetStatic ## _Prim_type ## Field(env, cls, fieldID, val); \
}

STATIC_FIELD_SETTER(int, Int, )

STATIC_FIELD_SETTER(long, Long, )

STATIC_FIELD_SETTER(short, Short, )

STATIC_FIELD_SETTER(char, Char, )

STATIC_FIELD_SETTER(boolean, Boolean, )

STATIC_FIELD_SETTER(byte, Byte, )

STATIC_FIELD_SETTER(float, Float, )

STATIC_FIELD_SETTER(double, Double, )

STATIC_FIELD_SETTER(object, Object, || !checkFieldValType(env, field, val) )

// -----------------------------------------------------------------------------------------------------------------

// Invoke non-static methods

#define INVOKE_METHOD(_jni_ret_type, _prim_type, _Prim_type, _assign, _assign_return, _err_return, _extra_check) \
JNIEXPORT _jni_ret_type JNICALL Java_io_github_toolfactory_narcissus_Narcissus_invoke ## _Prim_type ## Method(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray args) { \
    if (argIsNull(env, obj) || argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, false) || !checkMethodReceiver(env, obj, method) _extra_check) { return _err_return; } \
    jmethodID methodID = (*env)->FromReflectedMethod(env, method); \
    if (thrown(env)) { return _err_return; } \
    jsize num_args = (*env)->GetArrayLength(env, args); \
    if (thrown(env)) { return _err_return; } \
    jvalue arg_jvalues[num_args == 0 ? 1 : num_args]; \
    if (unbox(env, method, args, num_args, arg_jvalues)) { \
        _assign (*env)->Call ## _Prim_type ## MethodA(env, obj, methodID, arg_jvalues); \
        return _assign_return; \
    } else {\
        return _err_return; \
    } \
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
    if (argIsNull(env, method) || argIsNull(env, args) || !checkMethodStaticModifier(env, method, true) _extra_check) { return _err_return; } \
    jclass cls = (*env)->CallObjectMethod(env, method, Method_getDeclaringClass_methodID); \
    if (thrown(env)) { return _err_return; } \
    jmethodID methodID = (*env)->FromReflectedMethod(env, method); \
    if (thrown(env)) { return _err_return; } \
    jsize num_args = (*env)->GetArrayLength(env, args); \
    if (thrown(env)) { return _err_return; } \
    jvalue arg_jvalues[num_args == 0 ? 1 : num_args]; \
    if (unbox(env, method, args, num_args, arg_jvalues)) { \
        _assign (*env)->CallStatic ## _Prim_type ## MethodA(env, cls, methodID, arg_jvalues); \
        return _assign_return; \
    } else { \
        return _err_return; \
    } \
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

