package com.example.dailydose.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dailydose.services.WaterReminderService

class WaterReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val waterReminderService = WaterReminderService(context)
        waterReminderService.showWaterReminderNotification()
    }
}


