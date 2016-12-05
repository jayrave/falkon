package com.jayrave.falkon.mapper

/**
 * [converter] A converter between the SQL type and [C]
 */
class ReadOnlyColumnImpl<out C>(
        override val name: String, private val converter: Converter<C>) :
        ReadOnlyColumn<C> {

    override fun computePropertyFrom(dataProducer: DataProducer): C {
        return converter.from(dataProducer)
    }
}