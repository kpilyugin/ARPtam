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
Java_mit_spbau_ptam_PtamSystem_nCreate(JNIEnv* env, jclass type, jint width, jint height) {
    ImageRef imageSize(width, height);
    PtamSystem* system = new PtamSystem(imageSize);
    start_logger("ARSystem");
    return reinterpret_cast<jlong>(system);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nProcessFrame(JNIEnv* env, jclass type, jlong handle, jint width,
    jint height, jbyteArray jData) {
    PtamSystem* arSystem = reinterpret_cast<PtamSystem*>(handle);

    jsize size = width * height;
    Image<byte>* pImage = arSystem->image();
    env->GetByteArrayRegion(jData, 0, size, (jbyte*) pImage->data());
    arSystem->processFrame(*pImage);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nNextTrackingState(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    pTracker->PokeTracker();
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nReset(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    pTracker->Reset();
}

JNIEXPORT jstring JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetMessage(JNIEnv* env, jclass type, jlong handle) {
    Tracker* pTracker = ptamSystem(handle)->tracker();
    string message = pTracker->GetMessageForUser();
    return env->NewStringUTF(message.c_str());
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetMap(JNIEnv* env, jclass type, jlong handle) {
    return reinterpret_cast<jlong>(ptamSystem(handle)->map());
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_ptam_PtamSystem_nInitRenderer(JNIEnv* env, jclass type) {
    TrackerRenderer* renderer = new TrackerRenderer;
    return reinterpret_cast<jlong>(renderer);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nRenderTrackingInfo(JNIEnv* env, jclass type, jlong hSystem,
    jlong hRenderer) {
    PtamSystem* ptam = ptamSystem(hSystem);
    TrackerRenderer* renderer = reinterpret_cast<TrackerRenderer*>(hRenderer);
    const ImageRef& imageSize = ptam->image()->size();
    Vector<2> scale;
    scale[0] = 2.0f / static_cast<float>(imageSize.x);
    scale[1] = 2.0f / static_cast<float>(imageSize.y);

    Map* pMap = ptam->map();
    if (pMap->IsGood()) {
        //tracking
        pMap->LockMap();
        renderer->setProgram();
        renderer->setColor(0, 1, 0);
        for (auto& mapPoint: pMap->vpPoints) {
            TrackerData* tData = mapPoint->pTData;
            if (tData && mapPoint->bFoundRecent) {
                renderer->renderPoint(
                    tData->v2Image[0] * scale[0] - 1.0,
                    1.0f - tData->v2Image[1] * scale[1]);
            }
        }
        pMap->UnlockMap();
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

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetRotation(JNIEnv* env, jclass type, jlong handle, jfloatArray jRotation) {
    jfloat* rotation = env->GetFloatArrayElements(jRotation, NULL);
    const SE3<>& se3 = ptamSystem(handle)->tracker()->cameraPose();
    const Matrix<3, 3>& m = se3.get_rotation().get_matrix();
    for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 3; ++j) {
            rotation[3 * i + j] = (float) m[i][j];
        }
    }
    env->ReleaseFloatArrayElements(jRotation, rotation, 0);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetPosition(JNIEnv* env, jclass type, jlong handle, jfloatArray jPosition) {
    jfloat* position = env->GetFloatArrayElements(jPosition, NULL);
    const SE3<>& se3 = ptamSystem(handle)->tracker()->cameraPose();
    const Vector<3>& pos = se3.get_translation();
    for (int i = 0; i < 3; ++i) {
        position[i] = (float) pos[i];
    }
    env->ReleaseFloatArrayElements(jPosition, position, 0);
}

JNIEXPORT jdouble JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetLastTrackingTime(JNIEnv* env, jclass type, jlong handle) {
    return ptamSystem(handle)->tracker()->lastTrackingTime();
}

JNIEXPORT jfloat JNICALL
Java_mit_spbau_ptam_PtamSystem_nGetTrackingQuality(JNIEnv* env, jclass type, jlong handle) {
    return ptamSystem(handle)->tracker()->trackingQuality();
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nSaveMap(JNIEnv* env, jclass type, jlong handle, jstring fileName) {
    const char* name = env->GetStringUTFChars(fileName, false);
    ptamSystem(handle)->mapMaker()->SaveMap(name);
    env->ReleaseStringUTFChars(fileName, name);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_PtamSystem_nLoadMap(JNIEnv* env, jclass type, jlong handle, jstring fileName) {
    const char* name = env->GetStringUTFChars(fileName, false);
    ptamSystem(handle)->mapMaker()->LoadMap(name);
    env->ReleaseStringUTFChars(fileName, name);
}

JNIEXPORT jint JNICALL
Java_mit_spbau_ptam_Map_nGetNumPoints(JNIEnv *env, jclass type, jlong handle) {
    return (jint) reinterpret_cast<Map*>(handle)->vpPoints.size();
}

JNIEXPORT jint JNICALL
Java_mit_spbau_ptam_Map_nGetNumKeyFrames(JNIEnv *env, jclass type, jlong handle) {
    return (jint) reinterpret_cast<Map*>(handle)->vpKeyFrames.size();
}

JNIEXPORT jlong JNICALL
Java_mit_spbau_ptam_Map_nGetMapPoint(JNIEnv *env, jclass type, jlong handle, jint i) {
    MapPoint *point = reinterpret_cast<Map*>(handle)->vpPoints[i];
    return reinterpret_cast<jlong>(point);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_MapPoint_nGetImagePos(JNIEnv *env, jclass type, jlong handle,
                                            jfloatArray imagePos_) {
    jfloat *imagePos = env->GetFloatArrayElements(imagePos_, NULL);
    MapPoint* mapPoint = reinterpret_cast<MapPoint*>(handle);
    TrackerData* tData = mapPoint->pTData;
    if (tData && mapPoint->bFoundRecent) {
        imagePos[0] = (jfloat) tData->v2Image[0];
        imagePos[1] = (jfloat) tData->v2Image[1];
    }
    env->ReleaseFloatArrayElements(imagePos_, imagePos, 0);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_MapPoint_nGetWorldPos(JNIEnv *env, jclass type, jlong handle,
                                            jfloatArray worldPos_) {
    jfloat *worldPos = env->GetFloatArrayElements(worldPos_, NULL);
    MapPoint* mapPoint = reinterpret_cast<MapPoint*>(handle);
    worldPos[0] = (jfloat) mapPoint->v3WorldPos[0];
    worldPos[1] = (jfloat) mapPoint->v3WorldPos[1];
    worldPos[2] = (jfloat) mapPoint->v3WorldPos[2];
    env->ReleaseFloatArrayElements(worldPos_, worldPos, 0);
}

JNIEXPORT void JNICALL
Java_mit_spbau_ptam_MapPoint_nGetLocalPos(JNIEnv *env, jclass type, jlong handle,
                                            jfloatArray localPos_) {
    jfloat *localPos = env->GetFloatArrayElements(localPos_, NULL);
    MapPoint* mapPoint = reinterpret_cast<MapPoint*>(handle);
    TrackerData* tData = mapPoint->pTData;
    if (tData && mapPoint->bFoundRecent) {
        localPos[0] = (jfloat) tData->v3Cam[0];
        localPos[1] = (jfloat) tData->v3Cam[1];
        localPos[2] = (jfloat) tData->v3Cam[2];
    }
    env->ReleaseFloatArrayElements(localPos_, localPos, 0);
}

JNIEXPORT jboolean JNICALL
Java_mit_spbau_ptam_MapPoint_nIsTracked(JNIEnv *env, jclass type, jlong handle) {
    MapPoint* mapPoint = reinterpret_cast<MapPoint*>(handle);
    return (jboolean) mapPoint->bFoundRecent;
}
}

