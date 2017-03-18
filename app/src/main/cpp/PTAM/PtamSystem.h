// -*- c++ -*-
// Copyright 2008 Isis Innovation Limited
// Modified by  2015
//
// System.h
//
// Defines the System class
//
// This stores the main functional classes of the system, like the
// mapmaker, map, tracker etc, and spawns the working threads.
//
#ifndef __SYSTEM_H
#define __SYSTEM_H

#include <cvd/image.h>
#include <cvd/rgb.h>
#include <cvd/byte.h>

#include "ThreadPool.h"

using namespace CVD;

namespace PTAM {

class ATANCamera;
class Map;
class MapMaker;
class Tracker;

class PtamSystem {
public:
    PtamSystem(const CVD::ImageRef& imageSize);
    void processFrame(CVD::Image<CVD::byte> frame);
    Map* map();
    MapMaker* mapMaker();
    Tracker* tracker();

    Image<byte>* image();

public:
    bool requestFinish;

private:
    Image<byte> mimFrameBW;

    Map* mMap;
    MapMaker* mMapMaker;
    Tracker* mTracker;
    ATANCamera* mCamera;
};
}

#endif
