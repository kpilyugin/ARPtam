# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             cvd

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             installfiles/cvd_src/bayer.cxx
             installfiles/cvd_src/brezenham.cc                       
             installfiles/cvd_src/colourspace_convert.cxx                       
             installfiles/cvd_src/connected_components.cc                      
             installfiles/cvd_src/convolution.cc                      
             installfiles/cvd_src/cvd_timer.cc                      
             installfiles/cvd_src/deinterlacebuffer.cc                      
             installfiles/cvd_src/diskbuffer2.cc                      
             installfiles/cvd_src/draw.cc                      
             installfiles/cvd_src/draw_toon.cc                      
             installfiles/cvd_src/eventobject.cpp                      
             installfiles/cvd_src/exceptions.cc                      
             installfiles/cvd_src/fast/fast_10_detect.cxx                      
             installfiles/cvd_src/fast/fast_10_score.cxx                      
             installfiles/cvd_src/fast/fast_11_detect.cxx                      
             installfiles/cvd_src/fast/fast_11_score.cxx                      
             installfiles/cvd_src/fast/fast_12_detect.cxx                      
             installfiles/cvd_src/fast/fast_12_score.cxx                      
             installfiles/cvd_src/fast/fast_7_detect.cxx                      
             installfiles/cvd_src/fast/fast_7_score.cxx                      
             installfiles/cvd_src/fast/fast_8_detect.cxx                      
             installfiles/cvd_src/fast/fast_8_score.cxx                      
             installfiles/cvd_src/fast/fast_9_detect.cxx                      
             installfiles/cvd_src/fast/fast_9_score.cxx                      
             installfiles/cvd_src/fast_corner.cxx                      
             installfiles/cvd_src/fast_corner_9_nonmax.cxx                      
             installfiles/cvd_src/image_io.cc                      
             installfiles/cvd_src/Linux/videosource_nov4l1buffer.cc                      
             installfiles/cvd_src/Linux/videosource_nov4lbuffer.cc                      
             installfiles/cvd_src/morphology.cc                      
             installfiles/cvd_src/noarch/convolve_gaussian.cc                      
             installfiles/cvd_src/noarch/default_memalign.cpp                      
             installfiles/cvd_src/noarch/gradient.cc                      
             installfiles/cvd_src/noarch/median_3x3.cc                      
             installfiles/cvd_src/noarch/two_thirds_sample.cc                      
             installfiles/cvd_src/noarch/utility_byte_differences.cc                      
             installfiles/cvd_src/noarch/utility_double_int.cc                      
             installfiles/cvd_src/noarch/utility_float.cc                      
             installfiles/cvd_src/noarch/yuv422_wrapper.cc                      
             installfiles/cvd_src/nonmax_suppression.cxx                      
             installfiles/cvd_src/thread/runnable_batch.cc                      
             installfiles/cvd_src/OSX/videosource_noqtbuffer.cc                      
             installfiles/cvd_src/noarch/slower_corner_10.cxx                      
             installfiles/cvd_src/slower_corner_11.cxx                      
             installfiles/cvd_src/slower_corner_12.cxx                      
             installfiles/cvd_src/slower_corner_7.cxx                      
             installfiles/cvd_src/slower_corner_8.cxx                      
             installfiles/cvd_src/slower_corner_9.cxx                      
             installfiles/cvd_src/synchronized.cpp                      
             installfiles/cvd_src/tensor_voting.cc                      
             installfiles/cvd_src/thread.cpp                      
             installfiles/cvd_src/timeddiskbuffer.cc                      
             installfiles/cvd_src/videosource.cpp                      
             installfiles/cvd_src/videosource_nodvbuffer.cc                      
             installfiles/cvd_src/videosource_novideofilebuffer.cc                      
             installfiles/cvd_src/yuv411_to_stuff.cxx                      
             installfiles/cvd_src/yuv420.cpp                      
             installfiles/cvd_src/yuv422.cpp                      
             installfiles/cvd_src/get_time_of_day_ns.cc                      
             installfiles/cvd_src/image_io/bmp_write.cc                      
             installfiles/cvd_src/image_io/bmp.cxx                      
             installfiles/cvd_src/image_io/cvdimage.cxx                      
             installfiles/cvd_src/image_io/fits.cc                      
             installfiles/cvd_src/image_io/fitswrite.cc                      
             installfiles/cvd_src/image_io/pnm_grok.cxx                      
             installfiles/cvd_src/image_io/save_postscript.cxx                      
             installfiles/cvd_src/image_io/text.cxx                      
             installfiles/cvd_src/image_io/text_write.cc                      
             installfiles/cvd_src/image_io/bmp_read.cc                      
             installfiles/cvd_src/image_io/png.cc                      
             installfiles/cvd_src/image_io/jpeg.cxx                      
             installfiles/cvd_src/glwindow.cc                      
             installfiles/cvd_src/gltext.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       cvd

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
