package com.blooddonation.management.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blooddonation.management.R
import com.blooddonation.management.receivers.ExpirationAlarmReceiver
import java.util.Calendar

object NotificationUtils {

    fun scheduleExpirationAlarm(
        context: Context,
        itemName: String,
        lotNumber: String,
        expireDate: Long,
        daysUntilExpiration: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ExpirationAlarmReceiver::class.java).apply {
            putExtra("itemName", itemName)
            putExtra("lotNumber", lotNumber)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // جدولة التنبيه قبل 30 يوم من انتهاء الصلاحية
        val calendar = Calendar.getInstance().apply {
            timeInMillis = expireDate
            add(Calendar.DAY_OF_YEAR, -30)
        }

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelExpirationAlarm(context: Context, itemName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, ExpirationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    fun showQuickNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 999
    ) {
        val notification = NotificationCompat.Builder(context, "blood_donation_general")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun calculateDaysUntilExpiration(expireDate: Long): Int {
        val now = System.currentTimeMillis()
        val diffInMillis = expireDate - now
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun isExpirationWarningTime(daysUntilExpiration: Int): Boolean {
        return daysUntilExpiration in 0..30
    }
}
