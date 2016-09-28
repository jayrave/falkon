package com.jayrave.falkon.mapper.exceptions

/**
 * Thrown whenever a [Converter] finds something wrong with the data it has to work with
 */
class ConversionException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}