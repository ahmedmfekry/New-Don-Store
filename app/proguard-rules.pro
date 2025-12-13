proguard-rules.pro

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Room Database
-keep class androidx.room.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

# OkHttp
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# Your app classes
-keep class com.blooddonation.management.** { *; }
