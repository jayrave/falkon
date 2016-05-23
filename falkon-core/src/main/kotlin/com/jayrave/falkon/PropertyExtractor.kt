package com.jayrave.falkon

interface PropertyExtractor<T, C> {
    fun extract(t: T): C
}