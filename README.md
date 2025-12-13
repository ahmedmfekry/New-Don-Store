# تطبيق إدارة مخزون المركز الإقليمي لنقل الدم

## نظرة عامة
تطبيق Android متكامل لإدارة مخزون المركز الإقليمي لنقل الدم بالإسماعيلية، يوفر واجهات احترافية لإدارة الأصناف والمخزون والصرف والمرتجعات مع نظام إشعارات متقدم.

## المميزات الرئيسية

### 1. إدارة الأصناف
- إضافة أصناف جديدة
- تعديل الأصناف الموجودة
- حذف الأصناف
- قائمة منسدلة للأصناف في العمليات الأخرى

### 2. إدارة المخزون
- إضافة مخزون جديد (طلبيات)
- تتبع تاريخ الإضافة
- تسجيل LOT Number وتاريخ الانتهاء
- إضافة ملاحظات
- البحث عن المخزون

### 3. صرف المخزون
- صرف الأصناف للحملات الخارجية
- تسجيل بيانات الصرف (التاريخ، الصنف، الكمية، الوحدة)
- توثيق LOT Number وتاريخ الانتهاء
- إضافة ملاحظات

### 4. إدارة المرتجعات
- تسجيل المرتجعات من الحملات
- تسجيل سبب الإرجاع
- البحث والفرز

### 5. لوحة التحكم
- عرض الأصناف القريبة من انتهاء الصلاحية
- إحصائيات سريعة
- تنبيهات مهمة

### 6. نظام الإشعارات
- تنبيهات عند اقتراب انتهاء الصلاحية (قبل شهر)
- إشعارات Firebase
- اهتزاز وأصوات للتنبيهات الحرجة

### 7. التكامل مع Firebase
- المصادقة عبر Google
- حفظ البيانات في Firestore
- قاعدة بيانات محلية مع Room

### 8. واجهة احترافية
- دعم كامل للعربية (RTL)
- تصميم Material Design 3
- ألوان احترافية
- Cards و RecyclerViews محسّنة

## المتطلبات التقنية

### Gradle Dependencies
- AndroidX Core & AppCompat
- Material Design Components
- Navigation Components
- Room Database
- Firebase (Auth, Firestore, Messaging)
- Retrofit & OkHttp
- Hilt Dependency Injection
- Kotlin Coroutines & Flow

## البنية المعمارية

```
com.blooddonation.management/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── *Dao.kt
│   ├── models/
│   │   └── Models.kt
│   └── repository/
│       └── BloodDonationRepository.kt
├── di/
│   └── DatabaseModule.kt
├── receivers/
│   └── ExpirationAlarmReceiver.kt
├── services/
│   └── NotificationService.kt
├── ui/
│   ├── MainActivity.kt
│   ├── adapters/
│   │   ├── CategoryAdapter.kt
│   │   ├── InventoryAdapter.kt
│   │   ├── DistributionAdapter.kt
│   │   └── ReturnAdapter.kt
│   ├── fragments/
│   │   ├── DashboardFragment.kt
│   │   ├── CategoriesFragment.kt
│   │   ├── InventoryFragment.kt
│   │   ├── DistributionFragment.kt
│   │   └── ReturnsFragment.kt
│   └── viewmodels/
│       ├── CategoryViewModel.kt
│       ├── InventoryViewModel.kt
│       ├── DistributionViewModel.kt
│       └── ReturnViewModel.kt
└── BloodDonationApp.kt
```

## كيفية البدء

### 1. إعداد Firebase
- اذهب إلى [Firebase Console](https://console.firebase.google.com)
- أنشئ مشروعاً جديداً
- حمّل ملف `google-services.json` 
- ضعه في `app/` directory

### 2. تثبيت المتطلبات
```bash
./gradlew build
```

### 3. تشغيل التطبيق
```bash
./gradlew installDebug
```

## الواجهات الرئيسية

### لوحة التحكم (Dashboard)
- عرض الأصناف القريبة من الانتهاء
- إحصائيات سريعة

### واجهة الأصناف (Categories)
- قائمة الأصناف مع خيارات التعديل والحذف
- زر لإضافة صنف جديد

### واجهة المخزون (Inventory)
- البحث عن المخزون
- عرض تفاصيل كل صنف في المخزون
- زر لإضافة مخزون جديد

### واجهة الصرف (Distribution)
- عرض سجل الصرف
- البحث عن عمليات الصرف
- زر لتسجيل صرف جديد

### واجهة المرتجعات (Returns)
- عرض قائمة المرتجعات
- البحث عن المرتجعات
- زر لإضافة مرتجع جديد

## الألوان والتصميم
- الأحمر الأساسي: #C41E3A (لون مركز نقل الدم)
- الأحمر الداكن: #8B1428
- البرتقالي: #FF5733
- أبيض: #FFFFFF
- رمادي فاتح: #F5F5F5

## خطوات التطوير القادمة

### المرحلة الثانية
- [ ] تطوير واجهات الإضافة المتقدمة
- [ ] تحسين نظام الإشعارات
- [ ] إضافة التقارير والإحصائيات
- [ ] تصدير البيانات (PDF, Excel)
- [ ] نظام المستخدمين والأدوار

### المرحلة الثالثة
- [ ] مزامنة كاملة مع Firebase
- [ ] نسخ احتياطي تلقائي
- [ ] وضع عدم الاتصال بالإنترنت
- [ ] نسخة ويب
- [ ] تطبيق للويب

## ملاحظات مهمة

1. **Firebase Integration**
   - استبدل `google-services.json` بملفك الخاص من Firebase Console
   - تفعّل المصادقة عبر Google في Firebase
   - أنشئ Firestore Database

2. **Notifications**
   - التنبيهات تعمل تلقائياً عند اقتراب انتهاء الصلاحية
   - يمكن تخصيص فترة التنبيه من 30 يوم

3. **Database**
   - قاعدة البيانات المحلية (Room) تعمل بدون إنترنت
   - يتم مزامنة البيانات مع Firebase تلقائياً

4. **RTL Support**
   - التطبيق يدعم اللغة العربية بشكل كامل
   - تم تفعيل RTL في جميع الواجهات

## الترخيص
هذا المشروع مخصص للمركز الإقليمي لنقل الدم بالإسماعيلية.

## الدعم والمساعدة
للحصول على الدعم أو الإبلاغ عن مشاكل، يرجى التواصل مع فريق التطوير.

---

**آخر تحديث:** ديسمبر 2025
**الإصدار:** 1.0.0
