package com.neu.mobileapplicationdevelopment202430.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val expiryDate: String?,
    val price: String,
    val warranty: String?,
    val imageUrl: String,
    val bgColor: String = "",
    val page: Int = 0
)