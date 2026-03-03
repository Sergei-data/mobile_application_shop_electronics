package com.example.diplom.data.remote

import com.example.diplom.data.remote.dto.ProductDto
import retrofit2.http.GET


interface ProductApi {

    @GET("/api/v1/products")
    suspend fun getProducts(): List<ProductDto>

}