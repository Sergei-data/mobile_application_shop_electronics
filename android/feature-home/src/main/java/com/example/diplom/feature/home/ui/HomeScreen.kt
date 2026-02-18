package com.example.diplom.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.model.Category
import com.example.diplom.core.ui.components.ProductCard
import com.example.diplom.core.ui.components.ProductCardMode
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch








/**
 * Главный экран "Home" (главная страница магазина).
 *
 * Что делает:
 * - рисует шапку, поиск, категории, быстрые действия, баннер,
 * - рисует товары сеткой 2 колонки (максимум 30),
 * - экран скроллится вниз и имеет конечный список.
 *
 * С чем связан:
 * - products приходит из NavGraph (из репозитория),
 * - onProductClick ведёт на экран деталей товара через NavGraph,
 * - onCategoryClick ведёт в каталог с фильтром.
 */
@Composable
fun HomeScreen(
    categories: List<Category>,
    products: List<Product>,
    onProductClick: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit
) {
    // Текст поиска (сохраняется при перерисовках экрана)
    var searchText by remember { mutableStateOf("") }

    // Быстрые действия (пока статические)
    val quickActions = remember {
        listOf("Скидки и акции", "Статус заказа", "Shorts", "Магазины")
    }

    // Товары, отфильтрованные по поиску (если поиск пустой — возвращаем всё)
    val filteredProducts = remember(searchText, products) {
        filterProductsByTitle(products, searchText)
    }

    // Ограничиваем вывод товаров на главной (например 30)
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
        // --- ШАПКА (на всю ширину) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Москва", style = MaterialTheme.typography.titleMedium)
                Text("Скидки 🔥", style = MaterialTheme.typography.titleMedium)
            }
        }

        // --- ПОИСК (на всю ширину) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue -> searchText = newValue },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Искать в магазине") },
                singleLine = true
            )
        }


        // --- БЫСТРЫЕ ДЕЙСТВИЯ (на всю ширину) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(quickActions) { title ->
                    QuickActionCard(title = title)
                }
            }
        }

        // --- РЕКЛАМНЫЙ БАННЕР (на всю ширину) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            AdBanner(
                title = "Скидки недели",
                subtitle = "До -20% на популярные товары",
                onClick = { /* потом добавим переход */ }
            )
        }


        // --- ЗАГОЛОВОК ТОВАРОВ (на всю ширину) ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = buildRecommendationsTitle(searchText),
                style = MaterialTheme.typography.titleLarge
            )
        }

        // --- ТОВАРЫ (2 колонки) ---
        gridItems(visibleHomeProducts) { p ->
            ProductCard(
                product = p,
                mode = ProductCardMode.FULL,
                onOpenDetails = { onProductClick(p.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }


        // --- КОНЕЦ СПИСКА (на всю ширину) ---
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


/**
 * Фильтрует товары по названию (title) по подстроке.
 *
 * Как работает:
 * - если query пустая или состоит из пробелов — возвращает исходный список,
 * - иначе приводит всё к нижнему регистру и ищет вхождение query в title.
 *
 * С чем связан:
 * - используется в HomeScreen для фильтрации блока "рекомендаций".
 */
private fun filterProductsByTitle(products: List<Product>, query: String): List<Product> {
    val q = query.trim()
    if (q.isEmpty()) return products

    val qLower = q.lowercase()
    return products.filter { p ->
        p.title.lowercase().contains(qLower)
    }
}

/**
 * Формирует заголовок блока рекомендаций в зависимости от поиска.
 *
 * Как работает:
 * - если поиск пустой → "Вам может понравиться"
 * - если есть поиск → "Результаты поиска"
 *
 * С чем связан:
 * - используется в HomeScreen для смены текста заголовка.
 */
private fun buildRecommendationsTitle(searchText: String): String {
    return if (searchText.trim().isEmpty()) "Вам может понравиться" else "Результаты поиска"
}

/**
 * Плашка категории.
 *
 * Что делает:
 * - показывает название категории,
 * - по клику вызывает onClick(), чтобы перейти в каталог с фильтром.
 */
@Composable
private fun CategoryChip(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



/**
 * Карточка "быстрого действия" на главной.
 *
 * Что делает:
 * - рисует небольшую карточку фиксированной ширины с названием действия.
 *
 * С чем связан:
 * - вызывается из HomeScreen в блоке быстрых действий (LazyRow).
 * - позже можно добавить onClick и навигацию.
 */
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
private fun PromoBanner(
    banners: List<Int>,
    onBannerClick: (Int) -> Unit
) {
    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(banners.size) {
        if (banners.size < 2) return@LaunchedEffect

        while (true) {
            kotlinx.coroutines.delay(4000)

            // если пользователь сейчас свайпает — подождём
            while (pagerState.isScrollInProgress) {
                kotlinx.coroutines.delay(200)
            }

            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }


    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Card(
                onClick = { onBannerClick(page) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Image(
                    painter = painterResource(banners[page]),
                    contentDescription = "Рекламный баннер",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                )
            }
        }
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
private fun AdBannerCarousel(
    banners: List<Int>,
    onBannerClick: (index: Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) { page ->
            AdBannerImage(
                drawableRes = banners[page],
                onClick = { onBannerClick(page) }
            )
        }

        Spacer(Modifier.height(10.dp))

        // Индикатор точками
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                )
            }
        }
    }
}

@Composable
private fun AdBannerImage(
    @DrawableRes drawableRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = "Рекламный баннер",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}