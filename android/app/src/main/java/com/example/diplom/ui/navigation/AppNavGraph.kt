package com.example.diplom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diplom.feature.cart.viewmodel.CartState
import com.example.diplom.feature.profile.ui.ProfileScreen
import com.example.diplom.domain.repository.ProductRepository
import com.example.diplom.data.repository.InMemoryProductRepository
import com.example.diplom.feature.cart.ui.CartScreen
import com.example.diplom.feature.home.ui.HomeScreen
import com.example.diplom.feature.catalog.ui.CatalogScreen
import com.example.diplom.feature.catalog.ui.ProductListScreen
import com.example.diplom.feature.productdetails.ui.ProductDetailsScreen







/**
 * Граф навигации приложения.
 *
 * Что делает:
 * - описывает экраны и переходы,
 * - использует navController (созданный в MainScreen),
 * - использует cartState (созданный в MainScreen), чтобы корзина была общей для всех экранов.
 *
 * С чем связан:
 * - MainScreen передает сюда cartState и innerPaddingModifier.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPaddingModifier: Modifier,
    cartState: CartState
) {
    val repo: ProductRepository = InMemoryProductRepository()

    // Репозиторий данных (пока локальный хардкод).


    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = innerPaddingModifier
    ) {
        /**
         * Главная страница магазина.
         */
        composable(Routes.HOME) {
            HomeScreen(
                categories = repo.getCategories(),
                products = repo.getProducts(),
                onProductClick = { id: Int ->
                    navController.navigate(Routes.detailsRoute(id))
                },
                onCategoryClick = { categoryId: Int ->
                    navController.navigate(Routes.productListRoute(categoryId))
                }
            )
        }

        /**
         * Экран "Каталог" (категории плитками).
         * Тап по категории → открываем ProductListScreen с фильтром categoryId.
         */
        composable(Routes.CATALOG) {
            CatalogScreen(
                categories = repo.getCategories(),
                onCategoryClick = { categoryId: Int ->
                    navController.navigate(Routes.productListRoute(categoryId))
                }
            )
        }

        /**
         * Каталог (список товаров) с необязательным параметром categoryId.
         *
         * Что делает:
         * - если categoryId передан, фильтрует товары по категории,
         * - если нет, показывает все товары.
         */
        composable(
            route = "${Routes.PRODUCT_LIST}?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1

            val allProducts = repo.getProducts()
            val filteredProducts =
                if (categoryId == -1) allProducts else allProducts.filter { it.categoryId == categoryId }

            val categoryTitle =
                if (categoryId == -1) null else repo.getCategoryById(categoryId)?.title

            ProductListScreen(
                products = filteredProducts,
                categoryTitle = categoryTitle,
                onProductClick = { productId: Int ->
                    navController.navigate(Routes.detailsRoute(productId))
                },
                onAddToCart = { product ->
                    cartState.add(product)
                },
                onCartClick = {
                    navController.navigate(Routes.CART)
                }
            )

        }


        /**
         * Детали товара.
         */
        composable(
            route = "${Routes.PRODUCT_DETAILS}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            val product = repo.getProductById(productId)

            ProductDetailsScreen(
                product = product,
                onBack = { navController.popBackStack() },
                onAddToCart = { p -> cartState.add(p) },
                onGoToCart = { navController.navigate(Routes.CART) }
            )

        }

        /**
         * Корзина.
         */
        composable(Routes.CART) {
            CartScreen(
                cartState = cartState,
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Профиль.
         */
        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}
