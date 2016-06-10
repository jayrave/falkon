package com.jayrave.falkon.engine

/**
 * A SQL statement that has been compiled by the [Engine] which could be used multiple times
 * with different arguments. The two main advantages of using a compiled statement
 *
 *      - Performance. Could be compiled once and used multiple times
 *      - Prevent SQL injection as parameters are bound and not inline
 */
interface CompiledStatement<R> {

    /**
     * The SQL string this [CompiledStatement] represents
     */
    val sql: String

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
     */
    fun bindNull(index: Int): CompiledStatement<R>

    /**
     * Releases database resources immediately. Calling this method on a already
     * closed statement has no effect
     */
    fun close()

    /**
     * Clears all existing bindings
     */
    fun clearBindings(): CompiledStatement<R>
}


/**
 * INSERT specific [CompiledStatement]
 */
interface CompiledInsert : CompiledStatement<Int> {

    /**
     * @return number of rows inserted
     */
    override fun execute(): Int
}


/**
 * UPDATE specific [CompiledStatement]
 */
interface CompiledUpdate : CompiledStatement<Int> {

    /**
     * @return number of rows updated
     */
    override fun execute(): Int
}


/**
 * DELETE specific [CompiledStatement]
 */
interface CompiledDelete : CompiledStatement<Int> {

    /**
     * @return number of rows deleted
     */
    override fun execute(): Int
}


/**
 * SELECT specific [CompiledStatement]
 */
interface CompiledQuery : CompiledStatement<Source> {

    /**
     * @return source that contains the data produced by the query
     */
    override fun execute(): Source
}