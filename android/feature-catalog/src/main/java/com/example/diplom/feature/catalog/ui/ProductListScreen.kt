package com.example.diplom.feature.catalog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.diplom.core.ui.components.ProductCard
import com.example.diplom.core.ui.components.ProductCardMode
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.usecase.SortOption
import com.example.diplom.feature.catalog.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    categoryId: Int?,
    onProductClick: (Int) -> Unit,
    onAddToCart: (Product) -> Unit,
    onCartClick: () -> Unit
) {
    val vm: CatalogViewModel = viewModel()
    val state = vm.uiState.collectAsState().value

    LaunchedEffect(categoryId) {
        vm.load(categoryId)
    }

    val title = if (state.categoryTitle == null) "Каталог" else "Каталог: ${state.categoryTitle}"

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val isSortSheetVisible = remember { mutableStateOf(false) }

    if (isSortSheetVisible.value) {
        SortBottomSheet(
            current = state.sortOption,
            onSelect = { selected ->
                vm.updateSortOption(selected)
                scope.launch {
                    sheetState.hide()
                    isSortSheetVisible.value = false
                }
            },
            onDismiss = {
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
        Text(text = title, style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.padding(top = 12.dp))

        if (state.isLoading) {
            Text("Загрузка...")
            return
        }

        if (state.error != null) {
            Text("Ошибка: ${state.error}")
            return
        }

        OutlinedTextField(
            value = state.searchText,
            onValueChange = { vm.updateSearchText(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск в каталоге") },
            singleLine = true
        )

        Spacer(Modifier.padding(top = 12.dp))

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

        SortOptionRow(SortOption.POPULAR.title, current == SortOption.POPULAR) { onSelect(SortOption.POPULAR) }
        SortOptionRow(SortOption.PRICE_ASC.title, current == SortOption.PRICE_ASC) { onSelect(SortOption.PRICE_ASC) }
        SortOptionRow(SortOption.PRICE_DESC.title, current == SortOption.PRICE_DESC) { onSelect(SortOption.PRICE_DESC) }
        SortOptionRow(SortOption.NEW.title, current == SortOption.NEW) { onSelect(SortOption.NEW) }
        SortOptionRow(SortOption.DISCOUNT.title, current == SortOption.DISCOUNT) { onSelect(SortOption.DISCOUNT) }
        SortOptionRow(SortOption.RATING.title, current == SortOption.RATING) { onSelect(SortOption.RATING) }

        Spacer(Modifier.padding(bottom = 24.dp))
    }
}

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