package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import com.jayrave.falkon.engine.CompiledStatement as CS

class CompiledQueryExtnTestsForTable : BaseClassForCompiledQueryExtnTests() {

    @Test
    fun `extract first model from empty source returns null`() {
        extractAndAssertModelFromEmptySource { table, cs ->
            val model = cs.extractFirstModel(table, colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            model
        }
    }


    @Test
    fun `extract first model`() {
        extractAndAssertModel { table, cs ->
            val model = cs.extractFirstModel(table, colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            model
        }
    }


    @Test
    fun `extract first model and close from empty source returns null`() {
        extractAndAssertModelFromEmptySource { table, cs ->
            val model = cs.extractFirstModelAndClose(table, colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            model
        }
    }


    @Test
    fun `extract first model and close`() {
        extractAndAssertModel { table, cs ->
            val model = cs.extractFirstModelAndClose(table, colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            model
        }
    }


    @Test
    fun `extract all models from empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { table, cs ->
            val models = cs.extractAllModels(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract all models`() {
        extractAndAssertModels { table, cs ->
            val models = cs.extractAllModels(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract all models and close from empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { table, cs ->
            val models = cs.extractAllModelsAndClose(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract all models and close`() {
        extractAndAssertModels { table, cs ->
            val models = cs.extractAllModelsAndClose(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models with empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { table, cs ->
            val models = cs.extractAllModels(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models with source not having enough rows`() {
        extractAndAssertModels(maxModelsToExtract = 6, numberOfRowsInSource = 4) { table, cs ->
            val models = cs.extractAllModels(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models with more rows available in source`() {
        extractAndAssertModels(maxModelsToExtract = 4, numberOfRowsInSource = 6) { table, cs ->
            val models = cs.extractAllModels(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isFalse()
            cs.close()
            models
        }
    }


    @Test
    fun `extract models and close with empty source returns empty list`() {
        extractAndAssertModelsFromEmptySource { table, cs ->
            val models = cs.extractAllModelsAndClose(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models and close with source not having enough rows`() {
        extractAndAssertModels(
                maxModelsToExtract = 6, numberOfRowsInSource = 4) { table, cs ->

            val models = cs.extractAllModelsAndClose(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    @Test
    fun `extract models and close with more rows available in source`() {
        extractAndAssertModels(
                maxModelsToExtract = 4, numberOfRowsInSource = 6) { table, cs ->

            val models = cs.extractAllModelsAndClose(table, columnNameExtractor = colNameExtractor)
            assertThat(cs.isClosed).isTrue()
            models
        }
    }


    private fun extractAndAssertModelFromEmptySource(
            op: (TableForTest, CS<Source>) -> ModelForTest?) {

        clearTestTable()
        val builtModel = op.invoke(TableForTest(), compileSelectAllQuery())
        assertThat(builtModel).isNull()
    }


    private fun extractAndAssertModelsFromEmptySource(
            op: (TableForTest, CS<Source>) -> List<ModelForTest>) {

        clearTestTable()
        val builtModels = op.invoke(TableForTest(), compileSelectAllQuery())
        assertThat(builtModels).isEmpty()
    }


    private fun extractAndAssertModel(
            extractor: (table: TableForTest, CS<Source>) -> ModelForTest?) {

        // Build source
        val seed = 5
        clearTestTable()
        insertRow(seed)
        insertRow(seed + 1)
        insertRow(seed + 2)

        // Extract & verify model
        val builtModel = extractor.invoke(TableForTest(), compileSelectAllQuery())!!
        assertThat(builtModel.int).isEqualTo(seed)
        assertThat(builtModel.string).isEqualTo("test $seed")
    }


    private fun extractAndAssertModels(op: (TableForTest, CS<Source>) -> List<ModelForTest>) {
        // Build source
        val seed = 5
        clearTestTable()
        insertRow(seed)
        insertRow(seed + 1)
        insertRow(seed + 2)

        // Extract & verify model
        val builtModels = op.invoke(TableForTest(), compileSelectAllQuery())
        assertThat(builtModels.size).isEqualTo(3)
        builtModels.forEachIndexed { index, builtModel ->
            val valueForThisIteration = seed + index
            assertThat(builtModel.int).isEqualTo(valueForThisIteration)
            assertThat(builtModel.string).isEqualTo("test $valueForThisIteration")
        }
    }


    private fun extractAndAssertModels(
            maxModelsToExtract: Int, numberOfRowsInSource: Int,
            op: (TableForTest, CS<Source>) -> List<ModelForTest>) {

        // Build source
        val seed = 5
        clearTestTable()
        val expectedNumberOfModelsExtracted = Math.min(maxModelsToExtract, numberOfRowsInSource)
        (0..expectedNumberOfModelsExtracted - 1).forEach { insertRow(seed + it) }

        // Extract & verify model
        val builtModels = op.invoke(TableForTest(), compileSelectAllQuery())
        assertThat(builtModels.size).isEqualTo(expectedNumberOfModelsExtracted)
        builtModels.forEachIndexed { index, builtModel ->
            val valueForThisIteration = seed + index
            assertThat(builtModel.int).isEqualTo(valueForThisIteration)
            assertThat(builtModel.string).isEqualTo("test $valueForThisIteration")
        }
    }


    private fun compileSelectAllQuery(): CS<Source> {
        // Select column names with & without alias to make sure both
        // cases are handled correctly
        return engine.compileQuery(
                listOf(TABLE_NAME),
                "SELECT $INT_COL_NAME AS $INT_COL_NAME_ALIAS, $STRING_COL_NAME FROM $TABLE_NAME"
        )
    }



    companion object {
        private const val INT_COL_NAME_ALIAS = "alias_int"
        private val colNameExtractor: ((Column<ModelForTest, *>) -> String) = {
            when (it.name) {
                INT_COL_NAME -> INT_COL_NAME_ALIAS
                STRING_COL_NAME -> STRING_COL_NAME
                else -> throw IllegalArgumentException("Unrecognized col name: ${it.name}")
            }
        }
    }
}