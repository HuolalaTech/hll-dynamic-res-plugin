#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lalamove_huolala_client_nativelib_DynamicLib_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from dynamic lib C++";
    return env->NewStringUTF(hello.c_str());
}