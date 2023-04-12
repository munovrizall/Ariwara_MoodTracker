package com.artonov.ariwara.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.artonov.ariwara.MainActivity
import com.artonov.ariwara.R

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val wordList = listOf(
            "Kamu kuat dan mampu menghadapi segala tantangan",
            "Kamu memiliki potensi yang luar biasa",
            "Kamu sangat berharga",
            "Kamu layak bahagia",
            "Kamu itu unik",
            "Kamu layak untuk diterima dan dicintai",
        )
        val randomWord = wordList.random()

        val i = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                context,
                0, i,
                PendingIntent.FLAG_IMMUTABLE);
        }

        val builder = NotificationCompat.Builder(context!!, "ariwara")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Ariwara Daily Affirmation")
            .setContentText(randomWord)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())

    }
}

