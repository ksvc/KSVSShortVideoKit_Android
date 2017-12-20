# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xiaoqiang/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-allowaccessmodification
-verbose
-optimizations !code/simplification/arithmetic,!field/*,field/propagation/value,!class/merging/*,!code/allocation/variable

-ignorewarnings
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes *Annotation*
-dontoptimize

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.ksyun.ts.shortvideo.** {
    *;
}
-keep class com.ksyun.ts.skin.** {
    *;
}

# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-keep class okio.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# RxJava RxAndroid
-keep class io.reactivex.** {
    *;
}
-dontwarn io.reactivex.**
#GSON
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}
# 播放
-dontwarn com.ksyun.media.**
-keep class com.ksyun.media.** { *;}
# KS3
-dontwarn com.ksyun.ks3.**
-dontwarn org.apache.**
-keep class com.ksyun.ks3.** { *;}
-keep class org.apache.** { *;}

-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.leakcanary.** { *;}
-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.** { *;}

-keep class com.ksy.statlibrary.** {
  *;
}

-keep class com.sensetime.sensear.** {
*;
}

-keep class com.sensetime.sensear.** {
*;
}

-keep class com.googlecode.mp4parser.** {
  *;
}

-keep class com.mp4parser.** {
  *;
}

-keep class com.coremedia.iso.** {
  *;
}

-keep class com.ksyun.ts.ShortVideoDemo.** {
  *;
}
