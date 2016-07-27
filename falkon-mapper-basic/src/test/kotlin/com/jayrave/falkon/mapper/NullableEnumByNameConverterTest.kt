package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull
import com.jayrave.falkon.mapper.exceptions.ConversionException
import com.jayrave.falkon.mapper.testLib.StaticDataProducer
import org.assertj.core.api.Assertions
import org.junit.Test

class NullableEnumByNameConverterTest {

    private val converter = NullableEnumByNameConverter(TestEnum::class.java)

    @Test
    fun testFromWithNullValue() {
        val dataProducer = StaticDataProducer.createForString(null)
        val producedValue = converter.from(dataProducer)
        Assertions.assertThat(producedValue).isNull()
    }

    @Test
    fun testFromWithNonNullValue() {
        val dataProducer = StaticDataProducer.createForString(TestEnum.OPTION_1.name)
        val producedValue = converter.from(dataProducer)
        Assertions.assertThat(producedValue).isEqualTo(TestEnum.OPTION_1)
    }

    @Test(expected = ConversionException::class)
    fun testFromWithWrongEnumStringThrows() {
        val dataProducer = StaticDataProducer.createForString("lose yourself to dance")
        converter.from(dataProducer)
    }

    @Test
    fun testToWithNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(null, consumer)
        Assertions.assertThat(consumer.mostRecentConsumedValue).isEqualTo(TypedNull(Type.STRING))
    }

    @Test
    fun testToWithNonNullValue() {
        val consumer = ValueHoldingDataConsumer()
        converter.to(TestEnum.OPTION_2, consumer)
        Assertions.assertThat(consumer.mostRecentConsumedValue).isEqualTo(TestEnum.OPTION_2.name)
    }


    private enum class TestEnum {
        OPTION_1,
        OPTION_2,
        OPTION_3
    }
}