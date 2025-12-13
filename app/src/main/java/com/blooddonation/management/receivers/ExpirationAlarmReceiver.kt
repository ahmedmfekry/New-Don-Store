package com.blooddonation.management.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blooddonation.management.R
import com.blooddonation.management.ui.MainActivity

class ExpirationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val itemName = intent.getStringExtra("itemName") ?: "صنف"
        val lotNumber = intent.getStringExtra("lotNumber") ?: ""

        sendExpirationNotification(context, itemName, lotNumber)
    }

    private fun sendExpirationNotification(context: Context, itemName: String, lotNumber: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "blood_donation_expiration")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("تنبيه انتهاء الصلاحية")
            .setContentText("الصنف: $itemName - LOT: $lotNumber قريب من انتهاء الصلاحية")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        NotificationManagerCompat.from(context).notify(
            itemName.hashCode(),
            notification
        )
    }
}
