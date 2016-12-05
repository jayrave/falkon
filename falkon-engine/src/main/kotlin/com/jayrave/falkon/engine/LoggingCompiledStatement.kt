package com.jayrave.falkon.engine

import java.util.*

/**
 * Holds on to the arguments & sends appropriate information to [logger] on [execute].
 * **DO NOT USE [Logger] in PRODUCTION**
 */
internal class LoggingCompiledStatement<R>(
        private val delegate: CompiledStatement<R>, private val logger: Logger) :
        CompiledStatement<R> by delegate {

    private var largestIndex = DEFAULT_LARGEST_INDEX
    private val arguments = HashMap<Int, Any?>()
    override val sql: String get() = delegate.sql

    override fun execute(): R {
        return try {
            val result = delegate.execute()
            log(successful = true)
            result
        } catch (e: Exception) {
            log(successful = false)
            throw e
        }
    }

    override fun bindShort(index: Int, value: Short): CompiledStatement<R> {
        delegate.bindShort(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindInt(index: Int, value: Int): CompiledStatement<R> {
        delegate.bindInt(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindLong(index: Int, value: Long): CompiledStatement<R> {
        delegate.bindLong(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindFloat(index: Int, value: Float): CompiledStatement<R> {
        delegate.bindFloat(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindDouble(index: Int, value: Double): CompiledStatement<R> {
        delegate.bindDouble(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindString(index: Int, value: String): CompiledStatement<R> {
        delegate.bindString(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindBlob(index: Int, value: ByteArray): CompiledStatement<R> {
        delegate.bindBlob(index, value)
        storeArgument(index, value)
        return this
    }

    override fun bindNull(index: Int, type: Type): CompiledStatement<R> {
        delegate.bindNull(index, type)
        storeArgument(index, null)
        return this
    }

    override fun clearBindings(): CompiledStatement<R> {
        delegate.clearBindings()
        clearArguments()
        return this
    }

    private fun storeArgument(index: Int, value: Any?) {
        if (index > largestIndex) {
            largestIndex = index
        }

        arguments.put(index, value)
    }

    private fun clearArguments() {
        largestIndex = DEFAULT_LARGEST_INDEX
        arguments.clear()
    }

    private fun log(successful: Boolean) {
        // Index is 1-based
        val orderedArguments = (1..largestIndex).map { arguments[it] }
        when (successful) {
            true -> logger.onSuccessfullyExecuted(sql, orderedArguments)
            else -> logger.onExecutionFailed(sql, orderedArguments)
        }
    }


    companion object {
        private const val DEFAULT_LARGEST_INDEX = 0
    }
}