#include <jni.h>
#include <PTAM/TrackerData.h>
#include "PTAM/Tracker.h"
#include "PTAM/PtamSystem.h"
#include "Logger.h"
#include "TrackerRenderer.h"

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

JNIEXPORT jlong JNICALL
Java_mit_spbau_arptam_PtamSystem_nInitRenderer(JNIEnv* env, jclass type) {
    TrackerRenderer* renderer = new TrackerRenderer;
    return reinterpret_cast<jlong>(renderer);
}

JNIEXPORT void JNICALL
Java_mit_spbau_arptam_PtamSystem_nRenderTrackingInfo(JNIEnv* env, jclass type, jlong hSystem,
    jlong hRenderer) {
    PtamSystem* ptam = ptamSystem(hSystem);
    TrackerRenderer* renderer = reinterpret_cast<TrackerRenderer*>(hRenderer);
    const ImageRef& imageSize = ptam->image()->size();
    Vector<2> scale;
    scale[0] = 0.75f * 2.0f / static_cast<float>(imageSize.x);
    scale[1] = 2.0f / static_cast<float>(imageSize.y);

    Map* pMap = ptam->map();
    if (pMap->IsGood()) {
        //tracking
        renderer->setProgram();
        renderer->setColor(0, 1, 0);
        for (auto& mapPoint: pMap->vpPoints) {
            TrackerData* tData = mapPoint->pTData;
            if (tData) {
                renderer->renderPoint(
                    tData->v2Image[0] * scale[0] - 1.0,
                    1.0f - tData->v2Image[1] * scale[1]);
            }
        }
    } else {
        //initializing
        Tracker* pTracker = ptam->tracker();
        std::list<Trail> trails = pTracker->getTrails();
        renderer->setProgram();
        renderer->setColor(1, 1, 0);
        for (auto& trail: trails) {
            renderer->renderLine(
                trail.irInitialPos.x * scale[0] - 1.0,
                1.0 - trail.irInitialPos.y * scale[1],
                trail.irCurrentPos.x * scale[0] - 1.0,
                1.0 - trail.irCurrentPos.y * scale[1]);
        }
    }
}

}

