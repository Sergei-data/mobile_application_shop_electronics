package com.example.diplom.feature.productdetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diplom.domain.model.Product
import com.example.diplom.feature.productdetails.ui.viewmodel.ProductDetailsViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(
    productId: Int,
    onBack: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onGoToCart: () -> Unit
) {
    val vm: ProductDetailsViewModel = viewModel()
    val state = vm.uiState.collectAsState().value

    LaunchedEffect(productId) {
        vm.load(productId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
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
            Button(onClick = onBack) { Text("Назад") }

            if (state.isLoading) {
                Text("Загрузка...", modifier = Modifier.padding(top = 16.dp))
                return@Scaffold
            }

            if (state.error != null) {
                Text("Ошибка: ${state.error}", modifier = Modifier.padding(top = 16.dp))
                return@Scaffold
            }

            val product = state.product
            if (product == null) {
                Text("Товар не найден", modifier = Modifier.padding(top = 16.dp))
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
                    onAddToCart(product)

                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Добавлено в корзину",
                            actionLabel = "Корзина"
                        )
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