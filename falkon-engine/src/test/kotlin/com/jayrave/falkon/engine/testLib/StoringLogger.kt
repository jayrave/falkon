package com.jayrave.falkon.engine.testLib

import com.jayrave.falkon.engine.Logger

class StoringLogger : Logger {

    var onSuccessfulExecution: LogInfo? = null
    var onExecutionFailed: LogInfo? = null

    override fun onSuccessfullyExecuted(sql: String, arguments: Iterable<Any?>) {
        onSuccessfulExecution = LogInfo(sql, arguments)
    }

    override fun onExecutionFailed(sql: String, arguments: Iterable<Any?>) {
        onExecutionFailed = LogInfo(sql, arguments)
    }


    data class LogInfo(val sql: String, val arguments: Iterable<Any?>)
}