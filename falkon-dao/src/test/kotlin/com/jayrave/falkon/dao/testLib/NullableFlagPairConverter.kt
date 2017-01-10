package com.jayrave.falkon.dao.testLib

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.Converter
import com.jayrave.falkon.mapper.DataConsumer
import com.jayrave.falkon.mapper.DataProducer

internal class NullableFlagPairConverter : Converter<FlagPair?> {

    override val dbType: Type get() = Companion.dbType

    override fun from(dataProducer: DataProducer): FlagPair? {
        return when {
            dataProducer.isNull() -> null
            else -> {
                val shortString = dataProducer.getShort().toString()
                assert(shortString.length == 2)
                FlagPair(
                        shortString.first().toShort().flag,
                        shortString.second().toShort().flag
                )
            }
        }
    }

    override fun to(value: FlagPair?, dataConsumer: DataConsumer) {
        when (value) {
            null -> dataConsumer.put(null as Short?)
            else -> dataConsumer.put(asShort(value))
        }
    }


    companion object {
        private const val TRUE_SHORT: Short = 2
        private const val FALSE_SHORT: Short = 7

        private fun String.second() = this[1]
        private val Short.flag: Boolean
            get() = when (this) {
                TRUE_SHORT -> true
                FALSE_SHORT -> false
                else -> throw IllegalArgumentException("Invalid: $this")
            }

        private val Boolean.short: Short
            get() = when (this) {
                true -> TRUE_SHORT
                false -> FALSE_SHORT
            }

        val dbType: Type get() = Type.SHORT
        fun asShort(flagPair: FlagPair): Short {
            return ((flagPair.first.short * 10) + flagPair.second.short).toShort()
        }
    }
}