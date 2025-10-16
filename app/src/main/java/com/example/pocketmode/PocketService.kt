package com.example.pocketmode

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class PocketService : Service() {
    private var isLocked = false
    private var overlayView: android.view.View? = null
    private lateinit var windowManager: android.view.WindowManager

    companion object {
        const val CHANNEL_ID = "lock_channel"
        const val TAG = "PocketService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate呼ばれた")
        windowManager = getSystemService(WINDOW_SERVICE) as android.view.WindowManager
        checkOverlayPermission() // ←これ追加！！
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand呼ばれた action=${intent?.action}")
        when (intent?.action) {
            "TOGGLE_LOCK" -> {
                toggleLock()
            }
        }
        showNotification()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pocket Mode Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "通知からロック切り替え"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        Log.d(TAG, "showNotification呼ばれた")

        val toggleIntent = Intent(this, ToggleReceiver::class.java).apply {
            action = "TOGGLE_LOCK"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("ポケットモード")
            .setContentText(if (isLocked) "ロック待機中" else "ロック解除中")
            .addAction(android.R.drawable.ic_media_pause, if (isLocked) "解除" else "ロック", pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun toggleLock() {
        isLocked = !isLocked
        if (isLocked) {
            showOverlay()
        } else {
            removeOverlay()
        }
        android.util.Log.d("PocketService", "ロック状態切り替えた！ isLocked=$isLocked")
    }


    private fun showOverlay() {
        if (overlayView != null) return  // すでに表示中なら何もしない

        overlayView = android.view.View(this).apply {
            setBackgroundColor(android.graphics.Color.BLACK)  // ←ここ！
        }

        val params = android.view.WindowManager.LayoutParams(
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            android.view.WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                android.view.WindowManager.LayoutParams.TYPE_PHONE,
            android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)
    }

    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    private fun checkOverlayPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = android.net.Uri.parse("package:$packageName")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }




    override fun onBind(intent: Intent?): IBinder? = null
}
