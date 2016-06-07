package com.jayrave.falkon.engine

/**
 * A SQL statement that has been compiled by the [Engine] which could be used multiple times
 * with different arguments. The two main advantages of using a compiled statement
 *
 *      - Performance. Could be compiled once and used multiple times
 *      - Prevent SQL injection as parameters are bound and not inline
 */
interface CompiledStatement<R> {

    fun execute(): R

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindShort(index: Int, value: Short)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindInt(index: Int, value: Int)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindLong(index: Int, value: Long)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindFloat(index: Int, value: Float)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindDouble(index: Int, value: Double)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindString(index: Int, value: String)

    /**
     * @param index the 1-based index where [value] will be bound
     * @param value the parameter value
     */
    fun bindBlob(index: Int, value: ByteArray)

    /**
     * @param index the 1-based index where `null` will be bound
     */
    fun bindNull(index: Int)

    /**
     * Releases database resources immediately. Calling this method on a already
     * closed statement has no effect
     */
    fun close()

    /**
     * Clears all existing bindings
     */
    fun clearBindings()
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