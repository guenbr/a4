package com.neu.mobileapplicationdevelopment202430

import android.app.Application
import android.util.Log
import com.neu.mobileapplicationdevelopment202430.network.RetrofitClient
import com.neu.mobileapplicationdevelopment202430.repository.ProductRepository
import com.neu.mobileapplicationdevelopment202430.worker.WorkManagerHelper

class AmazingProducts : Application() {
    companion object {
        private const val TAG = "AmazingProducts"
    }

    val repository by lazy {
        ProductRepository(
            apiService = RetrofitClient.apiService,
            context = applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application started")

        WorkManagerHelper.schedulePeriodicProductRefresh(applicationContext)
    }
}