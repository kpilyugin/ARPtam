APP_STL := gnustl_shared
NDK_TOOLCHAIN_VERSION := 4.8
APP_CPPFLAGS := -frtti -fexceptions  -O3 -Ofast -ffast-math -ftree-vectorize -std=gnu++0x -Wno-deprecated -pthread -std=c++11
#APP_OPTIM := release
APP_OPTIM := debug

#-mfloat-abi=softfp -mfpu=neon