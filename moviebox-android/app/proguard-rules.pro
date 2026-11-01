# MovieBox ProGuard / R8 Anti-Tamper & Obfuscation Rules

# Scramble all classes, fields, and method names
-repackageclasses 'com.moviebox.app.internal'
-allowaccessmodification

# Remove all logging calls in production to prevent leaking sensitive API URLs & signatures
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep Data Models serialized with JSON
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep ExoPlayer Media3 Codecs and Decoders
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Obfuscate Security and Anti-Tamper Logic heavily
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Strip debug line numbers and source file names
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
