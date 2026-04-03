package com.example.diplom.feature.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.repository.RepositoryProvider
import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.usecase.GetVisibleProductsUseCase
import com.example.diplom.domain.usecase.SortOption
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val visibleProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchText: String = "",
    val searchSuggestions: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val repository = RepositoryProvider.productRepository
    private val visibleProductsUseCase = GetVisibleProductsUseCase()

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onSearchTextChanged(newValue: String) {
        val current = _uiState.value
        val trimmed = newValue.trim()

        val rankedProducts = visibleProductsUseCase
            .execute(current.products, newValue, SortOption.POPULAR)

        val visibleProducts = if (trimmed.isEmpty()) {
            current.products
                .shuffled()
                .take(30)
        } else {
            rankedProducts.take(30)
        }

        val suggestions = if (trimmed.isEmpty()) {
            emptyList()
        } else {
            rankedProducts.take(5)
        }

        _uiState.value = current.copy(
            searchText = newValue,
            visibleProducts = visibleProducts,
            searchSuggestions = suggestions
        )
    }

    fun onSuggestionSelected(title: String) {
        onSearchTextChanged(title)
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val productsDeferred = async { repository.getProducts() }
                val categoriesDeferred = async { repository.getCategories() }

                val products = productsDeferred.await()
                val categories = categoriesDeferred.await()

                val visibleProducts = products
                    .shuffled()
                    .take(30)

                _uiState.value = HomeUiState(
                    products = products,
                    visibleProducts = visibleProducts,
                    categories = categories,
                    searchText = "",
                    searchSuggestions = emptyList(),
                    isLoading = false,
                    error = null
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }
}