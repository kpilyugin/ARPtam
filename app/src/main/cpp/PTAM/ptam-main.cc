//#include <string.h>
////#include <sstream>
//#include <iostream>     // std::streambuf, std::cout
//#include <fstream>      // std::ofstream
//#include <stdlib.h>
//#include <android/log.h>
//#include <jni.h>
//#include <unistd.h>
//#include "CameraCalibrator.h"
//#include "PtamSystem.h"
//#include "Tracker.h"
//
////#define ENABLE_TIMING
//#include "Timing.h"
//
//using namespace std;
//using namespace GVars3;
//using namespace PTAM;
//
//extern "C" {
//
//JavaVM* jvm = NULL;
//System* msys;
//CameraCalibrator* mcal;
//bool docalibration = false;
//
//
//static double last = now_ms();
//
//std::streambuf* psbuf, * coutbackup, * cerrbackup;
//std::ofstream filestr;
//
//int start_logger(const char* app_name);
//static void* thread_func(void*);
//
////init PTAM
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeInit(JNIEnv* env, jobject thiz, jboolean calib) {
//    //init jni
//    env->GetJavaVM(&jvm);
//
//    //redirect cout and cerr for debugging purposes
//    /*filestr.open((GLWindow2::getFDir() + "coutlog.txt").c_str(), std::ofstream::out | std::ofstream::app);
//    __android_log_print(ANDROID_LOG_INFO, "coutfile", "%s",
//                        (GLWindow2::getFDir() + "coutlog.txt").c_str());
//
//    // back up old streambuffers
//    coutbackup = std::cout.rdbuf();
//    cerrbackup = std::cerr.rdbuf();
//
//    //change streambuffers
//    psbuf = filestr.rdbuf();
//    std::cout.rdbuf(psbuf);
//    std::cerr.rdbuf(psbuf);
//*/
//    start_logger("PTAM");
//    //add log time
//    time_t _tm = time(NULL);
//    struct tm* curtime = localtime(&_tm);
//    cout << endl << endl;
//    cout << "==========================================================" << endl;
//    cout << "New Log: " << asctime(curtime) << endl;
//    cout << "==========================================================" << endl;
//    fflush(stdout);
//
//    //redirect done...
//
//    //init PTAM
//    if (!docalibration) {
//        GUI.LoadFile(GLWindow2::getFDir() + "camera.cfg");
//        GUI.LoadFile(GLWindow2::getFDir() + "settings.cfg");
//    }
//    else {
//        //GV3::set_var("Camera.Parameters","[0.5 0.75 0.5 0.5 0.1 ]");
//        //GV3::set_var("Camera.UseOpenCVDistortion","0");
//        GUI.LoadFile(GLWindow2::getFDir() + "calibrator_settings.cfg");
//        //GUI.StopParserThread();
//    }
//
//    GUI.StartParserThread(); // Start parsing of the console input
//    atexit(GUI.StopParserThread);
//
//    docalibration = calib;
//
//    if (docalibration) {
//        GV3::get<Vector<NUMTRACKERCAMPARAMETERS> >("Camera.Parameters", ATANCamera::mvDefaultParams,
//                                                   SILENT);
//    }
//
//    try {
//        if (docalibration)
//            mcal = new CameraCalibrator();
//        else
//            msys = new System();
//    }
//    catch (CVD::Exceptions::All e) {
//        cout << endl;
//        cout << "Error when initializing System: " << e.what << std::endl;
//    }
//}
//
////clean up
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeDestroy(JNIEnv* env, jobject thiz) {
//    if (docalibration) {
//        delete mcal;
//        mcal = NULL;
//    }
//    else {
//        delete msys;
//        msys = NULL;
//    }
//    if (filestr.is_open()) {
//        std::cout.rdbuf(coutbackup);        // restore cout's original streambuf
//        std::cerr.rdbuf(cerrbackup);        // restore cout's original streambuf
//        filestr.close();
//    }
//}
//
////initialize OpenGL
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeInitGL(JNIEnv* env, jobject thiz) {
//    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
//
//#ifdef USE_OGL2
//    gles2h.InitializeShaders();
//#endif
//}
//
//static int framecount = 0;
//
////render and process a new frame
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeRender(JNIEnv* env, jobject thiz) {
//    framecount++;
//    if (now_ms() - last > 1000) {
//        cout << "FPS: " << framecount << endl;
//        last = now_ms();
//        framecount = 0;
//    }
//
//    if (docalibration) {
//        mcal->Run();
//    } else {
//        msys->Run();
//    }
//}
//
////check if finished
//JNIEXPORT bool JNICALL
//Java_com_android_ptam_PtamActivity_nativeFinish(JNIEnv* env, jobject thiz) {
//    if (docalibration)
//        return true;
//    else {
//        msys->requestFinish = true;
//        msys->Run();
//        return msys->finished;
//    }
//}
//
////forward mouse click to ptam
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeClick(JNIEnv* env, jobject thiz, jint x, jint y) {
//    if (docalibration)
//        mcal->mGLWindow.on_mouse_down(x, y);
//    else
//        msys->mGLWindow.on_mouse_down(x, y);
//}
//
////forward keyboard to ptam
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeKey(JNIEnv* env, jobject thiz, jint keycode) {
//    if (docalibration)
//        mcal->mGLWindow.on_key_down(keycode);
//    else
//        msys->mGLWindow.on_key_down(keycode);
//}
//
////resize window (might only work once)
//JNIEXPORT void JNICALL
//Java_com_android_ptam_PtamActivity_nativeResize(JNIEnv* env, jobject thiz, jint w, jint h) {
//    if (docalibration)
//        mcal->mGLWindow.resize(w, h);
//    else
//        msys->mGLWindow.resize(w, h);
//}
//
//bool cachedfdir = false;
//std::string fdir = "";
//
////get config dir
//std::string PTAM::GLWindow2::getFDir() {
//    if (!cachedfdir) {
//        //todo check if smth wrong in here as i had segfault without caching fdir
//
//        if (jvm == NULL) return "";
//        JNIEnv* env;
//        jvm->AttachCurrentThread(&env, NULL);
//        jclass main = env->FindClass("com/android/ptam/PtamActivity");
//        jmethodID getFDir = env->GetStaticMethodID(main, "getFDir",
//                                                   "()Ljava/lang/String;");
//        jstring result = (jstring) env->CallStaticObjectMethod(main, getFDir);
//
//        jboolean iscopy;
//        const char* elems = env->GetStringUTFChars(result, &iscopy);
//        //std::string fdir;
//        fdir.append(elems, env->GetStringUTFLength(result));
//        env->ReleaseStringUTFChars(result, elems);
//        fdir = fdir + "/";
//        cachedfdir = true;
//    }
//    return fdir;
//}
//
////hacky solution to allow rendering text in opengl es
//JNICALL const void PTAM::GLWindow2::drawTextJava(std::string s, CVD::ImageRef irPos,
//                                                  int shaderid) const {
//    if (jvm == NULL) return;
//    JNIEnv* env;
//    jvm->AttachCurrentThread(&env, NULL);
//    jclass main = env->FindClass("com/android/ptam/PtamActivity");
//    jmethodID drawText = env->GetStaticMethodID(main, "drawText", "(Ljava/lang/String;III)V");
//    jstring text = env->NewStringUTF(s.c_str());
//    jint x = irPos.x;
//    jint y = irPos.y;
//    env->CallStaticVoidMethod(main, drawText, text, x, y, (jint) shaderid);
//    env->DeleteLocalRef(text);
//    env->DeleteLocalRef(main);
//}
//
////get image size
//JNICALL void VideoSource::getSize(int* sizeBuffer) {
//    if (jvm == NULL) return;
//    JNIEnv* env;
//    jvm->AttachCurrentThread(&env, NULL);
//    jclass main = env->FindClass("com/android/ptam/PtamActivity");
//    jmethodID getVideoSource = env->GetStaticMethodID(main, "getVideoSource",
//                                                      "()Lcom/android/ptam/VideoSource;");
//    jobject vs = env->CallStaticObjectMethod(main, getVideoSource);
//    jclass videosource = env->FindClass("com/android/ptam/VideoSource");
//    jmethodID getSize = env->GetMethodID(videosource, "getSize", "()[I");
//    jintArray result = (jintArray) env->CallObjectMethod(vs, getSize);
//    int len = env->GetArrayLength(result);
//
//    jboolean iscopy;
//    jint* elems = env->GetIntArrayElements(result, &iscopy);
//    sizeBuffer[0] = elems[0];
//    sizeBuffer[1] = elems[1];
//    env->ReleaseIntArrayElements(result, elems, JNI_ABORT);
//    env->DeleteLocalRef(main);
//    env->DeleteLocalRef(vs);
//    env->DeleteLocalRef(videosource);
//}
//
////get rotation data gained from androids sensors matching the last requested image
//JNICALL void Tracker::getRotation(double* rotMat) {
//    if (jvm == NULL) return;
//    JNIEnv* env;
//    jvm->AttachCurrentThread(&env, NULL);
//    jclass main = env->FindClass("com/android/ptam/PtamActivity");
//    jmethodID getVideoSource = env->GetStaticMethodID(main, "getVideoSource",
//                                                      "()Lcom/android/ptam/VideoSource;");
//    jobject vs = env->CallStaticObjectMethod(main, getVideoSource);
//    jclass videosource = env->FindClass("com/android/ptam/VideoSource");
//    jmethodID getRotationJ = env->GetMethodID(videosource, "getRotation", "()[F");
//    jfloatArray result = (jfloatArray) env->CallObjectMethod(vs, getRotationJ);
//    int len = env->GetArrayLength(result);
//
//    jboolean iscopy;
//    jfloat* elems = env->GetFloatArrayElements(result, &iscopy);
//    for (int i = 0; i < 9; i++)
//        rotMat[i] = elems[i];
//    env->ReleaseFloatArrayElements(result, elems, JNI_ABORT);
//}
//
////adapt camera brightness correction (unused)
//JNICALL void VideoSource::changeBrightness(int change) {
//    if (jvm == NULL) return;
//    JNIEnv* env;
//    jvm->AttachCurrentThread(&env, NULL);
//    jclass main = env->FindClass("com/android/ptam/PtamActivity");
//    jmethodID getVideoSource = env->GetStaticMethodID(main, "getVideoSource",
//                                                      "()Lcom/android/ptam/VideoSource;");
//    jobject vs = env->CallStaticObjectMethod(main, getVideoSource);
//    jclass videosource = env->FindClass("com/android/ptam/VideoSource");
//    jmethodID getFrame = env->GetMethodID(videosource, "changeBrightness", "(I)V");
//    env->CallVoidMethod(vs, getFrame, change);
//}
//
////get frame data from java
//JNICALL void VideoSource::getFrame(CVD::Image<CVD::byte>* imBW,
//                                   CVD::Image<CVD::Rgb<CVD::byte> >* imRGB, int width, int height) {
//    if (jvm == NULL) return;
//    TIMER_INIT
//    TIMER_START
//    JNIEnv* env;
//    jvm->AttachCurrentThread(&env, NULL);
//    jclass main = env->FindClass("com/android/ptam/PtamActivity");
//    jmethodID getVideoSource = env->GetStaticMethodID(main, "getVideoSource",
//                                                      "()Lcom/android/ptam/VideoSource;");
//    jobject vs = env->CallStaticObjectMethod(main, getVideoSource);
//    jclass videosource = env->FindClass("com/android/ptam/VideoSource");
//    jmethodID getFrame = env->GetMethodID(videosource, "getFrame", "()[B");
//    jbyteArray array = (jbyteArray) env->CallObjectMethod(vs, getFrame);
//    TIMER_STOP("Call Java")
//    TIMER_START
//
//    if (array == NULL) {
//        env->DeleteLocalRef(main);
//        env->DeleteLocalRef(vs);
//        env->DeleteLocalRef(array);
//        return;
//    }
//
//    int len = env->GetArrayLength(array);
//    jboolean frame_copy;
//
//    imBW->resize(mirSize);
//
//    env->GetByteArrayRegion(array, 0, width * height, (jbyte*) imBW->data());
//
//    TIMER_STOP("BW image")
//    TIMER_START
//
//
//    if (imRGB != NULL) {
//        //use yuv shader to render image directly instead of converting it to rgb first (which is slow)
//        //hack use rgb image for yuv data!
//        imRGB->resize(mirSize);
//        env->GetByteArrayRegion(array, 0, width * height * 1.5, (jbyte*) imRGB->data());
//    }
//
//    TIMER_STOP("RGB image")
//    env->DeleteLocalRef(main);
//    env->DeleteLocalRef(vs);
//    env->DeleteLocalRef(array);
//}
//
//static int pfd[2];
//static pthread_t thr;
//static const char* tag = "myapp";
//
//static void* thread_func(void*) {
//    ssize_t rdsz;
//    char buf[128];
//    while ((rdsz = read(pfd[0], buf, sizeof buf - 1)) > 0) {
//        if (buf[rdsz - 1] == '\n') --rdsz;
//        buf[rdsz] = 0;  /* add null-terminator */
//        __android_log_write(ANDROID_LOG_INFO, tag, buf);
//    }
//    return 0;
//}
//
//int start_logger(const char* app_name) {
//    tag = app_name;
//
//    /* make stdout line-buffered and stderr unbuffered */
//    setvbuf(stdout, 0, _IOLBF, 0);
//    setvbuf(stderr, 0, _IONBF, 0);
//
//    /* create the pipe and redirect stdout and stderr */
//    pipe(pfd);
//    dup2(pfd[1], 1);
//    dup2(pfd[1], 2);
//
//    /* spawn the logging thread */
//    if (pthread_create(&thr, 0, thread_func, 0) == -1)
//        return -1;
//    pthread_detach(thr);
//    return 0;
//}
//
//}
