package com.example.diplom.feature.productdetails.ui.viewmodel

import com.example.diplom.domain.model.Product

data class ProductDetailsUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)