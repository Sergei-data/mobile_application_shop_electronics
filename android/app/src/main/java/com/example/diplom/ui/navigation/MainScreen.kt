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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diplom.auth.AuthUiState
import com.example.diplom.feature.cart.viewmodel.CartState

@Composable
fun MainScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onManageProductsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    authUiState: AuthUiState = AuthUiState()
) {
    val navController = rememberNavController()
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
            cartState = cartState,
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onLogoutClick = onLogoutClick,
            onManageProductsClick = onManageProductsClick,
            onAdminClick = onAdminClick,
            isAuthorized = authUiState.isAuthorized,
            displayName = authUiState.displayName,
            email = authUiState.email,
            roleLabel = authUiState.roleLabel
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
    val cartItemsCount = cartState.items.size

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item.route == Routes.HOME) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    } else {
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