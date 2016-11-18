package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.TableForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForDeletesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testDeletionOfSingleModel() {
        val modelToBeDeleted = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeDeleted)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 7)

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
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val deletedModel1 = buildModelForTest(66, modelToBeDeleted1.id1, modelToBeDeleted1.id2)
        val deletedModel2 = buildModelForTest(99, modelToBeDeleted2.id1, modelToBeDeleted2.id2)
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
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val deletedModel1 = buildModelForTest(55, modelToBeDeleted1.id1, modelToBeDeleted1.id2)
        val deletedModel2 = buildModelForTest(77, modelToBeDeleted2.id1, modelToBeDeleted2.id2)
        val numberOfRowsDeleted = table.dao.delete(listOf(deletedModel1, deletedModel2))

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testSingleModelDeleteById() {
        val modelToBeDeleted = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeDeleted)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 7)

        val numberOfRowsDeleted = table.dao.deleteById(
                TableForTest.Id(modelToBeDeleted.id1, modelToBeDeleted.id2)
        )

        assertAbsenceOf(table, modelToBeDeleted)
        assertThat(numberOfRowsDeleted).isEqualTo(1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(7)
    }


    @Test
    fun testVarargModelsDeleteById() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val deletedModel1 = buildModelForTest(66, modelToBeDeleted1.id1, modelToBeDeleted1.id2)
        val deletedModel2 = buildModelForTest(99, modelToBeDeleted2.id1, modelToBeDeleted2.id2)
        val numberOfRowsDeleted = table.dao.deleteById(
                TableForTest.Id(deletedModel1.id1, deletedModel1.id2),
                TableForTest.Id(deletedModel2.id1, deletedModel2.id2)
        )

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testModelIterableDeleteById() {
        val modelToBeDeleted1 = buildModelForTest(1)
        val modelToBeDeleted2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeDeleted1, modelToBeDeleted2)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val deletedModel1 = buildModelForTest(55, modelToBeDeleted1.id1, modelToBeDeleted1.id2)
        val deletedModel2 = buildModelForTest(77, modelToBeDeleted2.id1, modelToBeDeleted2.id2)
        val numberOfRowsDeleted = table.dao.deleteById(listOf(
                TableForTest.Id(deletedModel1.id1, deletedModel1.id2),
                TableForTest.Id(deletedModel2.id1, deletedModel2.id2)
        ))

        assertAbsenceOf(table, deletedModel1, deletedModel2)
        assertThat(numberOfRowsDeleted).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(6)
    }


    @Test
    fun testDeletionHasNoEffectIfModelDoesNotExist() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 8)
        val nonExistingModel = buildModelForTest(1)
        val numberOfRowsDeleted = table.dao.delete(nonExistingModel)

        assertAbsenceOf(table, nonExistingModel)
        assertThat(numberOfRowsDeleted).isEqualTo(0)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }
}