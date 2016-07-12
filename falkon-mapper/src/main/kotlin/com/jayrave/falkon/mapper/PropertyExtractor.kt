package com.jayrave.falkon.mapper

interface PropertyExtractor<in T, out C> {
    fun extract(t: T): C
}