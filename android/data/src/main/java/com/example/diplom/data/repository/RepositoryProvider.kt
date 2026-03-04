package com.example.diplom.data.repository

import com.example.diplom.data.network.NetworkProvider

object RepositoryProvider {
    val productRepository = NetworkProductRepository(
        api = NetworkProvider.productApi
    )
}