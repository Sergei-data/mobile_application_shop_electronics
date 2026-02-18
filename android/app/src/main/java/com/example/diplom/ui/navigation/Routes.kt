package com.example.diplom.ui.navigation

/**
 * Набор строковых маршрутов (routes) для навигации.
 *
 * Что делает:
 * - хранит имена экранов и помогает строить маршруты с параметрами.
 */
object Routes {
    const val HOME = "home"

    // Новый экран: каталог-как-категории
    const val CATALOG = "catalog"

    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAILS = "product_details"
    const val CART = "cart"
    const val PROFILE = "profile"

    /**
     * Формирует route для экрана деталей товара: "product_details/1"
     */
    fun detailsRoute(productId: Int) = "$PRODUCT_DETAILS/$productId"

    /**
     * Формирует route для каталога с фильтром по категории:
     * - без фильтра: "product_list"
     * - с фильтром: "product_list?categoryId=2"
     *
     * С чем связан:
     * - CatalogScreen вызывает это при клике на категорию,
     * - AppNavGraph читает categoryId и фильтрует товары.
     */
    fun productListRoute(categoryId: Int?): String {
        return if (categoryId == null) PRODUCT_LIST else "$PRODUCT_LIST?categoryId=$categoryId"
    }
}
