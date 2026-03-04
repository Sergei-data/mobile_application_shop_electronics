package com.example.diplom.data.repository

import com.example.diplom.data.remote.ProductApi
import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.repository.ProductRepository

class NetworkProductRepository(
    private val api: ProductApi
) : ProductRepository {

    override suspend fun getProducts(): List<Product> =
        api.getProducts().map { it.toDomain() }

    override suspend fun getProductById(id: Int): Product? =
        runCatching { api.getProductById(id).toDomain() }.getOrNull()

    override suspend fun getCategories(): List<Category> =
        api.getCategories()

    override suspend fun getCategoryById(id: Int): Category? =
        runCatching { api.getCategoryById(id) }.getOrNull()
}