package com.jayrave.falkon.mapper.testLib

import com.jayrave.falkon.mapper.DataProducer

internal class StaticDataProducer private constructor(
        private val shortProvider: () -> Short,
        private val intProvider: () -> Int,
        private val longProvider: () -> Long,
        private val floatProvider: () -> Float,
        private val doubleProvider: () -> Double,
        private val stringProvider: () -> String,
        private val blobProvider: () -> ByteArray,
        private val isNull: Boolean) : DataProducer {

    override fun getShort(): Short = shortProvider.invoke()
    override fun getInt(): Int = intProvider.invoke()
    override fun getLong(): Long = longProvider.invoke()
    override fun getFloat(): Float = floatProvider.invoke()
    override fun getDouble(): Double = doubleProvider.invoke()
    override fun getString(): String = stringProvider.invoke()
    override fun getBlob(): ByteArray = blobProvider.invoke()
    override fun isNull(): Boolean = isNull


    companion object {

        fun createForShort(short: Short?): DataProducer {
            return when (short) {
                null -> create(isNull = true)
                else -> create(shortProvider = { short }, isNull = false)
            }
        }

        fun createForInt(int: Int?): DataProducer {
            return when (int) {
                null -> create(isNull = true)
                else -> create(intProvider = { int }, isNull = false)
            }
        }

        fun createForLong(long: Long?): DataProducer {
            return when (long) {
                null -> create(isNull = true)
                else -> create(longProvider = { long }, isNull = false)
            }
        }

        fun createForFloat(float: Float?): DataProducer {
            return when (float) {
                null -> create(isNull = true)
                else -> create(floatProvider = { float }, isNull = false)
            }
        }

        fun createForDouble(double: Double?): DataProducer {
            return when (double) {
                null -> create(isNull = true)
                else -> create(doubleProvider = { double }, isNull = false)
            }
        }

        fun createForString(string: String?): DataProducer {
            return when (string) {
                null -> create(isNull = true)
                else -> create(stringProvider = { string }, isNull = false)
            }
        }

        fun createForBlob(blob: ByteArray?): DataProducer {
            return when (blob) {
                null -> create(isNull = true)
                else -> create(blobProvider = { blob }, isNull = false)
            }
        }

        private fun create(
                shortProvider: () -> Short = buildThrowingFunction(),
                intProvider: () -> Int = buildThrowingFunction(),
                longProvider: () -> Long = buildThrowingFunction(),
                floatProvider: () -> Float = buildThrowingFunction(),
                doubleProvider: () -> Double = buildThrowingFunction(),
                stringProvider: () -> String = buildThrowingFunction(),
                blobProvider: () -> ByteArray = buildThrowingFunction(),
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