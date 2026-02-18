package com.example.diplom.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diplom.domain.model.Product
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon





/**
 * Режим отображения карточки товара.
 *
 * Что делает:
 * - определяет, как карточка должна выглядеть (компактно или полно).
 *
 * С чем связан:
 * - HomeScreen использует COMPACT,
 * - ProductListScreen использует FULL.
 */
enum class ProductCardMode {
    COMPACT,
    FULL
}

/**
 * Единая карточка товара для всего приложения.
 *
 * Что делает:
 * - в режиме COMPACT: название + цена (для Home),
 * - в режиме FULL: скидка/рейтинг/цена + кнопка "В корзину" (для каталога).
 *
 * С чем связан:
 * - HomeScreen и ProductListScreen вызывают этот компонент,
 * - onOpenDetails ведёт на детали товара,
 * - onAddToCart используется только для FULL (можно передать null в COMPACT).
 */
@Composable
fun ProductCard(
    product: Product,
    mode: ProductCardMode,
    onOpenDetails: () -> Unit,
    onAddToCart: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    // Размер карточки задаём снаружи через modifier:
    // - для сетки: Modifier.fillMaxWidth()
    // - для горизонтального списка: Modifier.width(180.dp)
    Card(
        modifier = modifier
            .height(300.dp)
            .clickable { onOpenDetails() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F6F6)
        )
    ) {
        when (mode) {
            ProductCardMode.COMPACT -> CompactContent(product)
            ProductCardMode.FULL -> FullContent(product, onAddToCart)
        }
    }

}


/**
 * Контент карточки в компактном режиме (Home).
 *
 * Что делает:
 * - показывает название и цену.
 *
 * С чем связан:
 * - используется внутри ProductCard(mode = COMPACT).
 */
@Composable
private fun CompactContent(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color(0xFFF3F3F3))
    ) {
        if (product.imageUrl != null) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    Spacer(Modifier.padding(top = 10.dp))


    Column(modifier = Modifier.padding(12.dp)) {
        Text(product.title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.padding(top = 6.dp))
        Text("${product.priceRub} ₽", style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Контент карточки в полном режиме (Каталог).
 *
 * Что делает:
 * - показывает скидку, рейтинг, название, цену,
 * - показывает кнопку "В корзину" (если onAddToCart не null).
 *
 * С чем связан:
 * - используется внутри ProductCard(mode = FULL).
 */
@Composable
private fun FullContent(product: Product, onAddToCart: (() -> Unit)?) {
    Column {
        // Фото: фиксированная высота, фон, без обрезки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                .background(Color.White)
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }


        Spacer(Modifier.height(10.dp))

        // Контент: фиксированная структура
        Column(modifier = Modifier.padding(12.dp)) {

            // Цена / старая цена / скидка (в две строки — чтобы влезало)
            PriceRow(
                priceRub = product.priceRub,
                discountPercent = product.discountPercent
            )

            Spacer(Modifier.height(6.dp))

            // Название/описание: максимум 2 строки, чтобы высота карточки была одинаковой
            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // этот spacer "съест" лишнее пространство и прижмёт рейтинг вниз
            Spacer(modifier = Modifier.weight(1f))

            RatingRow(
                rating = product.rating,
                reviewsCount = product.reviewsCount
            )

        }
    }
}



/**
 * Бейдж скидки вида "-15%".
 *
 * Что делает:
 * - рисует небольшую плашку со скидкой.
 *
 * С чем связан:
 * - используется внутри FullContent.
 */
@Composable
private fun DiscountBadge(percent: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEFEFEF))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = "-$percent%", style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Чип рейтинга вида "★ 4.8".
 *
 * Что делает:
 * - показывает звёздочку и значение рейтинга.
 *
 * С чем связан:
 * - используется внутри FullContent.
 */
@Composable
private fun RatingChip(rating: Double) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEFEFEF))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = "★ $rating", style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
private fun PriceRow(priceRub: Int, discountPercent: Int) {
    val oldPrice: Int? =
        if (discountPercent > 0 && discountPercent < 100) {
            (priceRub / (1.0 - discountPercent / 100.0)).toInt()
        } else null

    // 1-я строка: текущая цена
    Text(
        text = "$priceRub ₽",
        style = MaterialTheme.typography.titleLarge
    )

    // 2-я строка: старая цена + скидка (компактно)
    if (oldPrice != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$oldPrice ₽",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
                textDecoration = TextDecoration.LineThrough
            )

            Spacer(Modifier.padding(start = 6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFE0E0))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "-$discountPercent%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB00020)
                )
            }
        }
    }
}


@Composable
private fun RatingRow(rating: Double, reviewsCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = "Рейтинг",
            modifier = Modifier.padding(end = 4.dp)
        )

        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.padding(start = 8.dp))

        Text(
            text = "$reviewsCount отзывов",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF777777)
        )
    }
}
