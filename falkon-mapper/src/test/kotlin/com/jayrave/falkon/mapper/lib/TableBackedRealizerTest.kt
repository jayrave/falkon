package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Realizer
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.mapper.TableConfiguration
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.util.*

class TableBackedRealizerTest {

    @Test
    fun `realize`() {
        val expectedId = UUID.randomUUID()
        val expectedInt = 5
        val expectedString = "test 6"
        val tableForTest = TableForTest()

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY", "unused")
        val realizerValue = object : Realizer.Value {
            override fun <C> of(columnName: String) = throw exception
            override fun <C> of(column: Column<*, C>): C {
                return when (column) {
                    tableForTest.idCol -> expectedId
                    tableForTest.intCol -> expectedInt
                    tableForTest.stringCol -> expectedString
                    else -> throw exception
                } as C
            }
        }

        val builtModelForTest = TableBackedRealizer(tableForTest).realize(realizerValue)
        assertThat(builtModelForTest.id).isEqualTo(expectedId)
        assertThat(builtModelForTest.int).isEqualTo(expectedInt)
        assertThat(builtModelForTest.string).isEqualTo(expectedString)
    }


    private class ModelForTest(val id: UUID, val int: Int, val string: String)


    private class TableForTest : Table<ModelForTest, UUID> {
        override val name: String get() = throw exception
        override val configuration: TableConfiguration get() = throw exception
        override val allColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override val idColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override val nonIdColumns: Collection<Column<ModelForTest, *>> get() = throw exception
        override fun <C> extractFrom(id: UUID, column: Column<ModelForTest, C>) = throw exception

        val idCol: Column<ModelForTest, UUID> = mock()
        val intCol: Column<ModelForTest, Int> = mock()
        val stringCol: Column<ModelForTest, String> = mock()

        override fun create(value: Table.Value<ModelForTest>): ModelForTest {
            return ModelForTest(value of idCol, value of intCol, value of stringCol)
        }
    }


    companion object {
        private val exception = UnsupportedOperationException()
    }
}