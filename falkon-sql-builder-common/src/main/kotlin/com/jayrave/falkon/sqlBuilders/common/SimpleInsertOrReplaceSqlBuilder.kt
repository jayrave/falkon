package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.iterables.IterablesBackedIterable
import java.sql.SQLSyntaxErrorException

object SimpleInsertOrReplaceSqlBuilder {

    /**
     * [phraseForInsertOrReplace] is used to replace the `INSERT` in `INSERT INTO ...`.
     * Columns are specified in the iteration order of [idColumns] & [nonIdColumns]
     */
    fun build(
            phraseForInsertOrReplace: String, tableName: String,
            idColumns: Iterable<String>, nonIdColumns: Iterable<String>):
            String {

        if (idColumns.size() == 0) {
            throw SQLSyntaxErrorException("ID columns can't be empty for insert or replace")
        }

        val allColumns = IterablesBackedIterable(listOf(idColumns, nonIdColumns))
        return SimpleInsertAndCousinsSqlBuilder.build(
                phraseForInsertOrReplace, tableName, allColumns
        )
    }


    private fun Iterable<*>.size(): Int {
        return when (this) {
            is Collection -> size
            else -> count()
        }
    }
}