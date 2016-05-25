package com.jayrave.falkon.exceptions

/**
 * Thrown whenever something is found to be wrong with the SQL syntax
 */
class SQLSyntaxErrorException : SQLException {

    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) :
    super(message, cause, enableSuppression, writableStackTrace)
}