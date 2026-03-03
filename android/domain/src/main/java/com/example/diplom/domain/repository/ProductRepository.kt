package com.example.diplom.domain.repository

import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getCategories(): List<Category>
    suspend fun getProductById(id: Int): Product?
    suspend fun getCategoryById(id: Int): Category?
}