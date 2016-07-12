package com.jayrave.falkon.mapper

interface PropertyExtractor<T, C> {
    fun extract(t: T): C
}