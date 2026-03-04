package com.example.diplom.data.remote.mappers

import com.example.diplom.data.remote.dto.ProductDto
import com.example.diplom.domain.model.Product

fun ProductDto.toDomain(): Product = Product(
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