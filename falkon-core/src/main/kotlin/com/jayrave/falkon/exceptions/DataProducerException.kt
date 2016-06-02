package com.jayrave.falkon.exceptions

/**
 * Thrown whenever a [DataProducer] finds something wrong with the data it is asked to work with
 */
class DataProducerException : RuntimeException {

    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
    super(message, cause, enableSuppression, writableStackTrace)
}