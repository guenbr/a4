package com.neu.mobileapplicationdevelopment202430.model

data class ApiResponse(
    val bgColor: String,
    val page: Int,
    val products: List<Product>
)