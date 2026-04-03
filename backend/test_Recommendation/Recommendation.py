from dataclasses import dataclass
from typing import List, Dict, Set


@dataclass
class Category:
    id: int
    title: str
    profit_weight: float


@dataclass
class Product:
    id: int
    category_id: int
    title: str
    price_rub: int
    description: str
    rating: float
    discount_percent: int
    created_at: int
    reviews_count: int


@dataclass
class ScoredProduct:
    product: Product
    popularity: float
    margin: float
    similarity: float
    score: float


class SearchNormalizer:
    EN = "`1234567890-=qwertyuiop[]\\asdfghjkl;'zxcvbnm,./"
    RU = "ё1234567890-=йцукенгшщзхъ\\фывапролджэячсмитьбю."

    EN_TO_RU = {**dict(zip(EN, RU)), **dict(zip(EN.upper(), RU.upper()))}
    RU_TO_EN = {**dict(zip(RU, EN)), **dict(zip(RU.upper(), EN.upper()))}

    @staticmethod
    def normalize(text: str) -> str:
        return " ".join(text.strip().lower().replace("ё", "е").split())

    @staticmethod
    def swap_layout(text: str, direction: str) -> str:
        if direction == "en->ru":
            mapping = SearchNormalizer.EN_TO_RU
        elif direction == "ru->en":
            mapping = SearchNormalizer.RU_TO_EN
        else:
            return text
        return "".join(mapping.get(ch, ch) for ch in text)

    @staticmethod
    def build_query_variants(query: str) -> List[str]:
        raw = query.strip()
        if not raw:
            return []

        variants = [
            raw,
            SearchNormalizer.swap_layout(raw, "en->ru"),
            SearchNormalizer.swap_layout(raw, "ru->en"),
        ]

        result = []
        seen = set()
        for item in variants:
            normalized = SearchNormalizer.normalize(item)
            if normalized and normalized not in seen:
                seen.add(normalized)
                result.append(normalized)
        return result

    @staticmethod
    def matches(source: str, query: str) -> bool:
        src = SearchNormalizer.normalize(source)
        variants = SearchNormalizer.build_query_variants(query)
        if not variants:
            return True
        return any(variant in src for variant in variants)


class RecommendationEngine:
    def __init__(self, alpha_popularity: float = 0.20, beta_margin: float = 0.45, gamma_similarity: float = 0.35):
        self.alpha_popularity = alpha_popularity
        self.beta_margin = beta_margin
        self.gamma_similarity = gamma_similarity

    def recommend(
        self,
        products: List[Product],
        categories: List[Category],
        query: str,
        limit: int = 10,
        exclude_ids: Set[int] | None = None,
    ) -> List[ScoredProduct]:
        exclude_ids = exclude_ids or set()
        candidates = [p for p in products if p.id not in exclude_ids]
        if not candidates:
            return []

        category_profit_weights = {c.id: c.profit_weight for c in categories}
        category_interest_weights = self._build_category_interest_weights(candidates, query)
        max_reviews = max((p.reviews_count for p in candidates), default=1)
        if max_reviews < 1:
            max_reviews = 1

        scored: List[ScoredProduct] = []
        for product in candidates:
            popularity = product.reviews_count / max_reviews
            margin = category_profit_weights.get(product.category_id, 0.50)
            similarity = 0.25 if not category_interest_weights else category_interest_weights.get(product.category_id, 0.0)
            score = (
                self.alpha_popularity * popularity
                + self.beta_margin * margin
                + self.gamma_similarity * similarity
            )
            scored.append(
                ScoredProduct(
                    product=product,
                    popularity=popularity,
                    margin=margin,
                    similarity=similarity,
                    score=score,
                )
            )

        scored.sort(
            key=lambda item: (
                item.score,
                item.product.rating,
                item.product.reviews_count,
                -item.product.id,
            ),
            reverse=True,
        )
        return scored[:limit]

    def _build_category_interest_weights(self, products: List[Product], query: str) -> Dict[int, float]:
        q = query.strip()
        if not q:
            return {}

        normalized_query = SearchNormalizer.normalize(q)
        matched = [
            p for p in products
            if SearchNormalizer.matches(p.title, q) or SearchNormalizer.matches(p.description, q)
        ]
        if not matched:
            return {}

        grouped: Dict[int, List[Product]] = {}
        for product in matched:
            grouped.setdefault(product.category_id, []).append(product)

        max_count = max(len(items) for items in grouped.values())
        result: Dict[int, float] = {}

        for category_id, items in grouped.items():
            count_weight = len(items) / max_count
            exact_prefix_count = sum(
                1 for item in items
                if SearchNormalizer.normalize(item.title).startswith(normalized_query)
            )
            exact_weight = exact_prefix_count / max(len(items), 1)
            result[category_id] = max(0.0, min(1.0, 0.8 * count_weight + 0.2 * exact_weight))

        return result


CATEGORIES = [
    Category(1, "Смартфоны", 0.80),
    Category(2, "Ноутбуки", 0.95),
    Category(3, "Наушники и аудио", 0.85),
    Category(4, "Телевизоры", 0.60),
    Category(5, "Аксессуары", 0.50),
]

