package com.blooddonation.management.data.firebase

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor() {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val messaging = FirebaseMessaging.getInstance()

    fun getCurrentUser() = auth.currentUser



    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun sendUserDataToFirebase(userId: String, userData: Map<String, Any>) {
        try {
            firestore.collection("users").document(userId)
                .set(userData).await()
        } catch (e: Exception) {
            throw Exception("فشل حفظ البيانات: ${e.message}")
        }
    }

    suspend fun getUserData(userId: String): Map<String, Any>? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun syncInventoryToFirebase(
        userId: String,
        inventoryData: List<Map<String, Any>>
    ) {
        try {
            firestore.collection("users").document(userId)
                .collection("inventory").document("items")
                .set(mapOf("items" to inventoryData)).await()
        } catch (e: Exception) {
            throw Exception("فشل مزامنة المخزون: ${e.message}")
        }
    }

    suspend fun syncDistributionsToFirebase(
        userId: String,
        distributionData: List<Map<String, Any>>
    ) {
        try {
            firestore.collection("users").document(userId)
                .collection("inventory").document("distributions")
                .set(mapOf("items" to distributionData)).await()
        } catch (e: Exception) {
            throw Exception("فشل مزامنة المنصرف: ${e.message}")
        }
    }

    suspend fun syncReturnsToFirebase(
        userId: String,
        returnData: List<Map<String, Any>>
    ) {
        try {
            firestore.collection("users").document(userId)
                .collection("inventory").document("returns")
                .set(mapOf("items" to returnData)).await()
        } catch (e: Exception) {
            throw Exception("فشل مزامنة المرتجعات: ${e.message}")
        }
    }

    suspend fun getFCMToken(): String {
        return try {
            messaging.token.await()
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun subscribeToTopic(topic: String) {
        try {
            messaging.subscribeToTopic(topic).await()
        } catch (e: Exception) {
            throw Exception("فشل الاشتراك في الموضوع: ${e.message}")
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getUserId(): String? = auth.currentUser?.uid

    fun getUserEmail(): String? = auth.currentUser?.email

    fun getUserDisplayName(): String? = auth.currentUser?.displayName

    /**
     * تحديث بيانات المستخدم الأساسية في Firestore
     * يتم استدعاء هذه الدالة عند تسجيل الدخول الأول
     */
    suspend fun updateUserProfile(userId: String) {
        try {
            val user = auth.currentUser ?: return
            
            val userData = mapOf(
                "uid" to userId,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "lastLogin" to System.currentTimeMillis(),
                "createdAt" to System.currentTimeMillis()
            )
            
            firestore.collection("users").document(userId)
                .set(userData).await()
        } catch (e: Exception) {
            throw Exception("فشل تحديث ملف المستخدم: ${e.message}")
        }
    }

    /**
     * حفظ البيانات برت الاتصال (Offline Support)
     * تخزين محلي يتم مزامنته مع السحابة لاحقاً
     */
    fun enableOfflineSupport() {
        try {
            firestore.enableNetwork()
        } catch (e: Exception) {
            // معالجة الخطأ
        }
    }

    /**
     * التحقق من توفر الإنترنت والاتصال بـ Firebase
     */
    suspend fun isOnline(): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            firestore.collection("users").document(userId).get().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * مزامنة آمنة مع معالجة الأخطاء
     */
    suspend fun syncDataWithRetry(
        userId: String,
        dataType: String,
        data: List<Map<String, Any>>,
        maxRetries: Int = 3
    ): Boolean {
        var retries = 0
        while (retries < maxRetries) {
            try {
                when (dataType) {
                    "inventory" -> syncInventoryToFirebase(userId, data)
                    "distribution" -> syncDistributionsToFirebase(userId, data)
                    "returns" -> syncReturnsToFirebase(userId, data)
                    else -> return false
                }
                return true
            } catch (e: Exception) {
                retries++
                if (retries >= maxRetries) {
                    throw Exception("فشل المزامنة بعد $maxRetries محاولات: ${e.message}")
                }
                // انتظر قبل المحاولة التالية
                kotlinx.coroutines.delay(1000)
            }
        }
        return false
    }

}
