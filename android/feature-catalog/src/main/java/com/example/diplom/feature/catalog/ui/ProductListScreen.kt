package com.example.diplom.feature.catalog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.usecase.SortOption
import com.example.diplom.feature.catalog.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.diplom.core.ui.components.ProductCard
import com.example.diplom.core.ui.components.ProductCardMode
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items


/**
 * Экран каталога (список товаров).
 *
 * Что делает:
 * - показывает заголовок (Каталог / Каталог: <категория>),
 * - показывает поле поиска (логика в CatalogViewModel),
 * - показывает кнопку "Сортировка" и bottom sheet со списком сортировок,
 * - показывает список товаров (state.visibleProducts),
 * - кнопка "Корзина" ведёт в корзину.
 *
 * С чем связан:
 * - products приходит отфильтрованным из AppNavGraph (по categoryId),
 * - CatalogViewModel применяет UseCase (поиск + сортировка),
 * - onProductClick ведёт в детали,
 * - onCartClick ведёт в корзину.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    products: List<Product>,
    categoryTitle: String?,
    onProductClick: (Int) -> Unit,
    onAddToCart: (Product) -> Unit,
    onCartClick: () -> Unit
) {

    // Заголовок зависит от выбранной категории
    val title = if (categoryTitle == null) "Каталог" else "Каталог: $categoryTitle"

    // ViewModel каталога (без DI, просто viewModel())
    val vm: CatalogViewModel = viewModel()

    // UI-state каталога (поиск, выбранная сортировка, итоговый список)
    val state = vm.uiState.collectAsState().value

    // Передаем исходные товары в VM (когда список реально меняется)
    val lastProducts = remember { mutableStateOf<List<Product>>(emptyList()) }
    if (lastProducts.value !== products) {
        lastProducts.value = products
        vm.setSourceProducts(products)
    }

    // Управление показом/скрытием bottom sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val isSortSheetVisible = remember { mutableStateOf(false) }

    // Bottom sheet сортировки (выезжает снизу)
    if (isSortSheetVisible.value) {
        SortBottomSheet(
            current = state.sortOption,
            onSelect = { selected ->
                // Сохраняем выбранную сортировку в VM
                vm.updateSortOption(selected)

                // Закрываем панель сортировки "вниз" с анимацией
                scope.launch {
                    sheetState.hide()
                    isSortSheetVisible.value = false
                }
            },
            onDismiss = {
                // Закрываем панель сортировки "вниз" с анимацией
                scope.launch {
                    sheetState.hide()
                    isSortSheetVisible.value = false
                }
            },
            sheetState = sheetState
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок каталога
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.padding(top = 12.dp))

        // Поиск в каталоге (логика поиска находится в VM + UseCase)
        OutlinedTextField(
            value = state.searchText,
            onValueChange = { newValue -> vm.updateSearchText(newValue) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск в каталоге") },
            singleLine = true
        )

        Spacer(Modifier.padding(top = 12.dp))

        // Кнопка сортировки (открывает bottom sheet)
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isSortSheetVisible.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Tune, contentDescription = "Сортировка")
                Spacer(Modifier.padding(start = 8.dp))
                Text("Сортировка: ${state.sortOption.title}")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Список товаров (результат работы VM)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.visibleProducts) { product ->
                ProductCard(
                    product = product,
                    mode = ProductCardMode.FULL,
                    onOpenDetails = { onProductClick(product.id) },
                    onAddToCart = { onAddToCart(product) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        // Кнопка перехода в корзину
        Button(
            onClick = onCartClick,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        ) {
            Text("Корзина")
        }
    }
}

/**
 * Bottom sheet выбора сортировки (как в DNS).
 *
 * Что делает:
 * - показывает список вариантов сортировки (радио-кнопки),
 * - при выборе вызывает onSelect(option),
 * - при закрытии вызывает onDismiss().
 *
 * С чем связан:
 * - current берется из CatalogViewModel,
 * - onSelect вызывает vm.updateSortOption(...)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    current: SortOption,
    onSelect: (SortOption) -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Text(
            text = "Сортировка",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SortOptionRow(
            title = SortOption.POPULAR.title,
            selected = current == SortOption.POPULAR,
            onClick = { onSelect(SortOption.POPULAR) }
        )
        SortOptionRow(
            title = SortOption.PRICE_ASC.title,
            selected = current == SortOption.PRICE_ASC,
            onClick = { onSelect(SortOption.PRICE_ASC) }
        )
        SortOptionRow(
            title = SortOption.PRICE_DESC.title,
            selected = current == SortOption.PRICE_DESC,
            onClick = { onSelect(SortOption.PRICE_DESC) }
        )
        SortOptionRow(
            title = SortOption.NEW.title,
            selected = current == SortOption.NEW,
            onClick = { onSelect(SortOption.NEW) }
        )
        SortOptionRow(
            title = SortOption.DISCOUNT.title,
            selected = current == SortOption.DISCOUNT,
            onClick = { onSelect(SortOption.DISCOUNT) }
        )
        SortOptionRow(
            title = SortOption.RATING.title,
            selected = current == SortOption.RATING,
            onClick = { onSelect(SortOption.RATING) }
        )

        Spacer(Modifier.padding(bottom = 24.dp))
    }
}

/**
 * Одна строка в списке сортировок (радио + текст).
 *
 * Что делает:
 * - рисует кликабельную строку,
 * - показывает RadioButton в выбранном состоянии.
 *
 * С чем связан:
 * - используется внутри SortBottomSheet.
 */
@Composable
private fun SortOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.padding(start = 6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}


/**
 * Карточка товара в каталоге (похожа на магазинные карточки, но без изображения).
 *
 * Что делает:
 * - показывает скидку бейджом (если discountPercent > 0),
 * - показывает название товара,
 * - показывает рейтинг,
 * - показывает цену,
 * - показывает кнопку "В корзину".
 *
 * С чем связан:
 * - вызывается из ProductListScreen внутри LazyColumn,
 * - onOpenDetails ведёт на детали товара,
 * - onAddToCart добавляет товар в корзину.
 */


/**
 * Бейдж скидки вида "-15%".
 *
 * Что делает:
 * - рисует маленькую плашку со скидкой.
 *
 * С чем связан:
 * - используется в ProductCard, показывается только если скидка > 0.
 */
@Composable
private fun DiscountBadge(percent: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFE0E0))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "-$percent%",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Чип рейтинга вида "★ 4.8".
 *
 * Что делает:
 * - показывает звёздочку и значение рейтинга.
 *
 * С чем связан:
 * - используется в ProductCard.
 */
@Composable
private fun RatingChip(rating: Double) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE8EEF8))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "★ $rating",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
