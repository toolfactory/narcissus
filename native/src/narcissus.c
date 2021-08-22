#include <jni.h>

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredMethods(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredConstructors(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_nativeGetDeclaredFields(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredFields0", "(Z)[Ljava/lang/reflect/Field;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// -----------------------------------------------------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_narcissus_Narcissus_nativeGetIntFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetIntField(env, obj, fieldID);
}

JNIEXPORT jlong JNICALL Java_narcissus_Narcissus_nativeGetLongFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetLongField(env, obj, fieldID);
}

JNIEXPORT jshort JNICALL Java_narcissus_Narcissus_nativeGetShortFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetShortField(env, obj, fieldID);
}

JNIEXPORT jchar JNICALL Java_narcissus_Narcissus_nativeGetCharFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetCharField(env, obj, fieldID);
}

JNIEXPORT jboolean JNICALL Java_narcissus_Narcissus_nativeGetBooleanFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetBooleanField(env, obj, fieldID);
}

JNIEXPORT jbyte JNICALL Java_narcissus_Narcissus_nativeGetByteFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetByteField(env, obj, fieldID);
}

JNIEXPORT jfloat JNICALL Java_narcissus_Narcissus_nativeGetFloatFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetFloatField(env, obj, fieldID);
}

JNIEXPORT jdouble JNICALL Java_narcissus_Narcissus_nativeGetDoubleFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetDoubleField(env, obj, fieldID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeGetObjectFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetObjectField(env, obj, fieldID);
}

// -----------------------------------------------------------------------------------------------------------------

// TODO: method calls that take parameters are not yet supported, the `params` parameter is ignored
// See https://stackoverflow.com/a/30961708/3950982

JNIEXPORT jint JNICALL Java_narcissus_Narcissus_nativeCallIntMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallIntMethod(env, obj, methodID);
}

JNIEXPORT jlong JNICALL Java_narcissus_Narcissus_nativeCallLongMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallLongMethod(env, obj, methodID);
}

JNIEXPORT jshort JNICALL Java_narcissus_Narcissus_nativeCallShortMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallShortMethod(env, obj, methodID);
}

JNIEXPORT jchar JNICALL Java_narcissus_Narcissus_nativeCallCharMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallCharMethod(env, obj, methodID);
}

JNIEXPORT jboolean JNICALL Java_narcissus_Narcissus_nativeCallBooleanMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallBooleanMethod(env, obj, methodID);
}

JNIEXPORT jbyte JNICALL Java_narcissus_Narcissus_nativeCallByteMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallByteMethod(env, obj, methodID);
}

JNIEXPORT jfloat JNICALL Java_narcissus_Narcissus_nativeCallFloatMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallFloatMethod(env, obj, methodID);
}

JNIEXPORT jdouble JNICALL Java_narcissus_Narcissus_nativeCallDoubleMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallDoubleMethod(env, obj, methodID);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_nativeCallVoidMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    (*env)->CallVoidMethod(env, obj, methodID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_nativeCallObjectMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallObjectMethod(env, obj, methodID);
}

