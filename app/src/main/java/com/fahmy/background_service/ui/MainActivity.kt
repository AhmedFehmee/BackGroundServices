package com.fahmy.background_service.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import androidx.lifecycle.Observer
import com.fahmy.background_service.R
import com.fahmy.background_service.utils.JobIntent.BackgroundService
import com.fahmy.background_service.utils.JobIntent.RestartService
import com.fahmy.background_service.utils.workManager.CountWorker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // job intent (start service when user destroyed the app
        // app will fire broadcast to restart the service)
        btn_run_service_job_intent.setOnClickListener {
            Log.i(
                "///isMyService",
                "${(!isMyServiceRunning())}"
            )
            if (!isMyServiceRunning()) {
                BackgroundService.startRefreshingToken(this)
            } else {
                Toast.makeText(this, "Service already running ", Toast.LENGTH_LONG).show()
            }
            isMyServiceRunning()
        }

        // work manager
        // It replaces JobScheduler as Google’s recommended way to enqueue background work
        btn_run_service_work_manager.setOnClickListener {
            startWorkManagerJob()
            isMyServiceRunning()
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
                    if (status.state.isFinished)
                        btn_run_service_work_manager.text =
                            getString(R.string.service_is_disconnected)
                    else
                        btn_run_service_work_manager.text = getString(R.string.service_is_connected)

                    val isFinished = status?.state?.isFinished
                    Log.d(TAG, "/////////// Job $workId; finished: $isFinished")
                })
        }

        Toast.makeText(this, "Job $workId enqueued", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        isMyServiceRunning()
        super.onResume()
    }

    private fun isMyServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("/// isMyService", service.service.className)

            if (service.service.className == BackgroundService()::class.java.name) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                btn_run_service_job_intent.text = getString(R.string.service_is_connected)
            } else {
                btn_run_service_job_intent.text = getString(R.string.service_is_disconnected)
            }

            if (service.service.className == "androidx.work.impl.background.systemjob.SystemJobService")
                btn_run_service_work_manager.text = getString(R.string.service_is_connected)
            else
                btn_run_service_work_manager.text = getString(R.string.service_is_disconnected)
        }
        Log.i("isMyServiceRunning?", false.toString() + "")

        return false
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
