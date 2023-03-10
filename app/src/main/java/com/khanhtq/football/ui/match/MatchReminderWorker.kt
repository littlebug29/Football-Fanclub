package com.khanhtq.football.ui.match

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.khanhtq.football.R

class MatchReminderWorker(
    appContext: Context,
    workParams: WorkerParameters
) : Worker(appContext, workParams) {

    override fun doWork(): Result {
        val data = inputData
        val title = data.getString(KEY_MATCH_TITLE)
        val description = data.getString(KEY_MATCH_DESC)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Football Match Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_game)
            .setContentTitle(title)
            .setContentText(description)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        notificationManager.notify(0, notification)

        return Result.success()
    }

    companion object {
        const val KEY_MATCH_TITLE = "MATCH_TITLE"
        const val KEY_MATCH_DESC = "MATCH_DESC"
        const val CHANNEL_ID = "Match_Reminder_Channel_Id"
    }
}