PRODUCTS = [
    Product(1, 1, "Samsung Galaxy S24", 69990, "Флагманский смартфон с ярким AMOLED экраном", 4.8, 10, 20260115, 412),
    Product(2, 1, "iPhone 15", 82990, "Смартфон Apple с мощной камерой и iOS", 4.9, 5, 20250920, 560),
    Product(3, 1, "Xiaomi Redmi Note 13", 24990, "Доступный телефон с хорошей батареей", 4.6, 15, 20251010, 298),
    Product(4, 1, "POCO X6 Pro", 31990, "Игровой android смартфон с мощным процессором", 4.7, 12, 20251102, 341),
    Product(5, 2, "MacBook Air 13 M3", 119990, "Легкий ноутбук Apple для работы и учебы", 4.9, 3, 20260105, 221),
    Product(6, 2, "ASUS TUF Gaming A15", 94990, "Игровой ноутбук с RTX видеокартой", 4.7, 8, 20251125, 188),
    Product(7, 2, "Lenovo IdeaPad Slim 5", 67990, "Универсальный laptop для офиса и дома", 4.6, 11, 20251019, 173),
    Product(8, 2, "Huawei MateBook D16", 72990, "Ноутбук с большим экраном 16 дюймов", 4.5, 14, 20250928, 147),
    Product(9, 3, "Sony WH-1000XM5", 32990, "Премиальные наушники с шумоподавлением", 4.9, 7, 20251011, 265),
    Product(10, 3, "AirPods Pro 2", 22990, "Беспроводные earbuds для экосистемы Apple", 4.8, 6, 20250917, 390),
    Product(11, 3, "JBL Tune 770NC", 9990, "Bluetooth аудио наушники для города", 4.5, 20, 20251101, 205),
    Product(12, 3, "HyperX Cloud III", 12990, "Игровая гарнитура для ПК", 4.6, 18, 20251203, 156),
    Product(13, 4, "LG OLED C4 55", 139990, "OLED телевизор 55 дюймов для кино и игр", 4.9, 4, 20260107, 133),
    Product(14, 4, "Samsung QLED Q80D 65", 129990, "QLED TV с насыщенными цветами", 4.8, 9, 20251118, 118),
    Product(15, 4, "TCL P745 50", 45990, "Smart TV с Google TV и 4K", 4.4, 16, 20251022, 207),
    Product(16, 4, "Hisense U7N 55", 69990, "Телевизор mini LED для гостиной", 4.5, 13, 20251109, 96),
    Product(17, 5, "Logitech MX Master 3S", 11990, "Премиальная мышь для ноутбука и ПК", 4.8, 10, 20251004, 281),
    Product(18, 5, "Keychron K3", 9990, "Тонкая механическая клавиатура", 4.7, 12, 20250930, 164),
    Product(19, 5, "UGREEN USB-C Hub", 3990, "Компактный хаб для laptop и macbook", 4.6, 21, 20251201, 142),
    Product(20, 5, "Samsung T7 SSD 1TB", 10990, "Быстрый внешний SSD для хранения данных", 4.9, 9, 20251014, 238),
]


def print_catalog(products: List[Product], categories: List[Category]) -> None:
    category_map = {c.id: c.title for c in categories}
    print("\nКаталог товаров:\n")
    for product in products:
        category_title = category_map.get(product.category_id, "Без категории")
        print(
            f"- [{product.id}] {product.title} | {category_title} | {product.price_rub} ₽ | "
            f"rating={product.rating} | reviews={product.reviews_count}"
        )


def print_recommendations(items: List[ScoredProduct], categories: List[Category]) -> None:
    category_map = {c.id: c.title for c in categories}
    print("\nРекомендации:\n")
    if not items:
        print("Ничего не найдено.")
        return

    for index, item in enumerate(items, start=1):
        product = item.product
        category_title = category_map.get(product.category_id, "Без категории")
        print(
            f"{index}. {product.title} | {category_title} | {product.price_rub} ₽ | "
            f"score={item.score:.3f} | pop={item.popularity:.3f} | "
            f"margin={item.margin:.3f} | sim={item.similarity:.3f}"
        )


def run_demo_query(engine: RecommendationEngine, query: str) -> None:
    print("\n" + "=" * 90)
    print(f"Запрос: {query if query else '<пустой запрос>'}")
    recommendations = engine.recommend(
        products=PRODUCTS,
        categories=CATEGORIES,
        query=query,
        limit=8,
    )
    print_recommendations(recommendations, CATEGORIES)


if __name__ == "__main__":
    engine = RecommendationEngine()

    print("Демо алгоритма рекомендаций")
    print("Формула: score = 0.20 * popularity + 0.45 * margin + 0.35 * similarity")

    print_catalog(PRODUCTS, CATEGORIES)

    demo_queries = [
        "",
        "смартфон",
        "ноутбук",
        "наушники",
        "телевизор",
        "laptop",
        "ghjdjlyst yfieiybrb",
    ]

    for query in demo_queries:
        run_demo_query(engine, query)

    print("\nГотово")
