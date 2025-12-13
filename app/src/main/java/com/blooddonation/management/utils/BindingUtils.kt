package com.blooddonation.management.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BindingUtils {
    
    @BindingAdapter("formatDate")
    @JvmStatic
    fun formatDate(textView: TextView, timestamp: Long) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
        textView.text = dateFormat.format(Date(timestamp))
    }

    @BindingAdapter("formatDateTime")
    @JvmStatic
    fun formatDateTime(textView: TextView, timestamp: Long) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("ar"))
        textView.text = dateFormat.format(Date(timestamp))
    }

    @BindingAdapter("statusColor")
    @JvmStatic
    fun setStatusColor(textView: TextView, status: String) {
        val color = when (status) {
            "متاح" -> android.graphics.Color.parseColor("#4CAF50")
            "تم الصرف" -> android.graphics.Color.parseColor("#2196F3")
            "منتهي الصلاحية" -> android.graphics.Color.parseColor("#F44336")
            else -> android.graphics.Color.parseColor("#757575")
        }
        textView.setTextColor(color)
    }
}
