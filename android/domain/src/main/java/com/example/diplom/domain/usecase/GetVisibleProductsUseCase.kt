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

private data class RankedProduct(
    val product: Product,
    val score: Double
)

class GetVisibleProductsUseCase {

    companion object {
        private const val MIN_SEARCH_SCORE = 0.35
        private const val DESCRIPTION_WEIGHT = 0.85
    }

    fun execute(
        products: List<Product>,
        query: String,
        sortOption: SortOption
    ): List<Product> {
        val q = query.trim()
        if (q.isEmpty()) {
            return sort(products, sortOption)
        }

        return searchAndSort(products, q, sortOption)
    }

    private fun searchAndSort(
        products: List<Product>,
        query: String,
        sortOption: SortOption
    ): List<Product> {
        val ranked = products
            .map { product ->
                val titleScore = SearchNormalizer.score(product.title, query)
                val descriptionScore = SearchNormalizer.score(product.description, query) * DESCRIPTION_WEIGHT

                RankedProduct(
                    product = product,
                    score = maxOf(titleScore, descriptionScore)
                )
            }
            .filter { it.score >= MIN_SEARCH_SCORE }

        return sortRanked(ranked, sortOption)
            .map { it.product }
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

    private fun sortRanked(
        products: List<RankedProduct>,
        option: SortOption
    ): List<RankedProduct> {
        return when (option) {
            SortOption.POPULAR -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenByDescending { it.product.reviewsCount }
                    .thenByDescending { it.product.rating }
                    .thenBy { it.product.title }
            )

            SortOption.PRICE_ASC -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenBy { it.product.priceRub }
                    .thenByDescending { it.product.reviewsCount }
            )

            SortOption.PRICE_DESC -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenByDescending { it.product.priceRub }
                    .thenByDescending { it.product.reviewsCount }
            )

            SortOption.NEW -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenByDescending { it.product.createdAt }
                    .thenByDescending { it.product.reviewsCount }
            )

            SortOption.DISCOUNT -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenByDescending { it.product.discountPercent }
                    .thenByDescending { it.product.reviewsCount }
            )

            SortOption.RATING -> products.sortedWith(
                compareByDescending<RankedProduct> { it.score }
                    .thenByDescending { it.product.rating }
                    .thenByDescending { it.product.reviewsCount }
            )
        }
    }
}