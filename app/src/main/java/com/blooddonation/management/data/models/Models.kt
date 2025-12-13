package com.blooddonation.management.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * نموذج الصنف
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val unit: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

/**
 * نموذج المخزون
 */
@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val expireDate: Long,
    val addedDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: String = "متاح",
    val createdBy: String = ""
) : Serializable

/**
 * نموذج الصرف من المخزون
 */
@Entity(tableName = "distributions")
data class Distribution(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val inventoryId: Int,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val expireDate: Long,
    val distributionDate: Long = System.currentTimeMillis(),
    val campaignName: String,
    val notes: String = "",
    val status: String = "تم الصرف",
    val distributedBy: String = ""
) : Serializable

/**
 * نموذج المرتجعات
 */
@Entity(tableName = "returns")
data class ReturnItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryName: String,
    val quantity: Int,
    val unit: String,
    val lotNumber: String,
    val expireDate: Long,
    val returnDate: Long = System.currentTimeMillis(),
    val reason: String,
    val notes: String = "",
    val returnedBy: String = ""
) : Serializable

/**
 * نموذج الإشعار
 */
data class NotificationAlert(
    val id: String = "",
    val itemId: Int,
    val categoryName: String,
    val lotNumber: String,
    val expireDate: Long,
    val daysUntilExpiration: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

/**
 * نموذج المستخدم
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "staff", // admin, staff
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)
