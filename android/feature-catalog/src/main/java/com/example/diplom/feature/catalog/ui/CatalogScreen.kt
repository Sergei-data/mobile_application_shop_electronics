package com.example.diplom.feature.catalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplom.domain.model.Category
import androidx.compose.foundation.lazy.grid.items as gridItems


/**
 * Экран "Каталог" (показывает категории плитками).
 *
 * Что делает:
 * - показывает список категорий в виде сетки (2 колонки),
 * - по клику на категорию вызывает onCategoryClick(categoryId).
 *
 * С чем связан:
 * - AppNavGraph передает сюда categories из репозитория,
 * - onCategoryClick в AppNavGraph делает navigate на product_list?categoryId=...
 */
@Composable
fun CatalogScreen(
    categories: List<Category>,
    onCategoryClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Каталог",
            style = MaterialTheme.typography.headlineSmall
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            gridItems(categories) { category ->
                CategoryTile(
                    title = category.title,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

/**
 * Плитка категории для сетки.
 *
 * Что делает:
 * - рисует карточку с названием категории,
 * - по клику вызывает onClick().
 *
 * С чем связан:
 * - используется внутри CatalogScreen (LazyVerticalGrid).
 */
@Composable
private fun CategoryTile(
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
