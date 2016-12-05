package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.DataProducer
import com.jayrave.falkon.mapper.ReadOnlyColumn
import com.jayrave.falkon.mapper.Realizer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.jayrave.falkon.engine.CompiledStatement as CS

class CompiledQueryExtnTestsForRealizer : BaseClassForCompiledQueryExtnTests() {

    @Test
    fun `extract first model from empty source returns null`() {
        extractAndAssertModelFromEmptySource { realizer, cs ->
            val model = cs.extractFirstModel(realizer, colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            model
        }
    }


    @Test
    fun `extract first model`() {
        extractAndAssertModel { realizer, cs ->
            val model = cs.extractFirstModel(realizer, colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            model
        }
    }


    @Test
    fun `extract first model and close from empty source returns null`() {
        extractAndAssertModelFromEmptySource { realizer, cs ->
            val model = cs.extractFirstModelAndClose(realizer, colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            model
        }
    }


    @Test
    fun `extract first model and close`() {
        extractAndAssertModel { realizer, cs ->
            val model = cs.extractFirstModelAndClose(realizer, colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            model
        }
    }


    @Test
    fun `extract all models from empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { realizer, cs ->
            val models = cs.extractAllModels(realizer, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract all models`() {
        extractAndAssertModels { realizer, cs ->
            val models = cs.extractAllModels(realizer, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract all models and close from empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { r, cs ->
            val models = cs.extractAllModelsAndClose(r, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract all models and close`() {
        extractAndAssertModels { r, cs ->
            val models = cs.extractAllModelsAndClose(r, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models with empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { realizer, cs ->
            val models = cs.extractAllModels(realizer, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models with source not having enough rows`() {
        extractAndAssertModels(
                maxModelsToExtract = 6, numberOfRowsInSource = 4) { realizer, cs ->

            val models = cs.extractAllModels(realizer, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models with more rows available in source`() {
        extractAndAssertModels(
                maxModelsToExtract = 4, numberOfRowsInSource = 6) { realizer, cs ->

            val models = cs.extractAllModels(realizer, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models and close with empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { r, cs ->
            val models = cs.extractAllModelsAndClose(r, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models and close with source not having enough rows`() {
        extractAndAssertModels(maxModelsToExtract = 6, numberOfRowsInSource = 4) { r, cs ->
            val models = cs.extractAllModelsAndClose(r, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models and close with more rows available in source`() {
        extractAndAssertModels(maxModelsToExtract = 4, numberOfRowsInSource = 6) { r, cs ->
            val models = cs.extractAllModelsAndClose(r, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    private fun extractAndAssertModelFromEmptySource(
            op: (RealizerForTest, CS<Source>) -> ModelWithCount?) {
        
        clearTestTable()
        val builtModel = op.invoke(RealizerForTest(), compileDistinctSelectQueryOrderedByInt())
        assertThat(builtModel).isNull()
    }


    private fun extractAndAssertModelsFromEmptySource(
            op: (RealizerForTest, CS<Source>) -> List<ModelWithCount>) {

        clearTestTable()
        val builtModels = op.invoke(RealizerForTest(), compileDistinctSelectQueryOrderedByInt())
        assertThat(builtModels).isEmpty()
    }


    private fun extractAndAssertModel(op: (table: RealizerForTest, CS<Source>) -> ModelWithCount?) {
        // Build source
        val seed = 5
        clearTestTable()
        insertRow(seed)
        insertRow(seed + 1)
        insertRow(seed)
        insertRow(seed + 2)
        insertRow(seed)

        // Extract & verify model
        val builtModel = op.invoke(RealizerForTest(), compileDistinctSelectQueryOrderedByInt())!!
        assertThat(builtModel.int).isEqualTo(seed)
        assertThat(builtModel.string).isEqualTo("test $seed")
        assertThat(builtModel.occurrenceCount).isEqualTo(3)
    }


    private fun extractAndAssertModels(op: (RealizerForTest, CS<Source>) -> List<ModelWithCount>) {
        // Build source
        val seed = 5
        clearTestTable()
        insertRow(seed)
        insertRow(seed + 1)
        insertRow(seed + 2)
        insertRow(seed + 1)
        insertRow(seed + 2)
        insertRow(seed)

        // Extract & verify model
        val builtModels = op.invoke(RealizerForTest(), compileDistinctSelectQueryOrderedByInt())
        assertThat(builtModels.size).isEqualTo(3)
        builtModels.forEachIndexed { index, builtModel ->
            val valueForThisIteration = seed + index
            assertThat(builtModel.int).isEqualTo(valueForThisIteration)
            assertThat(builtModel.string).isEqualTo("test $valueForThisIteration")
            assertThat(builtModel.occurrenceCount).isEqualTo(2)
        }
    }


    private fun extractAndAssertModels(
            maxModelsToExtract: Int, numberOfRowsInSource: Int,
            op: (RealizerForTest, CS<Source>) -> List<ModelWithCount>) {

        // Build source
        val seed = 5
        clearTestTable()
        val expectedNumberOfUniqueModelsExtracted = Math.min(
                maxModelsToExtract, numberOfRowsInSource
        )

        (0..expectedNumberOfUniqueModelsExtracted - 1).forEach {
            insertRow(seed + it)
            insertRow(seed + it)
        }

        // Extract & verify model
        val builtModels = op.invoke(RealizerForTest(), compileDistinctSelectQueryOrderedByInt())
        assertThat(builtModels.size).isEqualTo(expectedNumberOfUniqueModelsExtracted)
        builtModels.forEachIndexed { index, builtModel ->
            val valueForThisIteration = seed + index
            assertThat(builtModel.int).isEqualTo(valueForThisIteration)
            assertThat(builtModel.string).isEqualTo("test $valueForThisIteration")
            assertThat(builtModel.occurrenceCount).isEqualTo(2)
        }
    }


    private fun compileDistinctSelectQueryOrderedByInt(): CS<Source> {
        // Select column names with & without alias to make sure both
        // cases are handled correctly
        return engine.compileQuery(
                listOf(TABLE_NAME),
                "SELECT " +
                        "$INT_COL_NAME, $STRING_COL_NAME AS $STRING_COL_NAME_ALIAS, " +
                        "$OCCURRENCE_COUNT_COL_NAME AS $OCCURRENCE_COUNT_COL_NAME_ALIAS " +
                        "FROM $TABLE_NAME " +
                        "GROUP BY $STRING_COL_NAME_ALIAS " +
                        "ORDER BY $INT_COL_NAME"
        )
    }



    private class ModelWithCount(val int: Int, val string: String, val occurrenceCount: Long)



    private class ReadOnlyColumnForTest<out C>(
            override val name: String, private val propertyExtractor: (DataProducer) -> C) :
            ReadOnlyColumn<C> {

        override fun computePropertyFrom(dataProducer: DataProducer): C {
            return propertyExtractor.invoke(dataProducer)
        }
    }



    private class RealizerForTest : Realizer<ModelWithCount> {

        val int = ReadOnlyColumnForTest(INT_COL_NAME, { it.getInt() })
        val string = TableForTest().stringCol
        val occurrenceCount = ReadOnlyColumnForTest(OCCURRENCE_COUNT_COL_NAME, { it.getLong() })

        override fun realize(value: Realizer.Value): ModelWithCount {
            return ModelWithCount(value of int, value of string, value of occurrenceCount)
        }
    }



    companion object {
        private const val STRING_COL_NAME_ALIAS = "mnc_string_col"
        private const val OCCURRENCE_COUNT_COL_NAME = "count(string)"
        private const val OCCURRENCE_COUNT_COL_NAME_ALIAS = "count"

        private val colNameExtractor: ((ReadOnlyColumn<*>) -> String) = {
            when (it.name) {
                INT_COL_NAME -> INT_COL_NAME
                STRING_COL_NAME -> STRING_COL_NAME_ALIAS
                OCCURRENCE_COUNT_COL_NAME -> OCCURRENCE_COUNT_COL_NAME_ALIAS
                else -> throw IllegalArgumentException("Unrecognized col name: ${it.name}")
            }
        }
    }
}