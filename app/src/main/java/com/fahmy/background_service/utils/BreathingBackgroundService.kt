package com.fahmy.background_service.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import java.util.*


class BreathingBackgroundService : JobIntentService() {

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    override fun onHandleWork(p0: Intent) {
        Log.i("Running", " Start")

        startTimer()
    }

    private fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("Running", "App is Running in ${isAppOnForeground(applicationContext)}")
            }
        }
        timer?.schedule(timerTask, 1000, 1000)
    }

    private fun isAppOnForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val JOB_ID = 1

        private fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, BreathingBackgroundService::class.java, JOB_ID, work)
        }

        fun startRefreshingToken(context: Context) {
            stopCurrent(context)
            enqueueWork(context, Intent())
        }

        private fun stopCurrent(context: Context) {
            context.stopService(Intent(context, BreathingBackgroundService::class.java))
        }
    }
}