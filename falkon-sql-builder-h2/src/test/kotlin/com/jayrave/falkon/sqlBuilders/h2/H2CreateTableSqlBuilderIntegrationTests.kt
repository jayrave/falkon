package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.create.*
import org.junit.Test

class H2CreateTableSqlBuilderIntegrationTests : BaseClassForTesting() {

    private val createTableSqlBuilder = H2CreateTableSqlBuilder()

    @Test
    fun `simple primary key does not allow duplicates`() {
        TestSimplePrimaryKey(createTableSqlBuilder, db).`simple primary key does not allow duplicates`()
    }

    @Test
    fun `simple primary key cannot be null`() {
        TestSimplePrimaryKey(createTableSqlBuilder, db).`simple primary key cannot be null`()
    }

    @Test
    fun `composite primary key does not allow duplicates`() {
        TestCompositePrimaryKey(createTableSqlBuilder, db).`composite primary key does not allow duplicates`()
    }

    @Test
    fun `composite primary key does not allow any attributes to be null`() {
        TestCompositePrimaryKey(createTableSqlBuilder, db).`composite primary key does not allow any attributes to be null`()
    }

    @Test
    fun `max sized column does not allow values that are too big`() {
        TestMaxSize(createTableSqlBuilder, db).`max sized column does not allow values that are too big`()
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