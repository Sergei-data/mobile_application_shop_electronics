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

        return listOf(
            raw,
            swapLayout(raw, "en->ru"),
            swapLayout(raw, "ru->en")
        )
            .map { normalize(it) }
            .filter { it.isNotBlank() }
            .distinct()
    }

    fun tokenize(text: String): List<String> {
        return normalize(text)
            .split(" ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun score(source: String, query: String): Double {
        val normalizedSource = normalize(source)
        val variants = buildQueryVariants(query)

        if (variants.isEmpty()) return 1.0
        if (normalizedSource.isBlank()) return 0.0

        return variants.maxOf { variant ->
            scoreVariantAgainstSource(normalizedSource, variant)
        }
    }

    fun matches(source: String, query: String): Boolean {
        return score(source, query) > 0.0
    }

    private fun scoreVariantAgainstSource(source: String, variant: String): Double {
        val rFull = fullMatchScore(source, variant)
        val rToken = tokenMatchScore(source, tokenize(variant))

        return 0.55 * rFull + 0.45 * rToken
    }

    private fun fullMatchScore(source: String, variant: String): Double {
        if (source.isBlank() || variant.isBlank()) return 0.0

        val fuzzy = similarity(source, variant)

        return when {
            source == variant -> 1.0
            source.startsWith(variant) -> 0.92
            source.contains(variant) -> 0.78
            fuzzy >= 0.72 -> fuzzy * 0.70
            else -> 0.0
        }
    }

    private fun tokenMatchScore(source: String, tokens: List<String>): Double {
        if (tokens.isEmpty()) return 0.0

        val words = tokenize(source)
        if (words.isEmpty()) return 0.0

        return tokens.maxOf { token ->
            bestWordScore(words, token)
        }
    }

    private fun bestWordScore(words: List<String>, token: String): Double {
        return words.maxOf { word ->
            val fuzzy = similarity(word, token)

            when {
                word == token -> 1.0
                word.startsWith(token) -> 0.90
                word.contains(token) -> 0.72
                token.length >= 3 && fuzzy >= 0.70 -> fuzzy
                else -> 0.0
            }
        }
    }

    private fun similarity(left: String, right: String): Double {
        if (left.isBlank() || right.isBlank()) return 0.0

        val maxLen = maxOf(left.length, right.length).coerceAtLeast(1)
        val distance = levenshtein(left, right)

        return 1.0 - distance.toDouble() / maxLen.toDouble()
    }

    private fun levenshtein(left: String, right: String): Int {
        if (left == right) return 0
        if (left.isEmpty()) return right.length
        if (right.isEmpty()) return left.length

        val dp = IntArray(right.length + 1) { it }

        for (i in 1..left.length) {
            var prevDiagonal = dp[0]
            dp[0] = i

            for (j in 1..right.length) {
                val temp = dp[j]
                val cost = if (left[i - 1] == right[j - 1]) 0 else 1

                dp[j] = minOf(
                    dp[j] + 1,
                    dp[j - 1] + 1,
                    prevDiagonal + cost
                )

                prevDiagonal = temp
            }
        }

        return dp[right.length]
    }
}