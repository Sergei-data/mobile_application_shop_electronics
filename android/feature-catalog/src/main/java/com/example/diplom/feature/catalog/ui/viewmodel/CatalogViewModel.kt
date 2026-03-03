package com.example.diplom.feature.catalog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.repository.RepositoryProvider
import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.usecase.GetVisibleProductsUseCase
import com.example.diplom.domain.usecase.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val categories: List<Category> = emptyList(),
    val categoryTitle: String? = null,
    val searchText: String = "",
    val sortOption: SortOption = SortOption.POPULAR,
    val visibleProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CatalogViewModel(
    private val getVisibleProductsUseCase: GetVisibleProductsUseCase = GetVisibleProductsUseCase()
) : ViewModel() {

    private val repository = RepositoryProvider.productRepository

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = false))
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private var sourceProducts: List<Product> = emptyList()

    init {
        viewModelScope.launch {
            try {
                val cats = repository.getCategories()
                _uiState.value = _uiState.value.copy(categories = cats)
            } catch (_: Throwable) {
            }
        }
    }

    fun load(categoryId: Int?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val categories = if (_uiState.value.categories.isEmpty()) {
                    repository.getCategories()
                } else {
                    _uiState.value.categories
                }

                val allProducts = repository.getProducts()
                val filtered = if (categoryId == null) allProducts else allProducts.filter { it.categoryId == categoryId }

                val title = if (categoryId == null) null else {
                    categories.firstOrNull { it.id == categoryId }?.title
                        ?: repository.getCategoryById(categoryId)?.title
                }

                sourceProducts = filtered

                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    categoryTitle = title,
                    isLoading = false,
                    error = null
                )

                recalcVisibleProducts()
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = (t.message ?: "Unknown error")
                )
            }
        }
    }

    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
        recalcVisibleProducts()
    }

    fun updateSortOption(option: SortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
        recalcVisibleProducts()
    }

    private fun recalcVisibleProducts() {
        val state = _uiState.value
        val visible = getVisibleProductsUseCase.execute(
            products = sourceProducts,
            query = state.searchText,
            sortOption = state.sortOption
        )
        _uiState.value = state.copy(visibleProducts = visible)
    }
}