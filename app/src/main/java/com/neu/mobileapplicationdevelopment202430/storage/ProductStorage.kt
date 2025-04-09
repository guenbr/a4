package com.neu.mobileapplicationdevelopment202430.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neu.mobileapplicationdevelopment202430.model.Product

class ProductStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("product_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProductsForPage(pageNumber: Int, products: List<Product>) {
        val json = gson.toJson(products)
        sharedPreferences.edit().putString("page_$pageNumber", json).apply()
    }

    fun getProductsForPage(pageNumber: Int): List<Product> {
        val json = sharedPreferences.getString("page_$pageNumber",
            null) ?: return emptyList()
        val type = object : TypeToken<List<Product>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun hasProductsForPage(pageNumber: Int): Boolean {
        return sharedPreferences.contains("page_$pageNumber")
    }
}