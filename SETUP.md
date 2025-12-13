# دليل الإعداد والتطوير

## المتطلبات الأساسية
- Java 17 أو أحدث
- Android SDK 34
- Kotlin 1.9.0 أو أحدث
- Android Studio Hedgehog أو أحدث
- حساب Google لـ Firebase

## خطوات الإعداد الأولي

### 1. استنساخ المشروع
```bash
git clone <repository-url>
cd blood-donation-management
```

### 2. إعداد Firebase
1. اذهب إلى [Firebase Console](https://console.firebase.google.com)
2. أنشئ مشروعاً جديداً واسمه `Blood Donation Management`
3. أضف تطبيق Android:
   - Package Name: `com.blooddonation.management`
   - SHA-1: احصل عليها من `./gradlew signingReport`
4. حمّل `google-services.json`
5. ضعه في مجلد `app/`

### 3. تكوين المصادقة
في Firebase Console:
1. اذهب إلى Authentication
2. فعّل "Google Sign-In"
3. فعّل "Email/Password"

### 4. إعداد Firestore
في Firebase Console:
1. اذهب إلى Firestore Database
2. اختر "Start in production mode"
3. اختر المنطقة (يفضل `europe-west1`)
4. أضف القواعد التالية:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read, write: if request.auth.uid == uid;
      match /inventory/{document=**} {
        allow read, write: if request.auth.uid == uid;
      }
    }
  }
}
```

### 5. إعداد Cloud Messaging
1. في Firebase Console، اذهب إلى Cloud Messaging
2. احفظ Server API Key
3. فعّل Firebase Cloud Messaging في التطبيق

## بناء المشروع

### في Android Studio
1. فتح المشروع في Android Studio
2. اتركه يحمّل dependencies
3. اضغط على `Build` > `Make Project`

### في سطر الأوامر
```bash
./gradlew build
```

## تشغيل التطبيق

### على جهاز أو محاكي
```bash
./gradlew installDebug
./gradlew run
```

## هيكل المشروع

### Packages الرئيسية
- **data/**: قاعدة البيانات والـ Repositories
  - `local/`: Room Database و DAOs
  - `firebase/`: Firebase integration
  - `models/`: Data models
  - `repository/`: Repository patterns

- **ui/**: واجهات المستخدم
  - `fragments/`: Fragment screens
  - `activities/`: Activity screens
  - `adapters/`: RecyclerView adapters
  - `viewmodels/`: ViewModel classes

- **services/**: الخدمات
  - Firebase Cloud Messaging
  - Background services

- **receivers/**: Broadcast receivers
  - Alarm receivers للإشعارات

- **di/**: Dependency Injection (Hilt)

- **utils/**: أدوات مساعدة
  - Notification utilities
  - Date formatters
  - Binding adapters

## أهم الـ Data Models

### Category (الصنف)
```kotlin
data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val unit: String
)
```

### InventoryItem (عنصر المخزون)
```kotlin
data class InventoryItem(
    val id: Int,
    val categoryId: Int,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val expireDate: Long,
    val addedDate: Long,
    val notes: String,
    val status: String
)
```

### Distribution (الصرف)
```kotlin
data class Distribution(
    val id: Int,
    val inventoryId: Int,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val distributionDate: Long,
    val campaignName: String,
    val notes: String
)
```

### ReturnItem (المرتجع)
```kotlin
data class ReturnItem(
    val id: Int,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val returnDate: Long,
    val reason: String,
    val notes: String
)
```

## نظام الإشعارات

### كيف تعمل الإشعارات؟

1. **عند إضافة عنصر مخزون**:
   - يتم حساب تاريخ التنبيه (30 يوم قبل الانتهاء)
   - يتم جدولة alarm باستخدام `AlarmManager`

2. **في وقت التنبيه**:
   - يتم استدعاء `ExpirationAlarmReceiver`
   - يتم عرض notification

3. **في اللوحة الرئيسية**:
   - يتم عرض قائمة بالأصناف القريبة من الانتهاء

## خطوات التطوير

### إضافة شاشة جديدة
1. أنشئ Fragment جديد في `ui/fragments/`
2. أنشئ Layout XML في `res/layout/`
3. أنشئ ViewModel في `ui/viewmodels/`
4. أضفه إلى `nav_graph.xml`
5. أضف الـ navigation item

### إضافة ميزة جديدة للمخزون
1. أضف field للـ model في `Models.kt`
2. حدّث Database schema (version++)
3. حدّث الـ DAO queries
4. حدّث الـ Repository methods
5. حدّث الـ ViewModel

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

## Build APK للإطلاق

### Release APK
```bash
./gradlew assembleRelease
```

الملف سيكون في: `app/build/outputs/apk/release/app-release.apk`

## Troubleshooting

### المشكلة: Build fails مع Gradle error
**الحل**:
```bash
./gradlew clean
./gradlew build
```

### المشكلة: google-services.json not found
**الحل**: 
- تأكد من وضع الملف في `app/` directory
- لا تضعه في `app/src/main/`

### المشكلة: Firebase connection errors
**الحل**:
- تأكد من صحة `google-services.json`
- تأكد من أن SHA-1 في Firebase مطابق

### المشكلة: Notifications لا تظهر
**الحل**:
- تحقق من إذن POST_NOTIFICATIONS في Manifest
- تأكد من تفعيل notification channels في `BloodDonationApp.kt`

## إرشادات الكود

### Naming Conventions
- Activities: `MainActivity`, `LoginActivity`
- Fragments: `DashboardFragment`, `InventoryFragment`
- ViewModels: `CategoryViewModel`, `InventoryViewModel`
- Adapters: `CategoryAdapter`, `InventoryAdapter`
- DAOs: `CategoryDao`, `InventoryDao`

### Code Style
- استخدم Kotlin style guide الرسمي
- استخدم meaningful names
- أضف comments عند الحاجة
- استخدم null safety

### Git Workflow
```bash
# إنشاء branch جديد
git checkout -b feature/feature-name

# عمل commits صغيرة وواضحة
git commit -m "Add feature X"

# Push إلى remote
git push origin feature/feature-name

# عمل Pull Request
```

## الموارد المفيدة
- [Android Developer Docs](https://developer.android.com)
- [Firebase Docs](https://firebase.google.com/docs)
- [Kotlin Docs](https://kotlinlang.org/docs)
- [Material Design 3](https://m3.material.io)

---

**تاريخ آخر تحديث**: ديسمبر 2025
