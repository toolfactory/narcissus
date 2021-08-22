#include <jni.h>

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeGetObjectFieldVal(JNIEnv *env, jclass cls, jobject obj, jstring fieldName, jstring fieldSig) {
    const jclass objClass = (*env)->GetObjectClass(env, obj);
    const char* fieldNameUTF8 = (*env)->GetStringUTFChars(env, fieldName, NULL);
    const char* fieldSigUTF8 = (*env)->GetStringUTFChars(env, fieldSig, NULL);
    const jfieldID fieldID = (*env)->GetFieldID(env, objClass, fieldNameUTF8, fieldSigUTF8);
    (*env)->ReleaseStringUTFChars(env, fieldSig, fieldSigUTF8);
    (*env)->ReleaseStringUTFChars(env, fieldName, fieldNameUTF8);
    return (*env)->GetObjectField(env, obj, fieldID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeCallObjectMethod(JNIEnv *env, jclass cls, jobject obj, jstring methodName, jstring methodSig) {
    const jclass objClass = (*env)->GetObjectClass(env, obj);
    const char* methodNameUTF8 = (*env)->GetStringUTFChars(env, methodName, NULL);
    const char* methodSigUTF8 = (*env)->GetStringUTFChars(env, methodSig, NULL);
    const jmethodID methodID = (*env)->GetMethodID(env, objClass, methodNameUTF8, methodSigUTF8);
    (*env)->ReleaseStringUTFChars(env, methodSig, methodSigUTF8);
    (*env)->ReleaseStringUTFChars(env, methodName, methodNameUTF8);
    return (*env)->CallObjectMethod(env, obj, methodID);
}

