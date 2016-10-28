package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SimpleCreateTableSqlBuilderTest {

    private val builder = SimpleCreateTableSqlBuilder(DialectForTesting)

    @Test(expected = SQLSyntaxErrorException::class)
    fun testBuildThrowsForEmptyColumnInfos() {
        builder.build(TableInfoForTest(
                "test", emptyList(), "column_name", emptyList(), emptyList()
        ))
    }


    @Test
    fun testBuildWithSingleColumnInfo() {
        val columnInfo = ColumnInfoForTest("column_name", "TEXT", 256, false, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo), columnInfo.name, emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name TEXT(256), PRIMARY KEY (column_name))"
        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testBuildWithMultipleColumnInfo() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", 256, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", 128, false, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3),
                columnInfo1.name, emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT(256), column_name_3 BLOB(128), " +
                "PRIMARY KEY (column_name_1))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testBuildWithBothNullableAndNonNullColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, true, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                columnInfo1.name, emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT NOT NULL, " +
                "PRIMARY KEY (column_name_1))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testBuildWithBothWithAndWithoutAutoIncrementingColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, true)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                columnInfo1.name, emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, " +
                "column_name_2 TEXT ${DialectForTesting.autoIncrementExpression}, " +
                "PRIMARY KEY (column_name_1))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testSingleUniquenessConstraintWithOneColumn() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                columnInfo1.name, listOf(listOf(columnInfo2.name)), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "PRIMARY KEY (column_name_1), " +
                "UNIQUE (column_name_2))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testSingleUniquenessConstraintWithMultipleColumns() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, false, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3), columnInfo1.name,
                listOf(listOf(columnInfo2.name, columnInfo3.name)), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, column_name_3 BLOB, " +
                "PRIMARY KEY (column_name_1), " +
                "UNIQUE (column_name_2, column_name_3))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testMultipleUniquenessConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, false, false)
        val columnInfo4 = ColumnInfoForTest("column_name_4", "TEXT", null, false, false)
        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3, columnInfo4),
                columnInfo1.name,
                listOf(
                        listOf(columnInfo2.name, columnInfo3.name),
                        listOf(columnInfo3.name, columnInfo4.name),
                        listOf(columnInfo4.name)
                ), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (column_name_1 NUMBER, " +
                "column_name_2 TEXT, column_name_3 BLOB, column_name_4 TEXT, " +
                "PRIMARY KEY (column_name_1), " +
                "UNIQUE (column_name_2, column_name_3), " +
                "UNIQUE (column_name_3, column_name_4), " +
                "UNIQUE (column_name_4))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testSingleForeignKeyConstraint() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false)
        val foreignKeyConstraint = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table", "foreign_column_name"
        )

        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2), columnInfo1.name,
                emptyList(), listOf(foreignKeyConstraint)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "PRIMARY KEY (column_name_1), " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table(foreign_column_name))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testMultipleForeignKeyConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false)
        val foreignKeyConstraint1 = ForeignKeyConstraintForTest(
                columnInfo1.name, "foreign_table_1", "foreign_column_name_1"
        )

        val foreignKeyConstraint2 = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table_2", "foreign_column_name_2"
        )

        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2), columnInfo1.name,
                emptyList(), listOf(foreignKeyConstraint1, foreignKeyConstraint2)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER, column_name_2 TEXT, " +
                "PRIMARY KEY (column_name_1), " +
                "FOREIGN KEY (column_name_1) REFERENCES foreign_table_1(foreign_column_name_1), " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table_2(foreign_column_name_2))"

        assertThat(actualSql).containsOnly(expectedSql)
    }


    @Test
    fun testBuildWithAllConstraints() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", 64, false, true)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, true, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", 1024, true, true)
        val columnInfo4 = ColumnInfoForTest("column_name_4", "TEXT", null, false, false)

        val foreignKeyConstraint1 = ForeignKeyConstraintForTest(
                columnInfo1.name, "foreign_table_1", "foreign_column_name_1"
        )

        val foreignKeyConstraint2 = ForeignKeyConstraintForTest(
                columnInfo2.name, "foreign_table_2", "foreign_column_name_2"
        )

        val actualSql = builder.build(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2, columnInfo3, columnInfo4),
                columnInfo1.name,
                listOf(
                        listOf(columnInfo2.name, columnInfo3.name),
                        listOf(columnInfo3.name, columnInfo4.name),
                        listOf(columnInfo4.name)
                ), listOf(foreignKeyConstraint1, foreignKeyConstraint2)
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER(64) ${DialectForTesting.autoIncrementExpression}, " +
                "column_name_2 TEXT NOT NULL, " +
                "column_name_3 BLOB(1024) NOT NULL ${DialectForTesting.autoIncrementExpression}, " +
                "column_name_4 TEXT, " +
                "PRIMARY KEY (column_name_1), " +
                "UNIQUE (column_name_2, column_name_3), " +
                "UNIQUE (column_name_3, column_name_4), " +
                "UNIQUE (column_name_4), " +
                "FOREIGN KEY (column_name_1) REFERENCES foreign_table_1(foreign_column_name_1), " +
                "FOREIGN KEY (column_name_2) REFERENCES foreign_table_2(foreign_column_name_2))"

        assertThat(actualSql).containsOnly(expectedSql)
    }



    private class ColumnInfoForTest(
            override val name: String, override val dataType: String,
            override val maxSize: Int?, override val isNonNull: Boolean,
            override val autoIncrement: Boolean) : ColumnInfo



    private class ForeignKeyConstraintForTest(
            override val columnName: String, override val foreignTableName: String,
            override val foreignColumnName: String) : ForeignKeyConstraint



    private class TableInfoForTest(
            override val name: String, override val columnInfos: Iterable<ColumnInfo>,
            override val primaryKeyConstraint: String,
            override val uniquenessConstraints: Iterable<Iterable<String>>,
            override val foreignKeyConstraints: Iterable<ForeignKeyConstraint>) : TableInfo



    object DialectForTesting : Dialect {
        override val autoIncrementExpression: String = "AUTO_INCREMENT_FOR_TESTING"
    }
}