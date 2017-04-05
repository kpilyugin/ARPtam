# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_subdirectory(ndk-modules/agast)
add_subdirectory(ndk-modules/cvd)

include_directories(src/main/cpp)
include_directories(ndk-modules)
include_directories(ndk-modules/agast/include/agast)
include_directories(ndk-modules/TooN/include)
include_directories(ndk-modules/cvd/installfiles)
include_directories(ndk-modules/tinyxml2/tinyxml2-master)

add_library( # Sets the name of the library.
            native-lib

            # Sets the library as a shared library.
            SHARED

            # Provides a relative path to your source file(s).
            # Associated headers in the same location as their source
            # file are automatically included.

            src/main/cpp/PTAM/ATANCamera.cc
            src/main/cpp/PTAM/Bundle.cc
            src/main/cpp/PTAM/HomographyInit.cc
            src/main/cpp/PTAM/KeyFrame.cc
            src/main/cpp/PTAM/Map.cc
            src/main/cpp/PTAM/MapMaker.cc
            src/main/cpp/PTAM/MapPoint.cc
            src/main/cpp/PTAM/MapSerialization.cc
            src/main/cpp/PTAM/MiniPatch.cc
            src/main/cpp/PTAM/PatchFinder.cc
            src/main/cpp/PTAM/PtamSystem.cc
            src/main/cpp/PTAM/Relocaliser.cc
            src/main/cpp/PTAM/ShiTomasi.cc
            src/main/cpp/PTAM/SmallBlurryImage.cc
            src/main/cpp/PTAM/Tracker.cc
            src/main/cpp/PTAM/ThreadPool.cc

            src/main/jni/PtamSystemJni.cpp
            src/main/cpp/Logger.cpp
            src/main/cpp/TrackerRenderer.cpp
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library(log-lib log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib
                       agast
                       cvd

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       GLESv2
                       EGL)
