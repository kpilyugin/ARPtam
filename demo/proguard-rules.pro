# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kirill/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontpreverify
-dontoptimize

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service

-keep class * extends com.google.protobuf.nano.MessageNano { *; }
# -keep class com.google.vrtoolkit.cardboard.** { *; }

-keep class org.mozilla.javascript.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

# add here what we use from jni
-keep class delightex.math.Vec3f { *; }
-keep class delightex.math.Quat { *; }
-keep class delightex.jni.JniTest { *; }
-keep class delightex.opengl.GLFast { *; }


#jetpad
-dontwarn com.google.gwt.**
-dontwarn org.eclipse.jetty.**
-dontwarn javax.servlet.**
-dontwarn jetbrains.jetpad.transport.http.rpc.server.**
-dontwarn jetbrains.jetpad.transport.socket.servlet.**
-dontwarn java.lang.ThreadLocal
-dontwarn javax.swing.**
-dontwarn java.awt.**

# Guava 20.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.google.errorprone.annotations.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue

#opengl
-dontwarn javax.microedition.khronos.**

# Cardboard sdk
-dontwarn com.google.vr.cardboard.**
-dontwarn android.opengl.**
-dontwarn android.os.Trace
-dontwarn android.view.Display
-dontwarn com.google.vrtoolkit.**
-dontwarn com.google.vr.vrcore.**
-dontwarn com.google.vr.ndk.**