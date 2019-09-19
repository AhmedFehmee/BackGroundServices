package com.fahmy.background_service.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import androidx.lifecycle.Observer
import com.fahmy.background_service.R
import com.fahmy.background_service.utils.jobIntent.BackgroundService
import com.fahmy.background_service.utils.jobIntent.RestartService
import com.fahmy.background_service.utils.workManager.CountWorker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // job intent (start service when user destroyed the app
        // app will fire broadcast to restart the service)
        btn_run_service_job_intent.setOnClickListener {
            if (!isMyServiceRunning(BackgroundService()::class.java.name)) {
                BackgroundService.startBackGroundServices(this)
            } else {
                BackgroundService.stopCurrent(this)
                Toast.makeText(this, "Service already running ", Toast.LENGTH_LONG).show()
            }
        }

        // work manager
        // It replaces JobScheduler as Google’s recommended way to enqueue background work
        btn_run_service_work_manager.setOnClickListener {
            startWorkManagerJob()
            isMyServiceRunning("androidx.work.impl.background.systemjob.SystemJobService")
        }
    }

    private fun startWorkManagerJob() {
        // setup WorkRequest
        val constraints = Constraints.Builder().build()

        val inputData = Data.Builder().build()

        val myWork = OneTimeWorkRequest.Builder(CountWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val workId = myWork.id

        WorkManager.getInstance().apply {
            // enqueue Work
            enqueue(myWork)
            // observe work status
            getWorkInfoByIdLiveData(workId)
                .observe(this@MainActivity, Observer { status ->
                    setStyleOfButton(btn_run_service_work_manager, status.state.isFinished)

                    val isFinished = status?.state?.isFinished
                    Log.d(TAG, "/////////// Job $workId; finished: $isFinished")
                })
        }

        Toast.makeText(this, "Job $workId enqueued", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        isMyServiceRunning(BackgroundService()::class.java.name)
        super.onResume()
    }

    private fun isMyServiceRunning(serviceClass: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("/// isMyService", "$serviceClass and service ${service.service.className} ${manager.getRunningServices(Integer.MAX_VALUE).size}")

            when (serviceClass) {
                service.service.className -> {
                    setStyleOfButton(btn_run_service_job_intent, true)
                    return true
                }
                service.service.className -> {
                    setStyleOfButton(btn_run_service_work_manager, true)
                    return true
                }
                else -> {
                    setStyleOfButton(btn_run_service_job_intent, false)
                    setStyleOfButton(btn_run_service_work_manager, false)
                }
            }
        }
        return false
    }

    private fun setStyleOfButton(view: Button, isConnected: Boolean) {
        if (isConnected) {
            view.text = getString(R.string.service_is_connected)
            view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        } else {
            view.text = getString(R.string.service_is_disconnected)
            view.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // restart breathing service
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartService"
        broadcastIntent.setClass(this, RestartService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
