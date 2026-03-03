package com.example.diplom.ui.navigation


object Routes {
    const val HOME = "home"

    // Новый экран: каталог-как-категории
    const val CATALOG = "catalog"

    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAILS = "product_details"
    const val CART = "cart"
    const val PROFILE = "profile"

    fun detailsRoute(productId: Int) = "$PRODUCT_DETAILS/$productId"

    fun productListRoute(categoryId: Int?): String {
        return if (categoryId == null) PRODUCT_LIST else "$PRODUCT_LIST?categoryId=$categoryId"
    }
}
