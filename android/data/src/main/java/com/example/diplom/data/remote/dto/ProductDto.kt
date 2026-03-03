package com.example.diplom.data.remote.dto

data class ProductDto(
    val id: Int,
    val category_id: Int,
    val title: String,
    val description: String,
    val price_rub: Int,
    val rating: Double,
    val discount_percent: Int,
    val created_at: Long,
    val image_url: String?
)