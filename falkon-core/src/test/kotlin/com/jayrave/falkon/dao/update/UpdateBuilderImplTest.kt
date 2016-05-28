package com.jayrave.falkon.dao.update

import com.jayrave.falkon.*
import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.Factory
import com.jayrave.falkon.engine.Sink
import com.jayrave.falkon.lib.MapBackedSink
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class UpdateBuilderImplTest {

    private val table = TableForTest()
    private val engine = table.configuration.engine
    private val builder = UpdateBuilderImpl(table)

    @Test
    fun testUpdateWithoutWhere() {
        val inputValue = 5
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(inputValue)

        builder.set(table.int, inputValue).update()
        verifyCallToUpdate(expectedSunkValues, null, null)
    }


    @Test
    fun testUpdateWithWhere() {
        val inputValue = 5
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(inputValue)

        builder.set(table.int, inputValue).where().eq(table.string, "test").update()
        verifyCallToUpdate(expectedSunkValues, "string = ?", listOf("test"))
    }


    @Test
    fun testCanUpdateMultipleColumns() {
        val inputInt = 5
        val inputString = "test"
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(inputInt)
        expectedSunkValues[table.string.name] = table.string.computeStorageFormOf(inputString)

        builder.set(table.int, inputInt).set(table.string, inputString).update()
        verifyCallToUpdate(expectedSunkValues, null, null)
    }


    @Test
    fun testSetOverwritesExistingValueForTheSameColumn() {
        val initialValue = 5
        val overwritingValue = initialValue + 1
        val expectedSunkValues = HashMap<String, Any?>()
        expectedSunkValues[table.int.name] = table.int.computeStorageFormOf(overwritingValue)

        builder.set(table.int, initialValue).set(table.int, overwritingValue).update()
        verifyCallToUpdate(expectedSunkValues, null, null)
    }


    @Test
    fun testDefiningSetAndWhereClausesDoesNotFireAnUpdateCall() {
        builder.set(table.int, 5).where().eq(table.string, "test")
        verify(engine, never()).update(any(), any(), any(), anyCollection())
    }


    @Test
    fun testUpdateReportsCorrectRowCount() {
        val numberOfRowsAffected = 183
        whenever(engine.update(any(), any(), any(), anyCollection())).thenReturn(numberOfRowsAffected)
        assertThat(builder.set(table.int, 5).where().eq(table.string, "test").update()).isEqualTo(numberOfRowsAffected)
    }


    private fun verifyCallToUpdate(
            expectedSunkValues: Map<String, Any?>, expectedWhereClause: String?,
            expectedWhereArgs: Iterable<Any?>?) {

        // TableConfigurationForTest uses an mock engine that returns a factory that generates MapBackedSink.
        // Capture and compare it against the passed in expectedSunkValues
        val sinkCaptor = argumentCaptor<Sink>()
        verify(engine).update(
                eq(table.name), capture(sinkCaptor),
                if (expectedWhereClause != null) eq(expectedWhereClause) else isNull<String>(),
                if (expectedWhereArgs != null) eq(expectedWhereArgs) else isNull<Iterable<*>>()
        )

        val actualSunkValues = (sinkCaptor.value as MapBackedSink).sunkValues()
        assertThat(actualSunkValues).isEqualTo(expectedSunkValues)
    }



    private class ModelForTest(
            val int: Int = 0,
            val string: String = "test"
    )



    private class TableForTest(
            configuration: TableConfiguration<Sink> = defaultConfiguration()) :
            BaseTable<ModelForTest, Int, Dao<ModelForTest, Int, Sink>, Sink>("test", configuration) {

        override val dao: Dao<ModelForTest, Int, Sink> = mock()
        override val idColumn: Column<ModelForTest, Int> = mock()
        override fun create(value: Value<ModelForTest>) = throw UnsupportedOperationException()

        val int = col(ModelForTest::int)
        val string = col(ModelForTest::string)

        companion object {
            private fun defaultConfiguration(): TableConfiguration<Sink> {
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