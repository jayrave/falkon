package com.jayrave.falkon.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForInsertOrReplacesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testInsertionOfSingleModel() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, 7)
        val modelForTest = buildModelForTest(5)
        table.dao.insertOrReplace(modelForTest)

        assertPresenceOf(table, modelForTest)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testReplacementOfSingleModel() {
        val modelForTest = buildModelForTest(5)
        table.dao.insertOrReplace(modelForTest)
        assertPresenceOf(table, modelForTest)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(1)

        val replacementModel = buildModelForTest(
                seedValue = 6, id1 = modelForTest.id1, id2 = modelForTest.id2
        )

        table.dao.insertOrReplace(replacementModel)
        assertAbsenceOf(table, modelForTest)
        assertPresenceOf(table, replacementModel)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(1)
    }


    @Test
    fun testInsertionOfVarargModels() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, 6)
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insertOrReplace(modelForTest1, modelForTest2)

        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testReplacementOfOneOfVarargModels() {
        val modelForTest1 = buildModelForTest(5)
        insertModelsUsingInsertBuilder(table, modelForTest1)
        assertPresenceOf(table, modelForTest1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(1)

        val modelForTest2 = buildModelForTest(8)
        val replacementForModel1 = buildModelForTest(
                seedValue = 6, id1 = modelForTest1.id1, id2 = modelForTest1.id2
        )

        table.dao.insertOrReplace(replacementForModel1, modelForTest2)

        assertAbsenceOf(table, modelForTest1)
        assertPresenceOf(table, replacementForModel1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)
    }


    @Test
    fun testReplacementOfAllInVarargModels() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        insertModelsUsingInsertBuilder(table, modelForTest1, modelForTest2)
        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)

        val replacementForModel1 = buildModelForTest(
                seedValue = 6, id1 = modelForTest1.id1, id2 = modelForTest1.id2
        )

        val replacementForModel2 = buildModelForTest(
                seedValue = 9, id1 = modelForTest2.id1, id2 = modelForTest2.id2
        )

        table.dao.insertOrReplace(replacementForModel1, replacementForModel2)

        assertAbsenceOf(table, modelForTest1, modelForTest2)
        assertPresenceOf(table, replacementForModel1, replacementForModel2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)
    }


    @Test
    fun testInsertionOfModelIterable() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, 6)
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insertOrReplace(listOf(modelForTest1, modelForTest2))

        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testReplacementOfOneOfModelIterable() {
        val modelForTest1 = buildModelForTest(5)
        insertModelsUsingInsertBuilder(table, modelForTest1)
        assertPresenceOf(table, modelForTest1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(1)

        val modelForTest2 = buildModelForTest(8)
        val replacementForModel1 = buildModelForTest(
                seedValue = 6, id1 = modelForTest1.id1, id2 = modelForTest1.id2
        )

        table.dao.insertOrReplace(listOf(replacementForModel1, modelForTest2))

        assertAbsenceOf(table, modelForTest1)
        assertPresenceOf(table, replacementForModel1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)
    }


    @Test
    fun testReplacementOfAllInModelIterable() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        insertModelsUsingInsertBuilder(table, modelForTest1, modelForTest2)
        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)

        val replacementForModel1 = buildModelForTest(
                seedValue = 6, id1 = modelForTest1.id1, id2 = modelForTest1.id2
        )

        val replacementForModel2 = buildModelForTest(
                seedValue = 9, id1 = modelForTest2.id1, id2 = modelForTest2.id2
        )

        table.dao.insertOrReplace(listOf(replacementForModel1, replacementForModel2))

        assertAbsenceOf(table, modelForTest1, modelForTest2)
        assertPresenceOf(table, replacementForModel1, replacementForModel2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(2)
    }
}