#通用
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

# Kotlin协程（Retrofit suspend函数必加！不加R8打包直接失败）
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Retrofit & OkHttp & Okio
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson JSON序列化
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# 保留你自己项目所有data class、api类！改成你的包名 com.chail.yvkari
-keep class com.chail.yvkari.data.** { *; }
-keep class com.chail.yvkari.chat.api.** { *; }

# Compose 基础兜底规则
-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.lifecycle.** { *; }

# Kotlin标准库
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**
