package com.jayrave.falkon.engine

/**
 * This interface provides random read-write access to the result set returned by a database query.
 * [Source] implementations are not required to be synchronized so code using a Source from
 * multiple threads should perform its own synchronization
 */
interface Source {

    /**
     * @return the number of rows in the source's row set
     */
    val rowCount: Int

    /**
     * Returns the current position the source is at in the row set. The value is zero-based.
     * When the row set is first returned the source will be at position -1, which is before
     * the first row. After the last row is returned another call to next() will leave the
     * cursor past the last entry, at a position of [rowCount]
     *
     * @return source's current position i.e., the row it is pointing to
     */
    val position: Int


    /**
     * Move the source by a relative amount, forward or backward, from the
     * current position. Positive offsets move forwards, negative offsets move
     * backwards. If the final position is outside of the bounds of the result
     * set then the resultant position will be pinned to -1 or count() depending
     * on whether the value is off the front or end of the set, respectively
     *
     * This method will return true if the requested destination was
     * reachable, otherwise, it returns false. For example, if the cursor is at
     * currently on the second entry in the result set and move(-5) is called,
     * the position will be pinned at -1, and false will be returned
     *
     * @param offset the offset to be applied from the current position
     * @return whether the requested move fully succeeded
     */
    fun move(offset: Int): Boolean

    /**
     * Move the source to an absolute position. The valid range of values is [-1, [count]]
     *
     * This method will return true if the request destination was reachable,
     * otherwise, it returns false
     *
     * @param position the zero-based position to move to
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
     * @return index corresponding to the column name if its exists in the result set else 0
     */
    fun getColumnIndex(columnName: String): Int

    fun getShort(columnIndex: Int): Short
    fun getInt(columnIndex: Int): Int
    fun getLong(columnIndex: Int): Long
    fun getFloat(columnIndex: Int): Float
    fun getDouble(columnIndex: Int): Double
    fun getString(columnIndex: Int): String
    fun getBlob(columnIndex: Int): ByteArray
    fun isNull(columnIndex: Int): Boolean
}