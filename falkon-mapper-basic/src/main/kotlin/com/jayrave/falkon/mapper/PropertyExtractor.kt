package com.jayrave.falkon.mapper

interface PropertyExtractor<in T, out C> {
    fun extractFrom(t: T): C
}