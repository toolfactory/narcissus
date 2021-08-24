#include <jni.h>

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_getDeclaredMethods(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_getDeclaredConstructors(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredConstructors0", "(Z)[Ljava/lang/reflect/Constructor;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

JNIEXPORT jobjectArray JNICALL Java_narcissus_Narcissus_getDeclaredFields(JNIEnv *env, jclass ignored, jclass cls) {
    const jclass clsDescriptor = (*env)->GetObjectClass(env, cls); // Class -> Class.class
    const jmethodID methodID = (*env)->GetMethodID(env, clsDescriptor, "getDeclaredFields0", "(Z)[Ljava/lang/reflect/Field;");
    if (methodID == 0) {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, cls, methodID, (jboolean) 0);
}

// -----------------------------------------------------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_narcissus_Narcissus_getIntFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetIntField(env, obj, fieldID);
}

JNIEXPORT jlong JNICALL Java_narcissus_Narcissus_getLongFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetLongField(env, obj, fieldID);
}

JNIEXPORT jshort JNICALL Java_narcissus_Narcissus_getShortFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetShortField(env, obj, fieldID);
}

JNIEXPORT jchar JNICALL Java_narcissus_Narcissus_getCharFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetCharField(env, obj, fieldID);
}

JNIEXPORT jboolean JNICALL Java_narcissus_Narcissus_getBooleanFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetBooleanField(env, obj, fieldID);
}

JNIEXPORT jbyte JNICALL Java_narcissus_Narcissus_getByteFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetByteField(env, obj, fieldID);
}

JNIEXPORT jfloat JNICALL Java_narcissus_Narcissus_getFloatFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetFloatField(env, obj, fieldID);
}

JNIEXPORT jdouble JNICALL Java_narcissus_Narcissus_getDoubleFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetDoubleField(env, obj, fieldID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_getObjectFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    return (*env)->GetObjectField(env, obj, fieldID);
}

// ----

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setIntFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jint val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetIntField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setLongFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jlong val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetLongField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setShortFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jshort val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetShortField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setCharFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jchar val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetCharField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setBooleanFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jboolean val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetBooleanField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setByteFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jbyte val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetByteField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setFloatFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jfloat val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetFloatField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setDoubleFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jdouble val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetDoubleField(env, obj, fieldID, val);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_setObjectFieldVal(JNIEnv *env, jclass ignored, jobject obj, jobject field, jobject val) {
    jfieldID fieldID = (*env)->FromReflectedField(env, field);
    (*env)->SetObjectField(env, obj, fieldID, val);
}

// -----------------------------------------------------------------------------------------------------------------

// TODO: method calls that take parameters are not yet supported, the `params` parameter is ignored
// See https://stackoverflow.com/a/30961708/3950982

JNIEXPORT jint JNICALL Java_narcissus_Narcissus_callIntMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallIntMethod(env, obj, methodID);
}

JNIEXPORT jlong JNICALL Java_narcissus_Narcissus_callLongMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallLongMethod(env, obj, methodID);
}

JNIEXPORT jshort JNICALL Java_narcissus_Narcissus_callShortMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallShortMethod(env, obj, methodID);
}

JNIEXPORT jchar JNICALL Java_narcissus_Narcissus_callCharMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallCharMethod(env, obj, methodID);
}

JNIEXPORT jboolean JNICALL Java_narcissus_Narcissus_callBooleanMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallBooleanMethod(env, obj, methodID);
}

JNIEXPORT jbyte JNICALL Java_narcissus_Narcissus_callByteMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallByteMethod(env, obj, methodID);
}

JNIEXPORT jfloat JNICALL Java_narcissus_Narcissus_callFloatMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallFloatMethod(env, obj, methodID);
}

JNIEXPORT jdouble JNICALL Java_narcissus_Narcissus_callDoubleMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallDoubleMethod(env, obj, methodID);
}

JNIEXPORT void JNICALL Java_narcissus_Narcissus_callVoidMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    (*env)->CallVoidMethod(env, obj, methodID);
}

JNIEXPORT jobject JNICALL Java_narcissus_Narcissus_callObjectMethod(JNIEnv *env, jclass ignored, jobject obj, jobject method, jobjectArray params) {
    jmethodID methodID = (*env)->FromReflectedMethod(env, method);
    return (*env)->CallObjectMethod(env, obj, methodID);
}

