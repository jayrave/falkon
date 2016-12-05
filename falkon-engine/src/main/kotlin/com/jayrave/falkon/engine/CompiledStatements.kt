package com.jayrave.falkon.engine

import java.io.Closeable

/**
 * A SQL statement that has been compiled by the [Engine] which could be used multiple times
 * with different arguments. The two main advantages of using a compiled statement
 *
 *      - Performance. Could be compiled once and used multiple times
 *      - Prevent SQL injection as parameters are bound and not inline
 */
interface CompiledStatement<R> : Closeable {

    /**
     * The SQL string this [CompiledStatement] represents
     */
    val sql: String

    /**
     * Whether this compiled statement is closed or not
     */
    val isClosed: Boolean

    /**
     * Execute this [CompiledStatement] for the currently bound arguments and
     * return the appropriate result
     */
    fun execute(): R

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindShort(index: Int, value: Short): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindInt(index: Int, value: Int): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindLong(index: Int, value: Long): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindFloat(index: Int, value: Float): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindDouble(index: Int, value: Double): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindString(index: Int, value: String): CompiledStatement<R>

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindBlob(index: Int, value: ByteArray): CompiledStatement<R>

    /**
     * @param index the 1-based index where `null` will be bound
     * @param type the type of column this index represents
     */
    fun bindNull(index: Int, type: Type): CompiledStatement<R>

    /**
     * Releases database resources immediately. Calling this method on a already
     * closed statement has no effect
     */
    override fun close()

    /**
     * Clears all existing bindings
     */
    fun clearBindings(): CompiledStatement<R>
}