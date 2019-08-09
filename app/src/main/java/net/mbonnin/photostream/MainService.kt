package net.mbonnin.photostream

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

import androidx.core.app.NotificationCompat


class MainService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = getString(R.string.app_name)
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentText(getString(R.string.app_running))
            .addAction(R.drawable.ic_crop_original_black_24dp, getString(R.string.view_stream), pendingIntent)
            .setSmallIcon(R.drawable.ic_terrain_black_24dp)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    companion object {
        private val NOTIFICATION_CHANNEL_ID = "main"
        private val NOTIFICATION_ID = 42

        fun stop(context: Context) {
            val serviceIntent = Intent()
            serviceIntent.setClass(context, MainService::class.java)
            context.stopService(serviceIntent)
        }

        fun start(context: Context) {
            val serviceIntent = Intent()
            serviceIntent.setClass(context, MainService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}

