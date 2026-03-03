package com.example.diplom.data.repository

import com.example.diplom.data.remote.RetrofitClient
import com.example.diplom.data.remote.mappers.toDomain
import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.repository.ProductRepository

class NetworkProductRepository : ProductRepository {

    override suspend fun getProducts(): List<Product> {
        return RetrofitClient.api.getProducts().map { it.toDomain() }
    }

    override suspend fun getCategories(): List<Category> {
        // позже сделаем отдельный endpoint
        return emptyList()
    }

    override suspend fun getProductById(id: Int): Product? {
        return getProducts().find { it.id == id }
    }

    override suspend fun getCategoryById(id: Int): Category? {
        return null
    }
}