package com.jayrave.falkon.engine

/**
 * [CompiledStatement]s that throw on trying to bind, clear or close
 */
internal open class CompiledStatementForTest<R>(
        override val sql: String, private val returnValue: R) :
        CompiledStatement<R> {

    override fun execute(): R = returnValue
    override fun bindShort(index: Int, value: Short) = throw buildException()
    override fun bindInt(index: Int, value: Int) = throw buildException()
    override fun bindLong(index: Int, value: Long) = throw buildException()
    override fun bindFloat(index: Int, value: Float) = throw buildException()
    override fun bindDouble(index: Int, value: Double) = throw buildException()
    override fun bindString(index: Int, value: String) = throw buildException()
    override fun bindBlob(index: Int, value: ByteArray) = throw buildException()
    override fun bindNull(index: Int, type: Type) = throw buildException()
    override fun close() = throw buildException()
    override fun clearBindings() = throw buildException()

    private fun buildException(): Exception {
        return UnsupportedOperationException("No can do")
    }
}


internal class CompiledSqlForTest(sql: String) :
        CompiledStatementForTest<Unit>(sql, Unit)


internal class CompiledStatementForInsertForTest(sql: String, returnValue: Int) :
        CompiledStatementForTest<Int>(sql, returnValue)


internal class CompiledStatementForUpdateForTest(sql: String, returnValue: Int) :
        CompiledStatementForTest<Int>(sql, returnValue)


internal class CompiledStatementForDeleteForTest(sql: String, returnValue: Int) :
        CompiledStatementForTest<Int>(sql, returnValue)


internal class CompiledStatementForQueryForTest(sql: String, returnValue: Source) :
        CompiledStatementForTest<Source>(sql, returnValue)