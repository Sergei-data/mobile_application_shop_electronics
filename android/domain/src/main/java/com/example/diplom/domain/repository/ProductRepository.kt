package com.example.diplom.domain.repository

import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product

interface ProductRepository {
    fun getProducts(): List<Product>
    fun getCategories(): List<Category>
    fun getProductById(id: Int): Product?
    fun getCategoryById(id: Int): Category?
}
