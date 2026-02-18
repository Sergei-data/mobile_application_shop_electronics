package com.example.diplom.feature.catalog.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.usecase.GetVisibleProductsUseCase
import com.example.diplom.domain.usecase.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Состояние каталога (то, что нужно UI).
 *
 * Что делает:
 * - хранит строку поиска, выбранную сортировку и список товаров для отображения.
 *
 * С чем связан:
 * - ProductListScreen подписывается на это состояние и рисует UI.
 */
data class CatalogUiState(
    val searchText: String = "",
    val sortOption: SortOption = SortOption.POPULAR,
    val visibleProducts: List<Product> = emptyList()
)

/**
 * ViewModel каталога.
 *
 * Что делает:
 * - хранит UI-состояние каталога,
 * - получает входные товары (уже отфильтрованные по категории в NavGraph),
 * - применяет usecase (поиск + сортировка) и обновляет visibleProducts.
 *
 * С чем связан:
 * - ProductListScreen вызывает методы updateSearchText/updateSortOption/setSourceProducts.
 */
class CatalogViewModel(
    private val getVisibleProductsUseCase: GetVisibleProductsUseCase = GetVisibleProductsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    // “Исходные товары” (после фильтра категории из NavGraph)
    private var sourceProducts: List<Product> = emptyList()

    /**
     * Устанавливает исходный список товаров (при открытии экрана или смене категории).
     */
    fun setSourceProducts(products: List<Product>) {
        sourceProducts = products
        recalcVisibleProducts()
    }

    /**
     * Обновляет текст поиска и пересчитывает список.
     */
    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
        recalcVisibleProducts()
    }

    /**
     * Обновляет вариант сортировки и пересчитывает список.
     */
    fun updateSortOption(option: SortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
        recalcVisibleProducts()
    }

    /**
     * Пересчитывает список товаров для отображения (поиск + сортировка).
     */
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
