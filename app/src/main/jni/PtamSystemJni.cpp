#include <jni.h>
#include "PTAM/Tracker.h"
#include "PTAM/PtamSystem.h"
#include "Logger.h"

using namespace PTAM;
using namespace CVD;

extern "C" {

PtamSystem* ptamSystem(jlong handle) {
    return reinterpret_cast<PtamSystem*>(handle);
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_arptam_PtamSystem_nCreate(JNIEnv* env, jclass type, jint width, jint height) {
    ImageRef imageSize(width, height);
    PtamSystem* system = new PtamSystem(imageSize);
    start_logger("ARSystem");
    return reinterpret_cast<jlong>(system);
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_PtamSystem_nProcessFrame(JNIEnv* env, jclass type, jlong handle, jint width,
                                               jint height, jbyteArray jData) {
    PtamSystem* arSystem = reinterpret_cast<PtamSystem*>(handle);

    jsize size = width * height;
    Image<byte>* pImage = arSystem->image();
    env->GetByteArrayRegion(jData, 0, size, (jbyte*) pImage->data());
    arSystem->processFrame(*pImage);
    env->ReleaseByteArrayElements(jData, (jbyte*) pImage->data(), 0);
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_PtamSystem_nNextTrackingState(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    pTracker->PokeTracker();
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_PtamSystem_nReset(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    pTracker->Reset();
}

JNIEXPORT jstring JNICALL
Java_mit_spbau_arptam_PtamSystem_nGetMessage(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    string message = pTracker->GetMessageForUser();
    return env->NewStringUTF(message.c_str());
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_arptam_PtamSystem_nGetMap(JNIEnv* env, jclass type, jlong handle) {
    return reinterpret_cast<jlong>(ptamSystem(handle)->map());
}

}

