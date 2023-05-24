#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_lalamove_huolala_client_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from native lib C++";
    return env->NewStringUTF(hello.c_str());
}