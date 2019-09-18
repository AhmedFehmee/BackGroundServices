package com.fahmy.background_service.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class RestartService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Running", " Stop >> restart")
        BreathingBackgroundService.startRefreshingToken(context)
    }
}