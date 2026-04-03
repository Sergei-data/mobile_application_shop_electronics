package com.example.diplom.feature.productdetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

            ProductGallery(product = product)

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

@Composable
private fun ProductGallery(product: Product) {
    val gallery = remember(product.imageUrl) {
        buildProductGallery(product.imageUrl)
    }

    var selectedIndex by rememberSaveable(product.id) { mutableIntStateOf(0) }

    val selectedImage = gallery.getOrElse(selectedIndex) { product.imageUrl }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (selectedImage != null) {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            if (gallery.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(gallery) { index, imageUrl ->
                        ThumbnailItem(
                            imageUrl = imageUrl,
                            isSelected = index == selectedIndex,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    imageUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color(0xFFD8D8D8)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Миниатюра товара",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

private fun buildProductGallery(imageUrl: String?): List<String?> {
    if (imageUrl == null) return emptyList()

    return listOf(
        imageUrl,
        imageUrl,
        imageUrl,
        imageUrl
    )
}