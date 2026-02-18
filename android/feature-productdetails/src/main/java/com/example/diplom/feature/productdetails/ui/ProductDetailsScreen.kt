package com.example.diplom.feature.productdetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplom.domain.model.Product
import kotlinx.coroutines.launch

/**
 * Экран деталей товара.
 *
 * Что делает:
 * - показывает информацию о товаре,
 * - дает добавить товар в корзину,
 * - показывает Snackbar после добавления,
 * - по кнопке в Snackbar может перейти в корзину.
 *
 * С чем связан:
 * - onAddToCart(product) меняет cartState (в NavGraph/MainScreen),
 * - onGoToCart() вызывает navController.navigate(Routes.CART) из NavGraph,
 * - onBack() возвращает назад.
 */
@Composable
fun ProductDetailsScreen(
    product: Product?,
    onBack: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onGoToCart: () -> Unit
) {
    // Хост для Snackbar (управляет показом сообщений)
    val snackbarHostState = remember { SnackbarHostState() }

    // CoroutineScope нужен, потому что showSnackbar — suspend функция
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Button(onClick = onBack) {
                Text("Назад")
            }

            if (product == null) {
                Text(
                    text = "Товар не найден",
                    modifier = Modifier.padding(top = 16.dp)
                )
                return@Scaffold
            }

            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "${product.priceRub} ₽",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )

            Button(
                onClick = {
                    // 1) Добавляем товар в корзину
                    onAddToCart(product)

                    // 2) Показываем Snackbar с действием "Корзина"
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Добавлено в корзину",
                            actionLabel = "Корзина"
                        )

                        // 3) Если нажали action — переходим в корзину
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            onGoToCart()
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("В корзину")
            }
        }
    }
}
