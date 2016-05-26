package com.jayrave.falkon.dao.insert

import com.jayrave.falkon.*
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Factory
import com.jayrave.falkon.engine.Sink
import com.jayrave.falkon.lib.MapBackedSink
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class InsertBuilderImplTest {

    private val table = TableForTest()
    private val engine = table.configuration.engine
    private val builder = InsertBuilderImpl(table)

    @Test
    fun testInsert() {
        val inputValue = 5
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(inputValue)

        builder.set(table.int, inputValue).insert()
        verifyCallToInsert(expectedSunkValues)
    }


    @Test
    fun testCanInsertValuesForMultipleColumns() {
        val inputInt = 5
        val inputString = "test"
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(inputInt)
        expectedSunkValues[table.string.name] = table.string.computeStorageFormOf(inputString)

        builder.set(table.int, inputInt).set(table.string, inputString).insert()
        verifyCallToInsert(expectedSunkValues)
    }


    @Test
    fun testSetOverwritesExistingValueForTheSameColumn() {
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(overwritingValue)

        builder.set(table.int, initialValue).set(table.int, overwritingValue).insert()
        verifyCallToInsert(expectedSunkValues)
    }


    @Test
    fun testDefiningSetDoesNotFireAnUpdateCall() {
        builder.set(table.int, 5)
        verify(engine, never()).insert(any(), any())
    }


    @Test
    fun testInsertReportsCorrectRowId() {
        val rowIdToReturn = 3764L
        whenever(engine.insert(any(), any())).thenReturn(rowIdToReturn)
        assertThat(builder.set(table.int, 5).insert()).isEqualTo(rowIdToReturn)
    }


    private fun verifyCallToInsert(expectedSunkValues: Map<String, Any?>) {
        // TableConfigurationForTest uses an mock engine that returns a factory that generates MapBackedSink.
        // Capture and compare it against the passed in expectedSunkValues
        val sinkCaptor = argumentCaptor<Sink>()
        verify(engine).insert(eq(table.name), capture(sinkCaptor))

        val actualSunkValues = (sinkCaptor.value as MapBackedSink).sunkValues()
        assertThat(actualSunkValues).isEqualTo(expectedSunkValues)
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test"
    )



    private class TableForTest(
            configuration: TableConfiguration<Engine<Sink>, Sink> = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Engine<Sink>, Sink>("test", configuration) {

        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)

        companion object {
            private fun defaultConfiguration(): TableConfiguration<Engine<Sink>, Sink> {
                val engineMock: Engine<Sink> = mock()
                whenever(engineMock.sinkFactory).thenReturn(object : Factory<Sink> {
                    override fun create() = MapBackedSink()
                })

                val configuration = TableConfigurationImpl(engineMock)
                configuration.registerDefaultConverters()
                return configuration
            }
        }
    }
}