package com.neu.mobileapplicationdevelopment202430.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.neu.mobileapplicationdevelopment202430.network.RetrofitClient
import com.neu.mobileapplicationdevelopment202430.storage.ProductStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ProductRefreshWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting background data refresh")
            // Get API
            val apiService = RetrofitClient.getWorkManagerApiService()
            // Get db
            val productStorage = ProductStorage(applicationContext)
            val response = apiService.getWorkerProducts()

            if (response.isSuccessful) {
                val products = response.body()
                if (products != null && products.isNotEmpty()) {
                    Log.d(TAG, "Fetched ${products.size} products from API")
                    val workManagerPageNumber = 999

                    val existingProducts = productStorage.getProductsForPage(workManagerPageNumber)
                    val existingIds = existingProducts.map { it.id }.toSet()

                    val newProducts = products.filter { !existingIds.contains(it.id) }
                        .map { it.copy(page = workManagerPageNumber) }

                    if (newProducts.isNotEmpty()) {
                        Log.d(TAG, "Saving ${newProducts.size} new products to storage")

                        val updatedProducts = existingProducts + newProducts

                        productStorage.saveProductsForPage(workManagerPageNumber, updatedProducts)

                        Log.d(TAG, "Background refresh completed successfully")
                    } else {
                        Log.d(TAG, "No new products to save")
                    }

                    return@withContext Result.success()
                } else {
                    Log.d(TAG, "API returned empty product list")
                    return@withContext Result.success()
                }
            } else {
                Log.e(TAG, "API call failed: ${response.message()}")
                return@withContext Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing products", e)
            return@withContext Result.retry()
        }
    }
}