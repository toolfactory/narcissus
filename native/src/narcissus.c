#include <jni.h>
#include <stdio.h>

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeGetObjectFieldVal(JNIEnv *env, jclass ignored, jobject obj, jstring fieldName, jstring fieldSig) {
    const jclass objClass = (*env)->GetObjectClass(env, obj);
    const char* fieldNameUTF8 = (*env)->GetStringUTFChars(env, fieldName, NULL);
    const char* fieldSigUTF8 = (*env)->GetStringUTFChars(env, fieldSig, NULL);
    const jfieldID fieldID = (*env)->GetFieldID(env, objClass, fieldNameUTF8, fieldSigUTF8);
    (*env)->ReleaseStringUTFChars(env, fieldSig, fieldSigUTF8);
    (*env)->ReleaseStringUTFChars(env, fieldName, fieldNameUTF8);
    if (fieldID == 0) {
        return NULL;
    }
    return (*env)->GetObjectField(env, obj, fieldID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeCallObjectMethod(JNIEnv *env, jclass ignored, jobject obj, jstring methodName, jstring methodSig) {
    const jclass objClass = (*env)->GetObjectClass(env, obj);
    const char* methodNameUTF8 = (*env)->GetStringUTFChars(env, methodName, NULL);
    const char* methodSigUTF8 = (*env)->GetStringUTFChars(env, methodSig, NULL);
    const jmethodID methodID = (*env)->GetMethodID(env, objClass, methodNameUTF8, methodSigUTF8);
    (*env)->ReleaseStringUTFChars(env, methodSig, methodSigUTF8);
    (*env)->ReleaseStringUTFChars(env, methodName, methodNameUTF8);
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, obj, methodID);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredMethods(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class object -> class object's descriptor
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, clsDescriptor, methodID);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredConstructors(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class object -> class object's descriptor
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, clsDescriptor, methodID);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredFields(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class object -> class object's descriptor
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredFields0", "(Z)[Ljava/lang/reflect/Field;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, clsDescriptor, methodID);
}

