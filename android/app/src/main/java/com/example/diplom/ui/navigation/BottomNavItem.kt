package com.example.diplom.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Модель элемента нижней навигации (BottomBar).
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
)

/**
 * Список вкладок нижнего меню.
 *
 * Важно:
 * - "Каталог" теперь ведёт на Routes.CATALOG (экран категорий),
 * - а список товаров открывается уже внутри по выбранной категории.
 */
val bottomNavItems = listOf(
    BottomNavItem(route = Routes.HOME, icon = Icons.Filled.Home, title = "Главная"),
    BottomNavItem(route = Routes.CATALOG, icon = Icons.Filled.List, title = "Каталог"),
    BottomNavItem(route = Routes.CART, icon = Icons.Filled.ShoppingCart, title = "Корзина"),
    BottomNavItem(route = Routes.PROFILE, icon = Icons.Filled.Person, title = "Профиль")
)
