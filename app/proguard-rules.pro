-dontwarn org.slf4j.impl.StaticLoggerBinder
-keep, allowobfuscation, allowshrinking interface retrofit2.Call
-keep, allowobfuscation, allowshrinking class retrofit2.Response
-keep, allowobfuscation, allowshrinking class kotlin.coroutines.Continuation
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**