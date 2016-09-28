package com.jayrave.falkon.mapper.exceptions

/**
 * Thrown whenever a [NameFormatter] finds something wrong with the data it has to work with
 */
class NameFormatException : RuntimeException {
    constructor(message: String?) : super(message)
}