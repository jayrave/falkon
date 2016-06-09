package com.jayrave.falkon

import kotlin.reflect.KProperty1

/**
 * A [PropertyExtractor] that extracts the value using the property's getter
 */
class SimplePropertyExtractor<T : Any, C>(private val property: KProperty1<T, C>) :
        PropertyExtractor<T, C> {

    override fun extract(t: T) = property.get(t)
}