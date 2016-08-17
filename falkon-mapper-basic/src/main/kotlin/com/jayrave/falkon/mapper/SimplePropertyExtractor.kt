package com.jayrave.falkon.mapper

import kotlin.reflect.KProperty1

/**
 * A [PropertyExtractor] that extracts the value using the property's getter
 */
class SimplePropertyExtractor<in T : Any, out C>(private val property: KProperty1<T, C>) :
        PropertyExtractor<T, C> {

    override fun extractFrom(t: T) = property.get(t)
}