package com.jayrave.falkon

/**
 * A [DataConsumer] that redirects `put` calls to `putNull` if the passed in value is `null`.
 * Otherwise, the call is just forwarded to the method that handles the corresponding non-null type
 */
abstract class NullHandlingDataConsumer : DataConsumer {

    override final fun put(byte: Byte?) {
        when(byte) {
            null -> putNull()
            else -> put(byte)
        }
    }

    override final fun put(char: Char?) {
        when(char) {
            null -> putNull()
            else -> put(char)
        }
    }

    override final fun put(short: Short?) {
        when(short) {
            null -> putNull()
            else -> put(short)
        }
    }

    override final fun put(int: Int?) {
        when(int) {
            null -> putNull()
            else -> put(int)
        }
    }

    override final fun put(long: Long?) {
        when(long) {
            null -> putNull()
            else -> put(long)
        }
    }

    override final fun put(float: Float?) {
        when(float) {
            null -> putNull()
            else -> put(float)
        }
    }

    override final fun put(double: Double?) {
        when(double) {
            null -> putNull()
            else -> put(double)
        }
    }

    override final fun put(boolean: Boolean?) {
        when(boolean) {
            null -> putNull()
            else -> put(boolean)
        }
    }

    override final fun put(string: String?) {
        when(string) {
            null -> putNull()
            else -> putNonNullString(string)
        }
    }

    override final fun put(blob: ByteArray?) {
        when(blob) {
            null -> putNull()
            else -> putNonNullBlob(blob)
        }
    }


    abstract fun put(byte: Byte)
    abstract fun put(char: Char)
    abstract fun put(short: Short)
    abstract fun put(int: Int)
    abstract fun put(long: Long)
    abstract fun put(float: Float)
    abstract fun put(double: Double)
    abstract fun put(boolean: Boolean)
    abstract fun putNonNullString(string: String)
    abstract fun putNonNullBlob(blob: ByteArray)
}