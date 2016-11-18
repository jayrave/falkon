package com.jayrave.falkon.sqlBuilders.common

internal fun <T> Iterable<T>?.joinToStringIfHasItems(
        separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "",
        transform: ((T) -> CharSequence)): String? {

    return when (this) {
        null -> null
        else -> {
            var count = 0
            val string = joinToString(separator = separator, prefix = prefix, postfix = postfix) {
                count++
                transform.invoke(it)
            }

            when (count) {
                0 -> null
                else -> string
            }
        }
    }
}