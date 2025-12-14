package com.blooddonation.management.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    suspend fun signInWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
        } catch (e: Exception) {
            throw Exception("فشل تسجيل الدخول: ${e.message}")
        }
    }

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
}
