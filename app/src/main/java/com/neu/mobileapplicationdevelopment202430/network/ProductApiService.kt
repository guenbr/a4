package com.neu.mobileapplicationdevelopment202430.network

import com.neu.mobileapplicationdevelopment202430.model.ApiResponse
import com.neu.mobileapplicationdevelopment202430.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
    @GET("getProducts")
    fun getProducts(): Call<List<Product>>

    @GET("getProducts")
    suspend fun getProductsSuspend(): List<Product>

    @GET("getProductsV4")
    suspend fun getPagedProducts(@Query("page") page: Int): ApiResponse

    @GET("getProductsV4")
    suspend fun getWorkerProducts(@Query("appSrc") appSrc: String = "workmanager"): Response<List<Product>>
}