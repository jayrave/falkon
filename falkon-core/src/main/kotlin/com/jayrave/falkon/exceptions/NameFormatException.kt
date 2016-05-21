package com.jayrave.falkon.exceptions

/**
 * Thrown whenever a [NameFormatter] finds something wrong with the data it has to work with
 */
class NameFormatException : RuntimeException {

    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
    super(message, cause, enableSuppression, writableStackTrace)
}