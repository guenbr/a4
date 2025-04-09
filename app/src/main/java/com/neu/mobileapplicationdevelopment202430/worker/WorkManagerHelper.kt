package com.neu.mobileapplicationdevelopment202430.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val TAG = "WorkManagerHelper"
    private const val PRODUCT_REFRESH_WORK_NAME = "product_refresh_work"

    fun schedulePeriodicProductRefresh(context: Context) {
        Log.d(TAG, "Scheduling periodic product refresh")

        WorkManager.getInstance(context).cancelUniqueWork(PRODUCT_REFRESH_WORK_NAME)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 1 hour
        val periodicWorkRequest = PeriodicWorkRequestBuilder<ProductRefreshWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PRODUCT_REFRESH_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

        Log.d(TAG, "Periodic product refresh scheduled")
    }
}