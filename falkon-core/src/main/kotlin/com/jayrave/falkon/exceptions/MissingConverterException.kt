package com.jayrave.falkon.exceptions

/**
 * Thrown whenever a converter is demanded from [TableConfiguration] for a type that hasn't been registered
 */
class MissingConverterException : RuntimeException {

    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
    super(message, cause, enableSuppression, writableStackTrace)
}