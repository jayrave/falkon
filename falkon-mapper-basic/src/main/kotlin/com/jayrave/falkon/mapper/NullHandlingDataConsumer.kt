package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type

/**
 * A [DataConsumer] that redirects `put` calls to `putNull` if the passed in value is `null`.
 * Otherwise, the call is just forwarded to the method that handles the corresponding non-null type
 */
internal abstract class NullHandlingDataConsumer : DataConsumer {

    override final fun put(short: Short?) {
        when (short) {
            null -> putNull(Type.SHORT)
            else -> put(short)
        }
    }

    override final fun put(int: Int?) {
        when (int) {
            null -> putNull(Type.INT)
            else -> put(int)
        }
    }

    override final fun put(long: Long?) {
        when (long) {
            null -> putNull(Type.LONG)
            else -> put(long)
        }
    }

    override final fun put(float: Float?) {
        when (float) {
            null -> putNull(Type.FLOAT)
            else -> put(float)
        }
    }

    override final fun put(double: Double?) {
        when (double) {
            null -> putNull(Type.DOUBLE)
            else -> put(double)
        }
    }

    override final fun put(string: String?) {
        when (string) {
            null -> putNull(Type.STRING)
            else -> putNonNullString(string)
        }
    }

    override final fun put(blob: ByteArray?) {
        when (blob) {
            null -> putNull(Type.BLOB)
            else -> putNonNullBlob(blob)
        }
    }


    protected abstract fun put(short: Short)
    protected abstract fun put(int: Int)
    protected abstract fun put(long: Long)
    protected abstract fun put(float: Float)
    protected abstract fun put(double: Double)
    protected abstract fun putNonNullString(string: String)
    protected abstract fun putNonNullBlob(blob: ByteArray)
    protected abstract fun putNull(type: Type)
}