package com.jayrave.falkon.mapper.exceptions

/**
 * Thrown whenever a converter is demanded from [TableConfiguration] for a type that
 * hasn't been registered
 */
class MissingConverterException : RuntimeException {
    constructor(message: String?) : super(message)
}