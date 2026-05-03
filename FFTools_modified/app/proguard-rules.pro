# Add project specific ProGuard rules here.

# Keep Shizuku classes
-keep class rikka.shizuku.** { *; }
-keep class moe.shizuku.** { *; }

# Keep AIDL generated classes
-keep class com.axeron.fftools.IFFToolsService { *; }
-keep class com.axeron.fftools.IFFToolsService$** { *; }
-keep class com.axeron.fftools.IFFToolsService$Stub { *; }

# Keep FFToolsUserService (dijalankan oleh Shizuku)
-keep class com.axeron.fftools.shizuku.FFToolsUserService { *; }

# Keep data models
-keep class com.axeron.fftools.data.** { *; }

# Keep hidden API bypass
-keep class org.lsposed.hiddenapibypass.** { *; }

# Keep refine
-keep class dev.rikka.tools.refine.** { *; }

# Standard Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-dontwarn kotlin.**
