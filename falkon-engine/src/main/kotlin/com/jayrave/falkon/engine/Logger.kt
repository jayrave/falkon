package com.jayrave.falkon.engine

/**
 * **DO NOT USE [Logger] in PRODUCTION**
 */
interface Logger {

    /**
     * Called when the execution of the passed in SQL statement for the arguments
     * was successful
     */
    fun onSuccessfullyExecuted(sql: String, arguments: Iterable<Any?>)

    /**
     * Called when the execution of the passed in SQL statement for the arguments
     * was a failure (an exception was thrown)
     */
    fun onExecutionFailed(sql: String, arguments: Iterable<Any?>)
}