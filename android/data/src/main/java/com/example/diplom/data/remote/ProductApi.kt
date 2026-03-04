package com.example.diplom.data.remote

import com.example.diplom.data.remote.dto.ProductDto
import com.example.diplom.domain.model.Category
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ProductDto>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("api/v1/categories")
    suspend fun getCategories(): List<Category>

    @GET("api/v1/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Category
}