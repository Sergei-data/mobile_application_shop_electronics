package com.example.diplom.data.remote.mappers

import com.example.diplom.data.remote.dto.ProductDto
import com.example.diplom.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        categoryId = category_id,
        title = title,
        description = description,
        priceRub = price_rub,
        rating = rating,
        discountPercent = discount_percent,
        createdAt = created_at,
        imageUrl = image_url,
        reviewsCount = 0 // пока нет в API
    )
}