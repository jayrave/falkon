package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.testLib.TableForTest
import com.jayrave.falkon.dao.testLib.testEquality
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class DaoForQueriesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testFindByIdReturnsAppropriateModel() {
        val modelToBeQueried = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeQueried)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 7)

        val queriedModel = table.dao.findById(
                TableForTest.Id(modelToBeQueried.id1, modelToBeQueried.id2)
        )

        assertThat(queriedModel).isNotNull()
        assertThat(modelToBeQueried.testEquality(queriedModel!!)).isTrue()
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testFindByIdReturnsNullOnIdOfNonExistingModel() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 8)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
        assertThat(
                table.dao.findById(TableForTest.Id(UUID.randomUUID(), UUID.randomUUID()))
        ).isNull()
    }


    @Test
    fun testFindAllReturnsAllModels() {
        val numberOfModelsToCreate = 80
        val modelsToBeQueried = (0..numberOfModelsToCreate - 1).map {
            buildModelForTest(it.toShort())
        }

        // Insert all models
        insertModelsUsingInsertBuilder(table, *modelsToBeQueried.toTypedArray())

        val queriedModels = table.dao.findAll()
        assertThat(queriedModels).isNotNull()
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(numberOfModelsToCreate)

        val orderedQueriedModels = queriedModels.sortedBy { it.short }
        assertThat(orderedQueriedModels.size).isEqualTo(modelsToBeQueried.size)
        orderedQueriedModels.forEachIndexed { index, modelForTest ->
            assertThat(modelForTest.testEquality(modelsToBeQueried[index])).isTrue()
        }
    }


    @Test
    fun testFindAllReturnsEmptyListIfTableIsEmpty() {
        val queriedModels = table.dao.findAll()
        assertThat(queriedModels).isEmpty()
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(0)
    }
}