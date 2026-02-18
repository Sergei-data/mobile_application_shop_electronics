package com.example.diplom.feature.cart.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diplom.feature.cart.viewmodel.CartState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState



@Composable
fun CartScreen(
    cartState: CartState,
    onBack: () -> Unit
) {
    val items by cartState.itemsFlow.collectAsState()  // <-- ВОТ ЭТО КЛЮЧ

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) { Text("Назад") }

        Text(
            text = "Корзина",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        if (items.isEmpty()) {
            Text("Корзина пустая")
            return
        }

        items.forEach { item ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.product.title, style = MaterialTheme.typography.titleMedium)
                    Text("${item.product.priceRub} ₽ / шт", style = MaterialTheme.typography.bodyMedium)
                }

                Row {
                    Button(onClick = { cartState.dec(item.product.id) }) { Text("-") }
                    Text(
                        text = " ${item.qty} ",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                    )
                    Button(onClick = { cartState.inc(item.product.id) }) { Text("+") }
                }
            }
            HorizontalDivider()
        }

        val total = items.sumOf { it.product.priceRub * it.qty }
        Text(
            text = "Итого: $total ₽",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}