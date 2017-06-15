package ru.midvikus.electro_l.service

import android.app.Notification
import android.app.Service
import android.app.WallpaperManager
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import ru.midvikus.electro_l.utils.setWallpaper
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.support.v7.app.NotificationCompat
import ru.midvikus.electro_l.R
import ru.midvikus.electro_l.activity.MainActivity
import ru.midvikus.electro_l.api.getLatestImage
import ru.midvikus.electro_l.utils.UPDATE_INTERVAL_NAME
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by midvik on 14.06.17.
 */
class ElectroWallpaperService : Service() {

    val handler = Handler()
    var UPDATE_INTERVAL : Long = 5000
    val DEFAULT_NOTIFICATION_ID = 42

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(UPDATE_INTERVAL_NAME)?.let {
            try {
                UPDATE_INTERVAL = it.toLong() * 60000
            } catch (e: NumberFormatException) {
            }
        }

        val wallpaperManager = WallpaperManager.getInstance(this)

        handler.post {
            updateWallpaper(wallpaperManager)
        }

        sendNotification(getString(R.string.app_name), getString(R.string.update_started))

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun sendNotification(title: String, text: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val contentIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.icon_small)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon_small))
                .setTicker(getString(R.string.app_name))
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())

        val notification: Notification
        if (android.os.Build.VERSION.SDK_INT <= 15) {
            notification = builder.getNotification() // API-15 and lower
        } else {
            notification = builder.build()
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification)
    }

    fun updateWallpaper(wallpaperManager: WallpaperManager) {
        if (!isNetworkAvailable()) {

            handler.postDelayed({
                updateWallpaper(wallpaperManager)
            }, UPDATE_INTERVAL)

            return
        }

        getLatestImage(this, {
            setWallpaper(wallpaperManager, it)

            val sdf = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

            sendNotification(getString(R.string.app_name),
                    getString(R.string.updated_at) + sdf.format(Date()))

            handler.postDelayed({
                updateWallpaper(wallpaperManager)
            }, UPDATE_INTERVAL)

        }, {
            println(it)

            handler.postDelayed({
                updateWallpaper(wallpaperManager)
            }, UPDATE_INTERVAL)
        })
    }

    fun isNetworkAvailable() : Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}