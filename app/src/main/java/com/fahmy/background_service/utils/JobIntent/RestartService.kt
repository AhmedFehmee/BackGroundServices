package com.fahmy.background_service.utils.JobIntent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class RestartService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Running", " Stop >> restart")
        BackgroundService.startRefreshingToken(
            context
        )
    }
}