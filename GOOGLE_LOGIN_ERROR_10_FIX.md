# 🔧 دليل حل مشكلة Google Login Error 10

## ❌ المشكلة
```
Error 10: DEVELOPER_ERROR
```
هذا الخطأ يحدث عندما يكون توقيع التطبيق (APK Signing) مختلفاً عن ما هو مسجل في Firebase Console.

---

## ✅ الحلول

### الحل 1: تصحيح توقيع APK (الأهم)

#### الخطوة 1: احصل على بصمة التوقيع من جهازك
```bash
# Windows - في مجلد التطبيق
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

ستجد `SHA1` و `MD5` في النتيجة.

#### الخطوة 2: أضف البصمات إلى Firebase Console
1. اذهب إلى [Firebase Console](https://console.firebase.google.com)
2. اختر مشروعك: `blood-donation-managemen-ac914`
3. اذهب إلى **Project Settings** ⚙️
4. اختر تبويب **Service Accounts**
5. تحت **Your apps** اختر تطبيقك `com.blooddonation.management`
6. اضغط على **Add fingerprint** أو **Add SHA-1**
7. الصق البصمة التي حصلت عليها

---

### الحل 2: إعادة بناء وتنظيف التطبيق

```bash
# امسح ملفات البناء السابقة
./gradlew.bat clean

# أعد البناء من الصفر
./gradlew.bat assembleDebug

# تثبيت على الجهاز
./gradlew.bat installDebug
```

---

### الحل 3: تحقق من google-services.json

تأكد من أن الملف موجود في المسار الصحيح:
```
app/google-services.json
```

**المحتوى الصحيح:**
```json
{
  "project_info": {
    "project_number": "857503009188",
    "project_id": "blood-donation-managemen-ac914"
  },
  "client": [
    {
      "client_info": {
        "package_name": "com.blooddonation.management"
      },
      "oauth_client": [
        {
          "client_id": "857503009188-qm2k0ijdarhjp15vugjjpklbr2a9fj3j.apps.googleusercontent.com",
          "client_type": 3
        }
      ]
    }
  ]
}
```

---

### الحل 4: تحقق من strings.xml

تأكد من وجود هذا السطر في `app/src/main/res/values/strings.xml`:
```xml
<string name="default_web_client_id">857503009188-qm2k0ijdarhjp15vugjjpklbr2a9fj3j.apps.googleusercontent.com</string>
```

---

### الحل 5: تحقق من build.gradle

تأكد من وجود هذه الأسطر:
```gradle
dependencies {
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    
    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx")
}

plugins {
    id("com.google.gms.google-services")
}
```

---

## 🧪 خطوات الاختبار

### 1. اختبر على جهاز حقيقي (الأهم)
```bash
# تثبيت على جهاز حقيقي
./gradlew.bat installDebug

# شغل التطبيق
adb shell am start -n com.blooddonation.management/.ui.MainActivity
```

### 2. اختبر في Firebase Emulator (اختياري)
```bash
# شغل Firebase Emulator
firebase emulators:start

# في strings.xml استخدم
<string name="firebase_database_url">http://localhost:9000?ns=blood-donation-managemen-ac914</string>
```

### 3. تفعيل Debug Logging
أضف هذا قبل Google Sign-In:
```kotlin
// في LoginFragment.kt
private fun enableDebugLogging() {
    try {
        val c = Class.forName("com.google.android.gms.auth.GooglePlayServicesUtil")
        val method = c.getMethod("setGmsSdkVersion", Int::class.java)
        method.invoke(null, 16020000)
    } catch (e: Exception) {
        Log.e("LoginFragment", "Debug logging error: ${e.message}")
    }
}
```

---

## 📊 جدول الأخطاء والحلول

| كود الخطأ | المعنى | الحل |
|---------|--------|------|
| **10** | توقيع APK مختلف | أضف SHA-1 إلى Firebase Console |
| 12500 | خطأ في التكوين | تحقق من google-services.json |
| 12501 | ألغى المستخدم | لا حل - يختار المستخدم |
| 12502 | لا إنترنت | تحقق من الاتصال |
| 12503 | خطأ الخادم | أعد المحاولة لاحقاً |

---

## 🔐 خطوات الإنتاج (Release)

عندما تريد نشر التطبيق على Play Store:

### 1. أنشئ مفتاح توقيع إنتاجي
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### 2. احصل على SHA-1 للمفتاح الإنتاجي
```bash
keytool -list -v -keystore my-release-key.jks -alias my-key-alias
```

### 3. أضف الـ SHA-1 إلى Firebase Console
(كما في الحل 1 أعلاه)

### 4. وقع التطبيق بالمفتاح الإنتاجي
```bash
./gradlew.bat assembleRelease \
  -Dorg.gradle.project.keystore=my-release-key.jks \
  -Dorg.gradle.project.storePassword=YOUR_PASSWORD \
  -Dorg.gradle.project.keyAlias=my-key-alias \
  -Dorg.gradle.project.keyPassword=YOUR_KEY_PASSWORD
```

---

## 🆘 إذا لم تنجح الحلول السابقة

### تفعيل Google Play Services على الجهاز
1. افتح **Google Play Store** على الجهاز
2. ابحث عن **Google Play Services**
3. اضغط **Update** أو **Install**
4. انتظر حتى ينتهي التحديث

### امسح بيانات Google Play Services
```bash
adb shell pm clear com.google.android.gms
adb shell pm clear com.google.android.gsf
```

### أعد تشغيل الجهاز
```bash
adb reboot
```

---

## 📝 ملخص الحل السريع

```bash
# 1. احصل على SHA-1
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# 2. أضفه إلى Firebase Console (الخطوة الأهم!)

# 3. نظف وأعد البناء
./gradlew.bat clean assembleDebug

# 4. ثبت على الجهاز
./gradlew.bat installDebug

# 5. اختبر التسجيل
# افتح التطبيق واضغط على Google Sign-In
```

---

## ✨ معايير النجاح

✅ **تم النجاح عندما:**
- تظهر نافذة Google Sign-In بدون أخطاء
- تستطيع اختيار حسابك
- يتم الانتقال للـ Dashboard بعد التسجيل
- تظهر بيانات المستخدم في Firebase Console

❌ **لم تنجح إذا:**
- يظهر Error 10 أو أخطاء أخرى
- النافذة لا تظهر بتاتاً
- تحدث أخطاء في Logcat

---

## 📞 الدعم التقني

اذا استمرت المشكلة:
1. تحقق من **Logcat** للأخطاء التفصيلية
2. راجع **Firebase Console** للتنبيهات
3. تأكد من **الاتصال بالإنترنت**
4. جرب على **جهاز آخر** أو **محاكي مختلف**

---

**آخر تحديث:** 14/12/2025
