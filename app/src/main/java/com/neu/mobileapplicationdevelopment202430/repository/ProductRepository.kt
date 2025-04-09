package com.neu.mobileapplicationdevelopment202430.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.network.ProductApiService
import com.neu.mobileapplicationdevelopment202430.paging.ProductPagingSource
import com.neu.mobileapplicationdevelopment202430.storage.ProductStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader

class ProductRepository(
    private val apiService: ProductApiService,
    private val context: Context? = null
) {
    private val productStorage by lazy {
        context?.let { ProductStorage(it) }
    }

    fun getProductsFlow(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading())

        try {
            // Try fetch data from API
            val response = apiService.getProducts().execute()

            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()

                if (products.isNotEmpty()) {
                    val distinctProducts = products.distinctBy { it.id }
                    Log.d("ProductRepository", "API returned " +
                            "${distinctProducts.size} products")
                    emit(Result.Success(distinctProducts))
                } else {
                    emit(Result.Error("No products found"))
                }
            } else {
                // If API fails, read from JSON file
                val localProducts = readProductsFromJson()
                if (localProducts.isNotEmpty()) {
                    emit(Result.Success(localProducts))
                } else {
                    emit(Result.Error("Error loading products: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching products", e)

            // If any exception, read from JSON file
            val localProducts = readProductsFromJson()
            if (localProducts.isNotEmpty()) {
                emit(Result.Success(localProducts))
            } else {
                val errorMsg = when (e) {
                    is HttpException -> "Error loading products: ${e.message()}"
                    is IOException -> "Network error: ${e.message ?: "Unknown error"}"
                    else -> "Error: ${e.message ?: "Unknown error"}"
                }
                emit(Result.Error(errorMsg))
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun readProductsFromJson(): List<Product> {
        if (context == null) return emptyList()

        try {
            val inputStream = context.assets.open("products.json")
            val reader = InputStreamReader(inputStream)
            val typeToken = object : TypeToken<List<Product>>() {}.type
            val products = Gson().fromJson<List<Product>>(reader, typeToken)

            reader.close()

            return products.distinctBy { it.id }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error reading from JSON file", e)
            return emptyList()
        }
    }

    fun getPagedProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 3,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                ProductPagingSource(
                    apiService,
                    productStorage ?: ProductStorage(context!!)
                )
            }
        ).flow
    }

    sealed class Result<out T> {
        class Loading<out T> : Result<T>()
        data class Success<out T>(val data: T) : Result<T>()
        data class Error<out T>(val message: String) : Result<T>()
    }
}