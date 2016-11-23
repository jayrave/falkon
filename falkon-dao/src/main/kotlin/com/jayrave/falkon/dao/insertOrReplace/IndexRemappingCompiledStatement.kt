package com.jayrave.falkon.dao.insertOrReplace

import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Type

/**
 * Used in situations where the index has to be remapped. For eg., may be when the user
 * binds a value to index 2, there could be a requirement to redirect that binding to
 * some other index
 *
 * To get the target index for an index passed into any of the binding methods
 * ([bindShort], [bindInt]...), [indexMap] is used. Int returned by [indexMap]
 * for index is used as the target index of [delegate] to bind to
 */
internal class IndexRemappingCompiledStatement(
        private val delegate: CompiledStatement<Int>,
        private val indexMap: IntArray) :
        CompiledStatement<Int> by delegate {

    private fun findTargetIndexForDelegate(index: Int): Int {
        return when (index) {
            0 -> throw ArrayIndexOutOfBoundsException("Index should be in [1, ..]")
            else -> indexMap[index]
        }
    }

    override fun bindShort(index: Int, value: Short): CompiledStatement<Int> {
        delegate.bindShort(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindInt(index: Int, value: Int): CompiledStatement<Int> {
        delegate.bindInt(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindLong(index: Int, value: Long): CompiledStatement<Int> {
        delegate.bindLong(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindFloat(index: Int, value: Float): CompiledStatement<Int> {
        delegate.bindFloat(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindDouble(index: Int, value: Double): CompiledStatement<Int> {
        delegate.bindDouble(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindString(index: Int, value: String): CompiledStatement<Int> {
        delegate.bindString(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindBlob(index: Int, value: ByteArray): CompiledStatement<Int> {
        delegate.bindBlob(findTargetIndexForDelegate(index), value)
        return this
    }

    override fun bindNull(index: Int, type: Type): CompiledStatement<Int> {
        delegate.bindNull(findTargetIndexForDelegate(index), type)
        return this
    }
}