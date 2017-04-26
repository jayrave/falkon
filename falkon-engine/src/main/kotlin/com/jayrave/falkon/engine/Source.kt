package com.jayrave.falkon.engine

import java.io.Closeable
import java.sql.SQLException

/**
 * This interface provides random read access to the result set returned by a database query.
 * [Source] implementations are not required to be synchronized so code using a source from
 * multiple threads should perform its own synchronization
 */
interface Source : Closeable {

    /**
     * Whether this source is closed or not
     */
    val isClosed: Boolean

    /**
     * Whether this source can move backwards too through the rows of result set
     */
    val canBacktrack: Boolean

    /**
     * Move the source to the next row. This method is a no-op if the source is
     * already past the last entry in the result set
     *
     * *Note:* Source is initially positioned before the first row
     *
     * @return `true` if the new current row is valid
     */
    fun moveToNext(): Boolean

    /**
     * Move the source to the previous row. This method is a no-op if the source can't
     * backtrack or is already before the first entry in the result set
     *
     * *Note:* Source is initially positioned before the first row
     *
     * @return `true` if the new current row is valid. `false` if the source can't
     * backtrack (i.e., [canBacktrack] returns `false`) or is already before the
     * first entry in the result set
     */
    fun moveToPrevious(): Boolean

    /**
     * @return index (1-based) corresponding to the column name if its exists in the result set
     * @throws [SQLException] if there exists no such column
     */
    fun getColumnIndex(columnName: String): Int

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getShort(columnIndex: Int): Short

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getInt(columnIndex: Int): Int

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getLong(columnIndex: Int): Long

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getFloat(columnIndex: Int): Float

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getDouble(columnIndex: Int): Double

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getString(columnIndex: Int): String

    /**
     * [columnIndex] 1-based index to get value from
     */
    fun getBlob(columnIndex: Int): ByteArray

    /**
     * [columnIndex] 1-based index to check
     */
    fun isNull(columnIndex: Int): Boolean

    /**
     * Releases all database resources held by this source. This is a no-op on
     * already closed sources
     */
    override fun close()
}