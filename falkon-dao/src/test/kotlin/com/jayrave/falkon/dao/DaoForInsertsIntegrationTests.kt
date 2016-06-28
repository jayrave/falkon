package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.dao.testLib.ModelForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForInsertsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testInsertionOfSingleModel() {
        val modelForTest = buildModelForTest(5)
        table.dao.insert(modelForTest)
        assertPresenceOfOnly(modelForTest)
    }


    @Test
    fun testInsertionOfVarargModels() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(modelForTest1, modelForTest2)
        assertPresenceOfOnly(modelForTest1, modelForTest2)
    }


    @Test
    fun testInsertionOfModelIterable() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(listOf(modelForTest1, modelForTest2))
        assertPresenceOfOnly(modelForTest1, modelForTest2)
    }


    private fun assertPresenceOfOnly(
            vararg models: ModelForTest, orderedBy: Column<ModelForTest, *> = table.short) {

        val source = table.dao.queryBuilder().orderBy(orderedBy, true).build().execute()
        models.forEach {
            assertThat(source.moveToNext()).isTrue()
            assertCurrentRowCorrespondsTo(source, it, table)
        }

        assertThat(source.moveToNext()).isFalse()
    }
}