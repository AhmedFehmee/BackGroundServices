package com.fahmy.background_service.utils.workManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit.SECONDS

class CountWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // perform long running operation 100 times
        for (i in 1..100) {
            SECONDS.sleep(1)
            Log.d("//////// CountWorker", "progress: $i")
        }
        //Result.success() -> task successfully finished
        //Result.failure() -> task failed and no need to retry
        //Result.retry() -> task failed; WorkManager will retry the execution based on the backoff policy
        return Result.success()
    }
}