package com.example.diplom.domain.usecase

import com.example.diplom.domain.model.Product


/**
 * Варианты сортировки каталога (как в DNS).
 *
 * Что делает:
 * - описывает возможные варианты сортировки.
 *
 * С чем связан:
 * - CatalogViewModel хранит выбранный SortOption,
 * - GetVisibleProductsUseCase применяет сортировку по этому значению.
 */
enum class SortOption(val title: String) {
    POPULAR("Сначала популярные"),
    PRICE_ASC("Сначала недорогие"),
    PRICE_DESC("Сначала дорогие"),
    NEW("По новинкам"),
    DISCOUNT("По скидке (%)"),
    RATING("Сначала с лучшей оценкой")
}

/**
 * UseCase "получить отображаемые товары" (поиск + сортировка).
 *
 * Что делает:
 * - принимает список товаров, строку поиска и сортировку,
 * - возвращает итоговый список для отображения в каталоге.
 *
 * С чем связан:
 * - вызывается из CatalogViewModel,
 * - заменяет логику поиска/сортировки внутри UI.
 */
class GetVisibleProductsUseCase {

    /**
     * Применяет поиск и сортировку.
     *
     * Как работает:
     * 1) фильтрует по title, если query не пустая
     * 2) сортирует по выбранному SortOption
     */
    fun execute(
        products: List<Product>,
        query: String,
        sortOption: SortOption
    ): List<Product> {
        val searched = filterByTitle(products, query)
        return sort(searched, sortOption)
    }

    /**
     * Фильтрует список товаров по подстроке в названии.
     */
    private fun filterByTitle(products: List<Product>, query: String): List<Product> {
        val q = query.trim()
        if (q.isEmpty()) return products
        val qLower = q.lowercase()
        return products.filter { it.title.lowercase().contains(qLower) }
    }

    /**
     * Сортирует товары согласно выбранному варианту.
     */
    private fun sort(products: List<Product>, option: SortOption): List<Product> {
        return when (option) {
            SortOption.POPULAR -> products.sortedByDescending { it.rating }
            SortOption.PRICE_ASC -> products.sortedBy { it.priceRub }
            SortOption.PRICE_DESC -> products.sortedByDescending { it.priceRub }
            SortOption.NEW -> products.sortedByDescending { it.createdAt }
            SortOption.DISCOUNT -> products.sortedByDescending { it.discountPercent }
            SortOption.RATING -> products.sortedByDescending { it.rating }
        }
    }
}
