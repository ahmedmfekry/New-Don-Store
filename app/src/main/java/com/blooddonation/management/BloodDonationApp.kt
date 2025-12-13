package com.blooddonation.management

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BloodDonationApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // قناة الإشعارات العامة
            val generalChannel = NotificationChannel(
                "blood_donation_general",
                "إشعارات عامة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات عامة للتطبيق"
            }

            // قناة تنبيهات الصلاحية
            val expirationChannel = NotificationChannel(
                "blood_donation_expiration",
                "تنبيهات انتهاء الصلاحية",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات عند اقتراب انتهاء صلاحية الأصناف"
                enableVibration(true)
            }

            manager?.createNotificationChannels(
                listOf(generalChannel, expirationChannel)
            )
        }
    }
}
