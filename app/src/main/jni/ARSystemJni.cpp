#include <jni.h>
#include <iostream>
#include <sstream>

#include "AR/ARSystem.h"
#include "Logger.h"

using namespace AR;

extern "C" {

ARSystem* arSystem(jlong handle) {
    return reinterpret_cast<ARSystem*>(handle);
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_arptam_ARSystem_nCreate(JNIEnv* env, jclass type) {
    ARSystem* arSystem = new ARSystem;
    arSystem->setInitConfiguration(arSystem->initConfiguration());
    arSystem->setTrackingConfiguration(arSystem->trackingConfiguration());
    arSystem->setCameraParameters(arSystem->cameraParameters());
    arSystem->setMapPointsDetectorConfiguration(MapPointsDetectorConfiguration());
    start_logger("ARSystem");
    return reinterpret_cast<jlong>(arSystem);
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_ARSystem_nProcessFrame(JNIEnv* env, jclass type, jlong handle,
                                             jint width, jint height, jbyteArray jData) {
    jsize size = width * height;
    jbyte* data = new jbyte[size];
    env->GetByteArrayRegion(jData, 0, size, data);
    Image<uchar>* frame = new Image<uchar>(Point2i(width, height));
    const int area = frame->area();
    uchar* bwPtr = frame->data();

    for (int i = 0; i < area; ++i, ++bwPtr) {
        *bwPtr = uchar(data[i]);
    }
    ARSystem* arSystem = reinterpret_cast<ARSystem*>(handle);
    arSystem->process(*frame);
    delete[] data;
    delete frame;
}

JNIEXPORT jint JNICALL
Java_mit_spbau_arptam_ARSystem_nGetTrackingState(JNIEnv* env, jclass type, jlong handle) {
    TrackingState state = arSystem(handle)->trackingState();
    return static_cast<jint>(state);
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_ARSystem_nNextTrackingState(JNIEnv* env, jclass type, jlong handle) {
    arSystem(handle)->nextTrackingState();
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_ARSystem_nRenderTrackingInfo(JNIEnv* env, jclass type, jlong handle) {
    arSystem(handle)->renderTrackingInfo();
}

JNIEXPORT jstring JNICALL
Java_mit_spbau_arptam_ARSystem_nGetTrackingInfo(JNIEnv* env, jclass type, jlong handle) {
    std::stringstream ss;
    ARSystem* arSystem = reinterpret_cast<ARSystem*>(handle);
    TrackingState state = arSystem->trackingState();
    ss << "Tracking state: " << state << std::endl;
    if (state == TrackingState::Tracking) {
        ss << "Tracking quality: " << arSystem->trackingQuality() << std::endl;
        ss << "Current translation: " << arSystem->currentTranslation() << std::endl;
        ss << "Current rotation: " << arSystem->currentRotation() << std::endl;
        ss << "Number of map points: " << arSystem->map()->countMapPoints() << std::endl;
        ss << "Number of keyframes: " << arSystem->map()->countKeyFrames() << std::endl;
    }
    return env->NewStringUTF(ss.str().c_str());
}

}

