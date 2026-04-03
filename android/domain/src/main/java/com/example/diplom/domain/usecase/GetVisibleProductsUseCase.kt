package com.example.diplom.domain.usecase

import com.example.diplom.domain.model.Product

enum class SortOption(val title: String) {
    POPULAR("Сначала популярные"),
    PRICE_ASC("Сначала недорогие"),
    PRICE_DESC("Сначала дорогие"),
    NEW("По новинкам"),
    DISCOUNT("По скидке (%)"),
    RATING("Сначала с лучшей оценкой")
}

class GetVisibleProductsUseCase {

    fun execute(
        products: List<Product>,
        query: String,
        sortOption: SortOption
    ): List<Product> {
        val searched = filterByQuery(products, query)
        return sort(searched, sortOption)
    }

    private fun filterByQuery(products: List<Product>, query: String): List<Product> {
        val q = query.trim()
        if (q.isEmpty()) return products

        return products.filter { product ->
            SearchNormalizer.matches(product.title, q) ||
                    SearchNormalizer.matches(product.description, q)
        }
    }

    private fun sort(products: List<Product>, option: SortOption): List<Product> {
        return when (option) {
            SortOption.POPULAR -> products.sortedByDescending { it.reviewsCount }
            SortOption.PRICE_ASC -> products.sortedBy { it.priceRub }
            SortOption.PRICE_DESC -> products.sortedByDescending { it.priceRub }
            SortOption.NEW -> products.sortedByDescending { it.createdAt }
            SortOption.DISCOUNT -> products.sortedByDescending { it.discountPercent }
            SortOption.RATING -> products.sortedByDescending { it.rating }
        }
    }
}