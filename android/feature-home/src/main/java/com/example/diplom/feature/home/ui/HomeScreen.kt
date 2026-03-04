package com.example.diplom.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diplom.core.ui.components.ProductCard
import com.example.diplom.core.ui.components.ProductCardMode
import com.example.diplom.domain.model.Product
import com.example.diplom.feature.home.ui.viewmodel.HomeViewModel
import com.example.diplom.domain.model.Category

@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit
) {
    val vm: HomeViewModel = viewModel()
    val state = vm.uiState.collectAsState().value
    val categories = state.categories

    if (state.isLoading) {
        Text("Загрузка...")
        return
    }

    if (state.error != null) {
        Text("Ошибка: ${state.error}")
        return
    }

    val products = state.products

    var searchText by remember { mutableStateOf("") }

    val quickActions = remember {
        listOf("Скидки и акции", "Статус заказа", "Shorts", "Магазины")
    }

    val filteredProducts = remember(searchText, products) {
        filterProductsByTitle(products, searchText)
    }

    val visibleHomeProducts = remember(filteredProducts) {
        filteredProducts.take(30)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Москва", style = MaterialTheme.typography.titleMedium)
                Text("Скидки 🔥", style = MaterialTheme.typography.titleMedium)
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue: String -> searchText = newValue },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Искать в магазине") },
                singleLine = true
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            if (categories.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories, key = { it.id }) { category: Category ->
                        CategoryChip(
                            title = category.title,
                            onClick = { onCategoryClick(category.id) }
                        )
                    }
                }
            } else {
                Text("Категории не найдены")
            }
        }


        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(quickActions) { title ->
                    QuickActionCard(title = title)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            AdBanner(
                title = "Скидки недели",
                subtitle = "До -20% на популярные товары",
                onClick = {}
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = buildRecommendationsTitle(searchText),
                style = MaterialTheme.typography.titleLarge
            )
        }

        gridItems(visibleHomeProducts) { p ->
            ProductCard(
                product = p,
                mode = ProductCardMode.FULL,
                onOpenDetails = { onProductClick(p.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Вы посмотрели все товары",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun filterProductsByTitle(products: List<Product>, query: String): List<Product> {
    val q = query.trim()
    if (q.isEmpty()) return products
    val qLower = q.lowercase()
    return products.filter { it.title.lowercase().contains(qLower) }
}

private fun buildRecommendationsTitle(searchText: String): String {
    return if (searchText.trim().isEmpty()) "Вам может понравиться" else "Результаты поиска"
}

@Composable
private fun QuickActionCard(title: String) {
    Card(
        modifier = Modifier.width(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AdBanner(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {}
        }
    }
}


@Composable
private fun CategoryChip(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}