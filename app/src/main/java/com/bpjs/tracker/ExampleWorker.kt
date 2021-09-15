package com.bpjs.tracker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ExampleWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        Log.d("WORKER", "-----")
        return try {
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}