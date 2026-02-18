package com.example.diplom.domain.model

/**
 * Модель категории товара.
 *
 * Что делает:
 * - хранит id и название категории.
 *
 * С чем связан:
 * - Product будет ссылаться на categoryId,
 * - репозиторий будет отдавать список категорий для главной/каталога.
 */
data class Category(
    val id: Int,
    val title: String
)
