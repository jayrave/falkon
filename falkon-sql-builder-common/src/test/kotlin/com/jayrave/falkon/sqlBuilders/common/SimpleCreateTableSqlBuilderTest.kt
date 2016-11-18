package com.jayrave.falkon.sqlBuilders.common

import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleCreateTableSqlBuilderTest {

    @Test(expected = SQLSyntaxErrorException::class)
    fun `build throws for empty column info`() {
        buildCreateTableStatement(TableInfoForTest(
                "test", emptyList(), emptyList(), emptyList())
        )
    }


    @Test
    fun `build with single non id column info`() {
        val columnInfo = ColumnInfoForTest("column_name", "TEXT", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo), emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name TEXT)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `build with single id column info`() {
        val columnInfo = ColumnInfoForTest("column_name", "TEXT", null, true, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo), emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name TEXT, PRIMARY KEY (column_name))"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `build with multiple id column infos`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "TEXT", null, true, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, true, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2), emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 TEXT, column_name_2 TEXT, " +
                "PRIMARY KEY (column_name_1, column_name_2))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `build with multiple column infos with both id & non id columns`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, true, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3),
                emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, column_name_3 BLOB, " +
                "PRIMARY KEY (column_name_1))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun `build with columns with & without max size specified`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", 256, false, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", 128, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3),
                emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, " +
                "column_name_2 TEXT(256), " +
                "column_name_3 BLOB(128))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildWithBothNullableAndNonNullColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, true, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name_1 NUMBER, column_name_2 TEXT NOT NULL)"
        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildWithBothWithAndWithoutAutoIncrementingColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, true)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT $AUTO_INCREMENT_FOR_TESTING)"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSingleUniquenessConstraintWithOneColumn() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                listOf(listOf(columnInfo2.name)), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "UNIQUE (column_name_2))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSingleUniquenessConstraintWithMultipleColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3),
                listOf(listOf(columnInfo2.name, columnInfo3.name)), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, column_name_3 BLOB, " +
                "UNIQUE (column_name_2, column_name_3))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testMultipleUniquenessConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, false, false, false)
        val columnInfo4 = ColumnInfoForTest("column_name_4", "TEXT", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3, columnInfo4),
                listOf(
                        listOf(columnInfo2.name, columnInfo3.name),
                        listOf(columnInfo3.name, columnInfo4.name),
                        listOf(columnInfo4.name)
                ), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name_1 NUMBER, " +
                "column_name_2 TEXT, column_name_3 BLOB, column_name_4 TEXT, " +
                "UNIQUE (column_name_2, column_name_3), " +
                "UNIQUE (column_name_3, column_name_4), " +
                "UNIQUE (column_name_4))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testSingleForeignKeyConstraint() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val foreignKeyConstraint = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table", "foreign_column_name"
        )

        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), listOf(foreignKeyConstraint)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table(foreign_column_name))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testMultipleForeignKeyConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val foreignKeyConstraint1 = ForeignKeyConstraintForTest(
                columnInfo1.name, "foreign_table_1", "foreign_column_name_1"
        )

        val foreignKeyConstraint2 = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table_2", "foreign_column_name_2"
        )

        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), listOf(foreignKeyConstraint1, foreignKeyConstraint2)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "FOREIGN KEY (column_name_1) REFERENCES foreign_table_1(foreign_column_name_1), " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table_2(foreign_column_name_2))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test
    fun testBuildWithAllConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", 64, true, false, true)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, true, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", 1024, true, true, true)
        val columnInfo4 = ColumnInfoForTest("column_name_4", "TEXT", null, false, false, false)

        val foreignKeyConstraint1 = ForeignKeyConstraintForTest(
                columnInfo1.name, "foreign_table_1", "foreign_column_name_1"
        )

        val foreignKeyConstraint2 = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table_2", "foreign_column_name_2"
        )

        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3, columnInfo4),
                listOf(
                        listOf(columnInfo2.name, columnInfo3.name),
                        listOf(columnInfo3.name, columnInfo4.name),
                        listOf(columnInfo4.name)
                ), listOf(foreignKeyConstraint1, foreignKeyConstraint2)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER(64) $AUTO_INCREMENT_FOR_TESTING, " +
                "column_name_2 TEXT NOT NULL, " +
                "column_name_3 BLOB(1024) NOT NULL $AUTO_INCREMENT_FOR_TESTING, " +
                "column_name_4 TEXT, " +
                "PRIMARY KEY (column_name_1, column_name_3), " +
                "UNIQUE (column_name_2, column_name_3), " +
                "UNIQUE (column_name_3, column_name_4), " +
                "UNIQUE (column_name_4), " +
                "FOREIGN KEY (column_name_1) REFERENCES foreign_table_1(foreign_column_name_1), " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table_2(foreign_column_name_2))"

        assertThat(actualSql).isEqualTo(expectedSql)
    }



    private class ColumnInfoForTest(
            override val name: String, override val dataType: String,
            override val maxSize: Int?, override val isId: Boolean,
            override val isNonNull: Boolean, override val autoIncrement: Boolean) : ColumnInfo



    private class ForeignKeyConstraintForTest(
            override val columnName: String, override val foreignTableName: String,
            override val foreignColumnName: String) : ForeignKeyConstraint



    private class TableInfoForTest(
            override val name: String, override val columnInfos: Iterable<ColumnInfo>,
            override val uniquenessConstraints: Iterable<Iterable<String>>,
            override val foreignKeyConstraints: Iterable<ForeignKeyConstraint>) : TableInfo


    companion object {
        private const val AUTO_INCREMENT_FOR_TESTING = "AUTO_INC"
        private fun buildCreateTableStatement(tableInfo: TableInfo): String {
            return SimpleCreateTableSqlBuilder.build(tableInfo, AUTO_INCREMENT_FOR_TESTING)
        }
    }
}