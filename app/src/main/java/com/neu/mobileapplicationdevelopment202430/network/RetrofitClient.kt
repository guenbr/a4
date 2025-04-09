package com.neu.mobileapplicationdevelopment202430.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://mobileapplicationdevelopment.pythonanywhere.com/api/"
    private const val WORKER_API_TIMEOUT = 30L

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    private val workerClient = OkHttpClient.Builder()
        .connectTimeout(WORKER_API_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(WORKER_API_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WORKER_API_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val workerRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(workerClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ProductApiService = retrofit.create(ProductApiService::class.java)

    fun getWorkManagerApiService(): ProductApiService =
        workerRetrofit.create(ProductApiService::class.java)
}