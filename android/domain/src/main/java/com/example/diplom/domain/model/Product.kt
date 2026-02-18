package com.example.diplom.domain.model

/**
 * Модель товара.
 *
 * Что делает:
 * - хранит данные товара для отображения и сортировок.
 *
 * С чем связан:
 * - categoryId связывает товар с Category,
 * - rating/discountPercent/createdAt нужны для сортировки "как в DNS".
 */
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
