package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.create.*
import org.junit.Test

class SqliteCreateTableSqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    /**
     * Sqlite is a little different from other db when it comes to PRIMARY KEY! It allows
     * `null` as a valid value in both simple & composite primary keys as documented at
     * https://www.sqlite.org/lang_createtable.html
     *
     * This removes the need for the following tests:
     *  - TestSimplePrimaryKey.`simple primary key cannot be null`
     *  - TestCompositePrimaryKey.`composite primary key does not allow any attributes to be null`
     *
     * Sqlite does not impose any length restrictions on data types like TEXT as documented at
     * https://www.sqlite.org/datatype3.html
     *
     * This removes the need for the following tests:
     *  - TestMaxSize.`max sized column does not allow values that are too big`
     */

    private val createTableSqlBuilder = SqliteCreateTableSqlBuilder()

    @Test
    fun `simple primary key does not allow duplicates`() {
        TestSimplePrimaryKey(createTableSqlBuilder, db).`simple primary key does not allow duplicates`()
    }

    @Test
    fun `composite primary key does not allow duplicates`() {
        TestCompositePrimaryKey(createTableSqlBuilder, db).`composite primary key does not allow duplicates`()
    }

    @Test
    fun `non-null column does not allow null values`() {
        TestNonNullability(createTableSqlBuilder, db).`non-null column does not allow null values`()
    }

    @Test
    fun `auto incrementing column increments value by at least 1 if not explicitly inserted`() {
        TestAutoIncrement(createTableSqlBuilder, db).`auto incrementing column increments value by at least 1 if not explicitly inserted`()
    }

    @Test
    fun `column with uniqueness constraint does not allow duplicates`() {
        TestSingleColumnUniquenessConstraint(createTableSqlBuilder, db).`column with uniqueness constraint does not allow duplicates`()
    }

    @Test
    fun `multi column uniqueness constraint does not allow duplicates`() {
        TestMultiColumnUniquenessConstraint(createTableSqlBuilder, db).`multi column uniqueness constraint does not allow duplicates`()
    }

    @Test
    fun `foreign key constraint violating operation isn't allowed`() {
        TestForeignKeyConstraint(createTableSqlBuilder, db).`foreign key constraint violating operation isn't allowed`()
    }
}