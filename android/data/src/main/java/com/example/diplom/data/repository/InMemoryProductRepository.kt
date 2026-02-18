package com.example.diplom.data.repository

import com.example.diplom.domain.model.Category
import com.example.diplom.domain.model.Product
import com.example.diplom.domain.repository.ProductRepository

class InMemoryProductRepository : ProductRepository {

    private val categories = listOf(
        Category(id = 1, title = "Смартфоны"),
        Category(id = 2, title = "Ноутбуки"),
        Category(id = 3, title = "Наушники"),
        Category(id = 4, title = "TV")
    )

    private val products = listOf(
        Product(
            id = 1,
            categoryId = 1,
            title = "Смартфон Pixel 8",
            priceRub = 69990,
            description = "Компактный флагман, 128 ГБ.",
            rating = 4.8,
            discountPercent = 10,
            createdAt = 1739000000000,
            imageUrl = "https://gsmbutik.ru/image/catalog/import_files/f7/f798bc094c7411f0842700155db5ff28_1da702244c7511f0842700155db5ff28.jpg",
            reviewsCount = 666
        ),
        Product(
            id = 2,
            categoryId = 2,
            title = "Ноутбук ASUS VivoBook 15",
            priceRub = 55990,
            description = "15.6\", 16 ГБ RAM, 512 ГБ SSD.",
            rating = 4.4,
            discountPercent = 0,
            createdAt = 1737000000000,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQre2GP5uhKGybY02Jz6hI-szQJjmc46bB7IA&s",
            reviewsCount = 555
        ),
        Product(
            id = 3,
            categoryId = 3,
            title = "Наушники Sony WH-1000XM5",
            priceRub = 34990,
            description = "Шумоподавление, Bluetooth.",
            rating = 4.9,
            discountPercent = 5,
            createdAt = 1739500000000,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXB9ogXCWJeiHUCAfccGqSZsz7VUDh8Hw5GQ&s",
            reviewsCount = 444
        ),
        Product(
            id = 4,
            categoryId = 4,
            title = "Телевизор LG 55",
            priceRub = 62990,
            description = "4K, WebOS, 120 Гц.",
            rating = 4.6,
            discountPercent = 20,
            createdAt = 1735000000000,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTW0rtY8VpTsL_2137t0b4dV5KBJ0ggmHd-g&s",
            reviewsCount = 333
        ),
        Product(
            id = 5,
            categoryId = 1,
            title = "Микроволновка LG NeoChef",
            description = "Тестовый товар для проверки сетки и скролла.",
            priceRub = 12990,
            discountPercent = 10,
            rating = 4.6,
            createdAt = 20260210,
            imageUrl = "https://www.lg.com/ru/home-appliances/neo-chef/img/banner-7_02_mob.jpg?v=1",
            reviewsCount = 111
        ),
        Product(
            id = 6,
            categoryId = 3,
            title = "Смартфон Xiaomi Redmi Note",
            description = "Тестовый товар для проверки сетки и скролла.",
            priceRub = 18990,
            discountPercent = 0,
            rating = 4.7,
            createdAt = 20260212,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMCTsZlc2ZHe1mZekrXitopLhBzf8PsHjARw&s",
            reviewsCount = 3
        ),
        Product(
            id = 7,
            categoryId = 5,
            title = "Ноутбук ASUS VivoBook 15 pro",
            description = "Тестовый товар для проверки сетки и скролла.",
            priceRub = 54990,
            discountPercent = 15,
            rating = 4.5,
            createdAt = 20260128,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKQHbs-kDzXmThNjXY1ubakIT7RM6LLhXiRw&s",
            reviewsCount = 352
        ),
        Product(
            id = 8,
            categoryId = 4,
            title = "Телевизор Samsung 43 Crystal UHD",
            description = "Тестовый товар для проверки сетки и скролла.",
            priceRub = 37990,
            discountPercent = 5,
            rating = 4.4,
            createdAt = 20260201,
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcREZnkRQ_svl6fDkEpj-3spR2QDFQ-psOGGDQ&s",
            reviewsCount = 279
        )
    )

    override fun getProducts(): List<Product> = products
    override fun getCategories(): List<Category> = categories
    override fun getProductById(id: Int): Product? = products.find { it.id == id }
    override fun getCategoryById(id: Int): Category? = categories.find { it.id == id }
}
