package com.example.diplom.feature.cart.viewmodel

import com.example.diplom.domain.model.CartItem
import com.example.diplom.domain.model.Product
import kotlin.collections.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CartState {

    private val _itemsFlow = MutableStateFlow<List<CartItem>>(emptyList())
    val itemsFlow: StateFlow<List<CartItem>> = _itemsFlow

    // Если тебе удобно как раньше: cartState.items
    val items: List<CartItem>
        get() = _itemsFlow.value

    fun add(product: Product) {
        _itemsFlow.update { list ->
            val idx = list.indexOfFirst { it.product.id == product.id }
            if (idx == -1) list + CartItem(product = product, qty = 1)
            else list.mapIndexed { i, item ->
                if (i == idx) item.copy(qty = item.qty + 1) else item
            }
        }
    }

    fun inc(productId: Int) {
        _itemsFlow.update { list ->
            list.map { item ->
                if (item.product.id == productId) item.copy(qty = item.qty + 1) else item
            }
        }
    }

    fun dec(productId: Int) {
        _itemsFlow.update { list ->
            val item = list.firstOrNull { it.product.id == productId } ?: return@update list
            if (item.qty <= 1) list.filterNot { it.product.id == productId }
            else list.map { i ->
                if (i.product.id == productId) i.copy(qty = i.qty - 1) else i
            }
        }
    }

    fun clear() {
        _itemsFlow.value = emptyList()
    }

    fun totalRub(): Int =
        _itemsFlow.value.sumOf { it.product.priceRub * it.qty }
}