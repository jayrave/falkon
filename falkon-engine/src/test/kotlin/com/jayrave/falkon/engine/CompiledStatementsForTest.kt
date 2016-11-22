package com.jayrave.falkon.engine

internal open class CompiledStatementForTest<R>(
        override val sql: String, private val returnValue: R,
        private val shouldThrowOnExecution: Boolean) :
        CompiledStatement<R> {

    override fun execute(): R {
        return when (shouldThrowOnExecution) {
            true -> throw RuntimeException()
            else -> returnValue
        }
    }

    override fun bindShort(index: Int, value: Short) = this
    override fun bindInt(index: Int, value: Int) = this
    override fun bindLong(index: Int, value: Long) = this
    override fun bindFloat(index: Int, value: Float) = this
    override fun bindDouble(index: Int, value: Double) = this
    override fun bindString(index: Int, value: String) = this
    override fun bindBlob(index: Int, value: ByteArray) = this
    override fun bindNull(index: Int, type: Type) = this
    override fun close() {}
    override fun clearBindings() = this
}


internal class UnitReturningCompiledStatementForTest(
        sql: String, shouldThrowOnExecution: Boolean = false) :
        CompiledStatementForTest<Unit>(sql, Unit, shouldThrowOnExecution)


internal class IntReturningCompiledStatementForTest(
        sql: String, returnValue: Int, shouldThrowOnExecution: Boolean = false) :
        CompiledStatementForTest<Int>(sql, returnValue, shouldThrowOnExecution)


internal class CompiledStatementForQueryForTest(
        sql: String, returnValue: Source, shouldThrowOnExecution: Boolean = false) :
        CompiledStatementForTest<Source>(sql, returnValue, shouldThrowOnExecution)