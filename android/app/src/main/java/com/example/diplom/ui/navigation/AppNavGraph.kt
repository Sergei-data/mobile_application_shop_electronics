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
import com.example.diplom.feature.cart.ui.CartScreen
import com.example.diplom.feature.home.ui.HomeScreen
import com.example.diplom.feature.catalog.ui.CatalogScreen
import com.example.diplom.feature.catalog.ui.ProductListScreen
import com.example.diplom.feature.productdetails.ui.ProductDetailsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPaddingModifier: Modifier,
    cartState: CartState
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = innerPaddingModifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onProductClick = { id ->
                    navController.navigate(Routes.detailsRoute(id))
                },
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.productListRoute(categoryId))
                }
            )
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.productListRoute(categoryId))
                }
            )
        }

        composable(
            route = "${Routes.PRODUCT_LIST}?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getInt("categoryId") ?: -1
            val categoryId: Int? = if (raw == -1) null else raw

            ProductListScreen(
                categoryId = categoryId,
                onProductClick = { productId ->
                    navController.navigate(Routes.detailsRoute(productId))
                },
                onAddToCart = { product -> cartState.add(product) },
                onCartClick = { navController.navigate(Routes.CART) }
            )
        }

        composable(
            route = "${Routes.PRODUCT_DETAILS}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable

            ProductDetailsScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onAddToCart = { p -> cartState.add(p) },
                onGoToCart = { navController.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                cartState = cartState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}