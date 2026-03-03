package com.example.diplom.ui.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diplom.feature.cart.viewmodel.CartState
import androidx.navigation.NavGraph.Companion.findStartDestination





@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Состояние корзины живет здесь, чтобы BottomBar видел количество товаров.
    val cartState = remember { CartState() }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                navController = navController,
                cartState = cartState
            )
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            innerPaddingModifier = Modifier.padding(innerPadding),
            cartState = cartState
        )
    }
}


@Composable
fun AppBottomBar(
    navController: NavHostController,
    cartState: CartState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Количество позиций в корзине (не суммарное количество штук, а именно строк/товаров).
    // Если хочешь суммарное количество - сделаем позже.
    val cartItemsCount = cartState.items.size

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item.route == Routes.HOME) {
                        // Всегда возвращаемся именно на корневой Home, без восстановления предыдущего экрана Home-ветки
                        navController.navigate(Routes.HOME) {
                            // Удаляем всё, что было выше стартового экрана (это гарантирует возврат на Home)
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                                // Не сохраняем состояние Home-ветки, чтобы не возвращаться в детали товара
                                saveState = false
                            }
                            // Не создаём копии Home
                            launchSingleTop = true
                            // Не восстанавливаем прошлое состояние Home-ветки (иначе снова откроются детали)
                            restoreState = false
                        }
                    } else {
                        // Для остальных вкладок можно сохранять/восстанавливать состояние (как "нормальные" табы)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    // Если это вкладка "Корзина" — рисуем иконку с Badge.
                    if (item.route == Routes.CART && cartItemsCount > 0) {
                        CartIconWithBadge(
                            icon = item.icon,
                            badgeText = cartItemsCount.toString()
                        )
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) }
            )
        }
    }
}


@Composable
fun CartIconWithBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeText: String
) {
    BadgedBox(
        badge = {
            Badge {
                Text(badgeText)
            }
        }
    ) {
        Icon(icon, contentDescription = "Корзина")
    }
}
