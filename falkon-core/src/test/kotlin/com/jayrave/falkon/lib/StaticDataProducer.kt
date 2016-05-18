package com.jayrave.falkon.lib

import com.jayrave.falkon.DataProducer

internal class StaticDataProducer private constructor(
        private val byteProvider: () -> Byte?,
        private val charProvider: () -> Char?,
        private val shortProvider: () -> Short?,
        private val intProvider: () -> Int?,
        private val longProvider: () -> Long?,
        private val floatProvider: () -> Float?,
        private val doubleProvider: () -> Double?,
        private val booleanProvider: () -> Boolean?,
        private val stringProvider: () -> String?,
        private val blobProvider: () -> ByteArray?,
        private val isNull: Boolean) : DataProducer {

    override fun getByte(): Byte? = byteProvider.invoke()
    override fun getChar(): Char? = charProvider.invoke()
    override fun getShort(): Short? = shortProvider.invoke()
    override fun getInt(): Int? = intProvider.invoke()
    override fun getLong(): Long? = longProvider.invoke()
    override fun getFloat(): Float? = floatProvider.invoke()
    override fun getDouble(): Double? = doubleProvider.invoke()
    override fun getBoolean(): Boolean? = booleanProvider.invoke()
    override fun getString(): String? = stringProvider.invoke()
    override fun getBlob(): ByteArray? = blobProvider.invoke()
    override fun isNull(): Boolean = isNull


    companion object {

        fun createForByte(byte: Byte?): DataProducer {
            return create(byteProvider = { byte }, isNull = byte == null)
        }

        fun createForChar(char: Char?): DataProducer {
            return create(charProvider = { char }, isNull = char == null)
        }

        fun createForShort(short: Short?): DataProducer {
            return create(shortProvider = { short }, isNull = short == null)
        }

        fun createForInt(int: Int?): DataProducer {
            return create(intProvider = { int }, isNull = int == null)
        }

        fun createForLong(long: Long?): DataProducer {
            return create(longProvider = { long }, isNull = long == null)
        }

        fun createForFloat(float: Float?): DataProducer {
            return create(floatProvider = { float }, isNull = float == null)
        }

        fun createForDouble(double: Double?): DataProducer {
            return create(doubleProvider = { double }, isNull = double == null)
        }

        fun createForBoolean(boolean: Boolean?): DataProducer {
            return create(booleanProvider = { boolean }, isNull = boolean == null)
        }

        fun createForString(string: String?): DataProducer {
            return create(stringProvider = { string }, isNull = string == null)
        }

        fun createForBlob(blob: ByteArray?): DataProducer {
            return create(blobProvider = { blob }, isNull = blob == null)
        }

        private fun create(
                byteProvider: () -> Byte? = buildThrowingFunction(),
                charProvider: () -> Char? = buildThrowingFunction(),
                shortProvider: () -> Short? = buildThrowingFunction(),
                intProvider: () -> Int? = buildThrowingFunction(),
                longProvider: () -> Long? = buildThrowingFunction(),
                floatProvider: () -> Float? = buildThrowingFunction(),
                doubleProvider: () -> Double? = buildThrowingFunction(),
                booleanProvider: () -> Boolean? = buildThrowingFunction(),
                stringProvider: () -> String? = buildThrowingFunction(),
                blobProvider: () -> ByteArray? = buildThrowingFunction(),
                isNull: Boolean): DataProducer {

            return StaticDataProducer(
                    byteProvider, charProvider, shortProvider, intProvider, longProvider, floatProvider,
                    doubleProvider, booleanProvider, stringProvider, blobProvider, isNull
            )
        }

        private fun <T> buildThrowingFunction(): () -> T {
            return { throw RuntimeException("No can do") }
        }
    }
}