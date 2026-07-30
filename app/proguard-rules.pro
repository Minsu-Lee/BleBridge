# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ============================================================
# Stack Trace (릴리스 크래시 로그 대비)
# ============================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# ============================================================
# Kotlin
# ============================================================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**


# ============================================================
# Domain (kotlin.jvm 모듈 - consumerProguardFiles 사용 불가)
# 코드에서 직접 참조되어 R8이 사용된 멤버는 자동으로 추적·보존하지만,
# 클래스 자체의 제거·리네임까지 막으려면 app 모듈에서 직접 keep해야 한다.
# ============================================================
-keep class com.jackson.blebridge.domain.**


# ============================================================
# Core Common (kotlin.jvm 모듈 - consumerProguardFiles 사용 불가)
# ============================================================
-keep class com.jackson.blebridge.core.common.**


# ============================================================
# Hilt ViewModel (AAR 내 번들 규칙 보완)
# hilt-android.pro에는 EntryPoint 리플렉티브 캐스트용 keep만 포함되어 있고,
# @HiltViewModel 클래스 자체는 보호하지 않는다.
# ============================================================
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}


# ============================================================
# 아래 항목은 Nevera 프로젝트에 있었지만 BleBridge에는 해당 의존성이 없어 제외함.
# 의존성이 추가되면 그때 함께 추가할 것.
# - Firebase ComponentRegistrar (Firebase 미사용)
# - Retrofit / OkHttp / Gson (네트워킹 라이브러리 미사용)
# - Room (미사용)
# - WorkManager / FCM (미사용)
# ============================================================