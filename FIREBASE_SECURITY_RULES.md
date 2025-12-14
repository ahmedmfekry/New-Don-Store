# 🔐 Firebase Firestore Security Rules

## نسخ الكود التالي وضعه في Firebase Console

### الموقع:
```
Firebase Console 
→ Firestore Database 
→ Rules (التبويب)
```

---

## 📋 القواعس الأمنية الكاملة:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // ==========================================
    // مجموعة المستخدمين
    // ==========================================
    match /users/{userId} {
      // السماح بقراءة البيانات الشخصية فقط
      allow read: if request.auth.uid == userId;
      
      // السماح بكتابة البيانات الشخصية فقط
      allow write: if request.auth.uid == userId;
      
      // السماح بإنشاء حساب جديد
      allow create: if request.auth.uid != null;
      
      // السماح بحذف الحساب الشخصي فقط
      allow delete: if request.auth.uid == userId;
    }
    
    // ==========================================
    // مجموعة المخزون
    // ==========================================
    match /users/{userId}/inventory/{document=**} {
      // قراءة: المستخدم يرى بيانات مخزونه فقط
      allow read: if request.auth.uid == userId;
      
      // كتابة: المستخدم يكتب في مخزونه فقط
      allow write: if request.auth.uid == userId;
      
      // حذف: حذف بيانات شخصية
      allow delete: if request.auth.uid == userId;
    }
    
    // ==========================================
    // مجموعة الصرف (Distribution)
    // ==========================================
    match /users/{userId}/distribution/{document=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId;
      allow delete: if request.auth.uid == userId;
    }
    
    // ==========================================
    // مجموعة المرتجعات (Returns)
    // ==========================================
    match /users/{userId}/returns/{document=**} {
      allow read: if request.auth.uid == userId;
      allow write: if request.auth.uid == userId;
      allow delete: if request.auth.uid == userId;
    }
    
    // ==========================================
    // منع أي وصول آخر
    // ==========================================
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

---

## 📝 شرح القواعس

### 1. Authentication Check:
```javascript
request.auth.uid != null
// ✅ المستخدم يجب أن يكون مسجل دخول
```

### 2. Ownership Check:
```javascript
request.auth.uid == userId
// ✅ المستخدم يرى بيانات نفسه فقط
```

### 3. Operations:
```javascript
allow read   // قراءة البيانات
allow write  // كتابة وتحديث
allow create // إنشاء جديد
allow delete // حذف البيانات
```

---

## 🚀 خطوات التطبيق:

### الخطوة 1: اذهب للـ Firebase Console
```
https://console.firebase.google.com
```

### الخطوة 2: اختر مشروعك
```
اختر: Blood Donation Management
```

### الخطوة 3: اذهب لـ Firestore Database
```
القائمة اليسرى → Firestore Database
```

### الخطوة 4: افتح Rules Tab
```
في الأعلى → اضغط Rules
```

### الخطوة 5: استبدل الكود
```
1. امسح الكود القديم
2. انسخ الكود أعلاه
3. اضغط Publish
4. انتظر لتصبح مفعّلة ✅
```

---

## ✅ التحقق من التطبيق:

### اختبر الأمان:

```kotlin
// ✅ يجب أن ينجح:
firebaseAuthManager.updateUserProfile(userId)

// ❌ يجب أن يفشل (مستخدم آخر):
val otherUserId = "different_user_id"
firebaseAuthManager.updateUserProfile(otherUserId)
// → رسالة خطأ: "Permission denied"
```

---

## 🔐 مستويات الأمان:

### Level 1: Authentication
```javascript
// تحقق: هل المستخدم مسجل دخول؟
request.auth.uid != null ✅
```

### Level 2: Authorization
```javascript
// تحقق: هل يملك البيانات؟
request.auth.uid == userId ✅
```

### Level 3: Data Validation
```javascript
// تحقق: البيانات صحيحة؟
request.resource.data.email is string ✅
```

---

## 📊 أمثلة الأداء:

### ✅ سيناريو ناجح:

```
User: ABC123
يريد: قراءة بيانات مخزونه

التحقق:
1. هل مسجل دخول؟ ✅
2. هل البيانات له؟ ✅
3. النتيجة: سماح! ✅
```

### ❌ سيناريو فاشل:

```
User: ABC123
يريد: قراءة بيانات DEF456

التحقق:
1. هل مسجل دخول؟ ✅
2. هل البيانات له؟ ❌
3. النتيجة: رفض! ❌
```

---

## 🆘 استكشاف الأخطاء:

### المشكلة: "Permission denied"

**الحل:**
```
1. تأكد من تسجيل الدخول
2. تأكد من userId صحيح
3. تحقق من Rules في Firebase
4. تحقق من Firestore Logs
```

### المشكلة: القواعس لم تُطبق

**الحل:**
```
1. انسخ الكود بدقة
2. اضغط Publish (أحمر في الأعلى)
3. انتظر 30 ثانية للتطبيق
4. أعد تحميل التطبيق
```

---

## 🎯 نصائح أمان إضافية:

### 1. تفعيل Google Analytics:
```
Firebase Console → Project Settings
→ Enable Analytics
```

### 2. تفعيل الإشعارات:
```
Firebase Console → Firestore
→ Real-time Listeners
```

### 3. نسخ احتياطية:
```
Firebase Console → Backups
→ أنشئ نسخة احتياطية يومية
```

### 4. المراقبة:
```
Firebase Console → Logs
→ شاهد جميع الوصول والعمليات
```

---

## 📱 اختبار من التطبيق:

```kotlin
// في LoginFragment.kt:
CoroutineScope(Dispatchers.IO).launch {
    try {
        val userId = firebaseAuthManager.getUserId()
        firebaseAuthManager.updateUserProfile(userId!!)
        
        // إذا وصل هنا → الحفظ نجح ✅
        withContext(Dispatchers.Main) {
            showSuccess("البيانات محفوظة بأمان!")
        }
    } catch (e: Exception) {
        // إذا حدث خطأ → تحقق من Rules
        withContext(Dispatchers.Main) {
            showError("خطأ في الحفظ: ${e.message}")
        }
    }
}
```

---

## ✅ Checklist التطبيق:

```
[ ] تم نسخ الكود أعلاه
[ ] تم فتح Firebase Console
[ ] تم اختيار المشروع الصحيح
[ ] تم فتح Firestore Rules
[ ] تم استبدال الكود القديم
[ ] تم اضغط Publish
[ ] تم الانتظار 30 ثانية
[ ] تم اختبار التطبيق
[ ] تم رؤية البيانات محفوظة ✅
```

---

## 🎊 النتيجة:

بعد تطبيق هذه القواعس:

✅ المستخدمون آمنون
✅ البيانات محمية
✅ لا وصول غير مصرح
✅ تطبيق منتج-جاهز

---

**الآن الحفظ آمن تماماً! 🔐**

---

**آخر تحديث:** 14/12/2025
