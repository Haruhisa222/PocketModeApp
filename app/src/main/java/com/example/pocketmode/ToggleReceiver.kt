package com.example.pocketmode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ToggleReceiver", "ボタン押された！")

        // PocketServiceに「ロック切り替えて！」とリクエストを送る
        val serviceIntent = Intent(context, PocketService::class.java).apply {
            action = "TOGGLE_LOCK"
        }
        context.startService(serviceIntent)
    }
}
