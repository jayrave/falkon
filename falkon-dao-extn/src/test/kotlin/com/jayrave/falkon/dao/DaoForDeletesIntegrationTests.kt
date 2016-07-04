package com.jayrave.falkon.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForDeletesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testDeletionOfSingleModel() {
        val modelToBeDeleted = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeDeleted)
        insertAdditionalRandomModels(table, count = 7)

        val numberOfRowsDeleted = table.dao.delete(modelToBeDeleted)

        assertAbsenceOf(table, modelToBeDeleted)
        assertThat(numberOfRowsDeleted).isEqualTo(1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(7)
    }


    @Test
    fun testDeletionOfVarargModels() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModels(table, count = 6)

        val deletedModel1 = buildModelForTest(66, modelToBeDeleted1.id)
        val deletedModel2 = buildModelForTest(99, modelToBeDeleted2.id)
        val numberOfRowsDeleted = table.dao.delete(deletedModel1, deletedModel2)

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testDeletionOfModelIterable() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModels(table, count = 6)

        val deletedModel1 = buildModelForTest(55, modelToBeDeleted1.id)
        val deletedModel2 = buildModelForTest(77, modelToBeDeleted2.id)
        val numberOfRowsDeleted = table.dao.delete(listOf(deletedModel1, deletedModel2))

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testSingleModelDeleteById() {
        val modelToBeDeleted = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeDeleted)
        insertAdditionalRandomModels(table, count = 7)

        val numberOfRowsDeleted = table.dao.deleteById(modelToBeDeleted.id)

        assertAbsenceOf(table, modelToBeDeleted)
        assertThat(numberOfRowsDeleted).isEqualTo(1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(7)
    }


    @Test
    fun testVarargModelsDeleteById() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModels(table, count = 6)

        val deletedModel1 = buildModelForTest(66, modelToBeDeleted1.id)
        val deletedModel2 = buildModelForTest(99, modelToBeDeleted2.id)
        val numberOfRowsDeleted = table.dao.deleteById(deletedModel1.id, deletedModel2.id)

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testModelIterableDeleteById() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModels(table, count = 6)

        val deletedModel1 = buildModelForTest(55, modelToBeDeleted1.id)
        val deletedModel2 = buildModelForTest(77, modelToBeDeleted2.id)
        val numberOfRowsDeleted = table.dao.deleteById(listOf(deletedModel1.id, deletedModel2.id))

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testDeletionHasNoEffectIfModelDoesNotExist() {
        insertAdditionalRandomModels(table, count = 8)
        val nonExistingModel = buildModelForTest(1)
        val numberOfRowsDeleted = table.dao.delete(nonExistingModel)

        assertAbsenceOf(table, nonExistingModel)
        assertThat(numberOfRowsDeleted).isEqualTo(0)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }
}