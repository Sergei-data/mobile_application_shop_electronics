package com.example.diplom.domain.usecase

object SearchNormalizer {

    private val en = "`1234567890-=qwertyuiop[]\\asdfghjkl;'zxcvbnm,./"
    private val ru = "ё1234567890-=йцукенгшщзхъ\\фывапролджэячсмитьбю."

    private val enToRu: Map<Char, Char> =
        (en zip ru).toMap() + (en.uppercase() zip ru.uppercase()).toMap()

    private val ruToEn: Map<Char, Char> =
        (ru zip en).toMap() + (ru.uppercase() zip en.uppercase()).toMap()

    fun normalize(text: String): String {
        return text
            .trim()
            .lowercase()
            .replace('ё', 'е')
            .replace(Regex("\\s+"), " ")
    }

    fun swapLayout(text: String, direction: String): String {
        val mapping = when (direction) {
            "en->ru" -> enToRu
            "ru->en" -> ruToEn
            else -> return text
        }

        return text.map { ch -> mapping[ch] ?: ch }.joinToString("")
    }

    fun buildQueryVariants(query: String): List<String> {
        val raw = query.trim()
        if (raw.isEmpty()) return emptyList()

        val variants = listOf(
            raw,
            swapLayout(raw, "en->ru"),
            swapLayout(raw, "ru->en")
        )

        return variants
            .map { normalize(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    fun matches(source: String, query: String): Boolean {
        val src = normalize(source)
        val variants = buildQueryVariants(query)
        if (variants.isEmpty()) return true

        return variants.any { variant ->
            src.contains(variant)
        }
    }
}