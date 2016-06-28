package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.ModelForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForUpdatesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testUpdateOfSingleModel() {
        val modelToBeUpdated = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeUpdated)
        insertAdditionalRandomModels(count = 7)

        val updatedModel = buildModelForTest(88, modelToBeUpdated.id)
        val numberOfRowsUpdated = table.dao.update(updatedModel)

        assertPresenceOf(updatedModel)
        assertThat(numberOfRowsUpdated).isEqualTo(1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateOfVarargModels() {
        val modelToBeUpdated1 = buildModelForTest(1)
        val modelToBeUpdated2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeUpdated1, modelToBeUpdated2)
        insertAdditionalRandomModels(count = 6)

        val updatedModel1 = buildModelForTest(66, modelToBeUpdated1.id)
        val updatedModel2 = buildModelForTest(99, modelToBeUpdated2.id)
        val numberOfRowsUpdated = table.dao.update(updatedModel1, updatedModel2)

        assertPresenceOf(updatedModel1, updatedModel2)
        assertThat(numberOfRowsUpdated).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateOfModelIterable() {
        val modelToBeUpdated1 = buildModelForTest(1)
        val modelToBeUpdated2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeUpdated1, modelToBeUpdated2)
        insertAdditionalRandomModels(count = 6)

        val updatedModel1 = buildModelForTest(55, modelToBeUpdated1.id)
        val updatedModel2 = buildModelForTest(77, modelToBeUpdated2.id)
        val numberOfRowsUpdated = table.dao.update(listOf(updatedModel1, updatedModel2))

        assertPresenceOf(updatedModel1, updatedModel2)
        assertThat(numberOfRowsUpdated).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateHasNoEffectIfModelDoesNotExist() {
        insertAdditionalRandomModels(count = 8)
        val nonExistingModel = buildModelForTest(1)
        val numberOfRowsUpdated = table.dao.update(nonExistingModel)

        val compiledQuery = table.dao.queryBuilder()
                .where()
                .eq(table.id, nonExistingModel.id)
                .build()

        val source = compiledQuery.execute()

        assertThat(source.moveToNext()).isFalse()
        assertThat(numberOfRowsUpdated).isEqualTo(0)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)

        source.close()
        compiledQuery.close()
    }


    private fun insertAdditionalRandomModels(count: Int) {
        (0..count - 1).forEach {
            insertModelUsingInsertBuilder(table, buildModelForTest(seedValue = it.toShort()))
        }
    }


    private fun assertPresenceOf(vararg models: ModelForTest) {
        models.forEach {
            val compiledQuery = table.dao.queryBuilder().where().eq(table.id, it.id).build()
            val source = compiledQuery.execute()

            assertThat(source.moveToNext()).isTrue()
            assertCurrentRowCorrespondsTo(source, it, table)
            assertThat(source.moveToNext()).isFalse()

            source.close()
            compiledQuery.close()
        }
    }
}