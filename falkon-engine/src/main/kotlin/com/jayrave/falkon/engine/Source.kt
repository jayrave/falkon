package com.jayrave.falkon.engine

import java.io.Closeable

/**
 * This interface provides random read access to the result set returned by a database query.
 * [Source] implementations are not required to be synchronized so code using a Source from
 * multiple threads should perform its own synchronization
 */
interface Source : Closeable {

    /**
     * Returns the current position the source is at in the row set. The value is 1-based.
     * When the row set is first returned the source will be at position 0, which is before
     * the first row. After the last row is returned another call to next() will leave the
     * cursor past the last entry
     *
     * @return source's current position i.e., the row it is pointing to
     */
    val position: Int

    /**
     * Move the source by a relative amount, forward or backward, from the
     * current position. Positive offsets move forwards, negative offsets move
     * backwards. If the value is too large, the position is moved after the last row,
     * if the value is too small it is moved before the first row
     *
     * [offset] the offset to be applied from the current position (1-based)
     * @return whether the requested move fully succeeded
     */
    fun move(offset: Int): Boolean

    /**
     * Move the source to an absolute position. The valid range of values is [1, [count]].
     * If the value is too large, the position is moved after the last row, if the value
     * is too small it is moved before the first row
     *
     * [position] the 1-based position to move to
     * @return whether the requested move fully succeeded
     */
    fun moveToPosition(position: Int): Boolean

    /**
     * Move the source to the first row. This method will return false if the source is empty
     *
     * @return whether the move succeeded
     */
    fun moveToFirst(): Boolean

    /**
     * Move the source to the last row. This method will return false if the source is empty
     *
     * @return whether the move succeeded
     */
    fun moveToLast(): Boolean

    /**
     * Move the source to the next row. This method will return false if the source is
     * already past the last entry in the result set
     *
     * @return whether the move succeeded
     */
    fun moveToNext(): Boolean

    /**
     * Move the source to the previous row. This method will return false if the source is
     * already before the first entry in the result set
     *
     * @return whether the move succeeded
     */
    fun moveToPrevious(): Boolean

    /**
     * @return index (1-based) corresponding to the column name if its exists in
     * the result set else 0
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

    /**
     * @return whether this source is closed or not
     */
    fun isClosed(): Boolean
}