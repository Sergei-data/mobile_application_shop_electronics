package com.example.diplom.data.remote.dto

import com.example.diplom.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price_rub") val priceRub: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("discount_percent") val discountPercent: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("image_url") val imageUrl: String?
) {
    fun toDomain(): Product = Product(
        id = id,
        categoryId = categoryId,
        title = title,
        description = description,
        priceRub = priceRub,
        rating = rating,
        discountPercent = discountPercent,
        createdAt = createdAt,
        imageUrl = imageUrl
    )
}