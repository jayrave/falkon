package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SqliteCreateTableSqlBuilderTest {

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


    @Test(expected = SQLSyntaxErrorException::class)
    fun `build with max size for columns throws`() {
        buildCreateTableStatement(TableInfoForTest(
                "test",
                listOf(ColumnInfoForTest("column_name_1", "NUMBER", 256, true, false, false)),
                emptyList(), emptyList()
        ))
    }


    @Test
    fun `build with both nullable & non null columns`() {
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
    fun `build with auto incremented primary key column`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, true, false, true)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        val actualSql = buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))

        val expectedSql = "CREATE TABLE test (" +
                "column_name_1 NUMBER PRIMARY KEY AUTOINCREMENT, " +
                "column_name_2 TEXT)"

        assertThat(actualSql).isEqualTo(expectedSql)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun `build with auto incremented non primary key column throws`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, false, false, true)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, false, false)
        buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun `build with auto incremented attribute of composite primary key throws, 1`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, true, false, true)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, true, false, false)
        buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun `build with auto incremented attribute of composite primary key throws, 2`() {
        val columnInfo2 = ColumnInfoForTest("column_name_1", "TEXT", null, true, false, false)
        val columnInfo1 = ColumnInfoForTest("column_name_2", "NUMBER", null, true, false, true)
        buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun `build with multiple auto incremented columns throws`() {
        val columnInfo2 = ColumnInfoForTest("column_name_1", "TEXT", null, true, false, true)
        val columnInfo1 = ColumnInfoForTest("column_name_2", "NUMBER", null, true, false, true)
        buildCreateTableStatement(TableInfoForTest(
                "test", listOf(columnInfo1, columnInfo2),
                emptyList(), emptyList()
        ))
    }


    @Test
    fun `build with single uniqueness constraint over one column`() {
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
    fun `build with single uniqueness constraint over multiple columns`() {
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
    fun `build with multiple uniqueness constraints`() {
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
    fun `build with single foreign key constraint`() {
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
    fun `build with multiple foreign key constraints`() {
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
    fun `build with lots of constraints`() {
        val columnInfo1 = ColumnInfoForTest("column_name_1", "NUMBER", null, true, false, false)
        val columnInfo2 = ColumnInfoForTest("column_name_2", "TEXT", null, false, true, false)
        val columnInfo3 = ColumnInfoForTest("column_name_3", "BLOB", null, true, true, false)
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
                "column_name_1 NUMBER, " +
                "column_name_2 TEXT NOT NULL, " +
                "column_name_3 BLOB NOT NULL, " +
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
        private fun buildCreateTableStatement(tableInfo: TableInfo): String {
            val sqlList = SqliteCreateTableSqlBuilder().build(tableInfo)
            assertThat(sqlList).hasSize(1)
            return sqlList.first()
        }
    }
}