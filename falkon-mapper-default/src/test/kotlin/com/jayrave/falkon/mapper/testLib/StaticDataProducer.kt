package com.jayrave.falkon.mapper.testLib

import com.jayrave.falkon.mapper.DataProducer

internal class StaticDataProducer private constructor(
        private val shortProvider: () -> Short?,
        private val intProvider: () -> Int?,
        private val longProvider: () -> Long?,
        private val floatProvider: () -> Float?,
        private val doubleProvider: () -> Double?,
        private val stringProvider: () -> String?,
        private val blobProvider: () -> ByteArray?,
        private val isNull: Boolean) : DataProducer {

    override fun getShort(): Short? = shortProvider.invoke()
    override fun getInt(): Int? = intProvider.invoke()
    override fun getLong(): Long? = longProvider.invoke()
    override fun getFloat(): Float? = floatProvider.invoke()
    override fun getDouble(): Double? = doubleProvider.invoke()
    override fun getString(): String? = stringProvider.invoke()
    override fun getBlob(): ByteArray? = blobProvider.invoke()
    override fun isNull(): Boolean = isNull


    companion object {

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

        fun createForString(string: String?): DataProducer {
            return create(stringProvider = { string }, isNull = string == null)
        }

        fun createForBlob(blob: ByteArray?): DataProducer {
            return create(blobProvider = { blob }, isNull = blob == null)
        }

        private fun create(
                shortProvider: () -> Short? = buildThrowingFunction(),
                intProvider: () -> Int? = buildThrowingFunction(),
                longProvider: () -> Long? = buildThrowingFunction(),
                floatProvider: () -> Float? = buildThrowingFunction(),
                doubleProvider: () -> Double? = buildThrowingFunction(),
                stringProvider: () -> String? = buildThrowingFunction(),
                blobProvider: () -> ByteArray? = buildThrowingFunction(),
                isNull: Boolean): DataProducer {

            return StaticDataProducer(
                    shortProvider, intProvider, longProvider, floatProvider,
                    doubleProvider, stringProvider, blobProvider, isNull
            )
        }

        private fun <T> buildThrowingFunction(): () -> T {
            return { throw RuntimeException("No can do") }
        }
    }
}