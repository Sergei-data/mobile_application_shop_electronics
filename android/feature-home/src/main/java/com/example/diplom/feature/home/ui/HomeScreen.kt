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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplom.core.ui.components.ProductCard
import com.example.diplom.core.ui.components.ProductCardMode
import com.example.diplom.domain.model.Category
import com.example.diplom.feature.home.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import com.example.diplom.feature.home.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    onProductClick: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit
) {

    val vm: HomeViewModel = viewModel()
    val state = vm.uiState.collectAsState().value
    val categories = state.categories
    val promoBanners = listOf(
        "http://10.0.2.2:9000/product-images/promos/sale_week_1.png",
        "http://10.0.2.2:9000/product-images/promos/sale_week_1.png",
        "http://10.0.2.2:9000/product-images/promos/sale_week_1.png"
    )

    if (state.isLoading) {
        Text("Загрузка...")
        return
    }

    if (state.error != null) {
        Text("Ошибка: ${state.error}")
        return
    }

    val visibleHomeProducts = state.visibleProducts
    val quickActions = listOf("Скидки и акции", "Статус заказа", "Shorts", "Магазины")

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
                value = state.searchText,
                onValueChange = vm::onSearchTextChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Искать в магазине") },
                singleLine = true
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            if (state.searchText.isNotBlank() && state.searchSuggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        state.searchSuggestions.forEachIndexed { index, product ->
                            SuggestionRow(
                                title = product.title,
                                onClick = { vm.onSuggestionSelected(product.title) }
                            )

                            if (index != state.searchSuggestions.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
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
            PromoBannerCarousel(
                banners = promoBanners,
                onBannerClick = { index ->
                    // потом можно открыть экран акции
                }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = if (state.searchText.trim().isEmpty()) {
                    "Популярные товары"
                } else {
                    "Результаты поиска"
                },
                style = MaterialTheme.typography.titleLarge
            )
        }

        gridItems(visibleHomeProducts, key = { it.id }) { product ->
            ProductCard(
                product = product,
                mode = ProductCardMode.FULL,
                onOpenDetails = { onProductClick(product.id) },
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

@Composable
private fun SuggestionRow(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
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
private fun PromoBannerCarousel(
    banners: List<String>,
    onBannerClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(page) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = banners[page],
                    contentDescription = "Промо-баннер ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (banners.size > 1) {
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                banners.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
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