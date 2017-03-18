// Copyright 2008 Isis Innovation Limited

#include "PtamSystem.h"
#include "ATANCamera.h"
#include "MapMaker.h"
#include "Tracker.h"
#include <cvd/image_io.h>
#include <cvd/convolution.h>
#include <cvd/vision.h>

//#define ENABLE_TIMING
#include "Timing.h"

namespace PTAM {

using namespace CVD;
using namespace std;

PtamSystem::PtamSystem(const ImageRef& size) {
    requestFinish = false;

    mimFrameBW.resize(size);

    mCamera = new ATANCamera("Camera", size);
    mCamera->SetImageSize(size);
    mMap = new Map;
    mMapMaker = new MapMaker(*mMap, *mCamera);
    mTracker = new Tracker(size, *mCamera, *mMap, *mMapMaker);
}

void PtamSystem::processFrame(CVD::Image<CVD::byte> image) {
    TIMER_INIT
    if (!requestFinish) {
        // Grab new video frame...
        TIMER_START
        mimFrameBW = image;
        TIMER_STOP("Get Frame")
    }

    if (!requestFinish) {
        mMapMaker->mbLockMap = 0;
        TIMER_START
        mTracker->TrackFrame(mimFrameBW);
        TIMER_STOP("Tracker")
    }
}

Map* PtamSystem::map() {
    return mMap;
}

MapMaker* PtamSystem::mapMaker() {
    return mMapMaker;
}

Tracker* PtamSystem::tracker() {
    return mTracker;
}

Image<byte>* PtamSystem::image() {
    return &mimFrameBW;
}
}


