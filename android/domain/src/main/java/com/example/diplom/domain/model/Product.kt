package com.example.diplom.domain.model


data class Product(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val priceRub: Int,
    val description: String,
    val rating: Double,
    val discountPercent: Int,
    val createdAt: Long,
    val imageUrl: String? = null,
    val reviewsCount: Int = 0
)
