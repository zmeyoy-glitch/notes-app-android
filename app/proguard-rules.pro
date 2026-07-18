# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep data classes and their properties
-keep class com.example.notesapp.data.** { *; }

# Keep SharedPreferences keys if needed
-keepattributes Signature
-keepattributes *Annotation*

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.* { *; }

# Keep inner classes
-keepclassmembers class * {
    <init>(...);
}