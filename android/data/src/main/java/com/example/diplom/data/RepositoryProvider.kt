package com.example.diplom.data

import com.example.diplom.data.repository.NetworkProductRepository
import com.example.diplom.domain.repository.ProductRepository

object RepositoryProvider {
    val productRepository: ProductRepository = NetworkProductRepository()
}