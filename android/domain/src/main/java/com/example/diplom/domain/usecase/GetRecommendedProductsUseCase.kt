package com.example.diplom.domain.usecase

import com.example.diplom.domain.model.Product

class GetRecommendedProductsUseCase {

    fun execute(
        products: List<Product>,
        categoryInterestWeights: Map<Int, Double>,
        categoryProfitWeights: Map<Int, Double>,
        limit: Int = 10,
        excludeIds: Set<Int> = emptySet()
    ): List<Product> {
        val candidates = products.filterNot { it.id in excludeIds }
        if (candidates.isEmpty()) return emptyList()

        val maxReviews = (candidates.maxOfOrNull { it.reviewsCount } ?: 1).coerceAtLeast(1)

        return candidates
            .sortedByDescending { product ->
                val popularity = product.reviewsCount.toDouble() / maxReviews.toDouble()
                val margin = categoryProfitWeights[product.categoryId] ?: 0.0

                val similarity = when {
                    categoryInterestWeights.isEmpty() -> 0.25
                    else -> categoryInterestWeights[product.categoryId] ?: 0.0
                }

                0.20 * popularity +
                        0.45 * margin +
                        0.35 * similarity
            }
            .take(limit)
    }
}