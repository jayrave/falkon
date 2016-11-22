package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.test.*
import org.junit.Test

class SqlWithBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `compile sql with all types of bound args`() {
        TestCompileSqlWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile insert with all types of bound args`() {
        TestCompileInsertWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile update with all types of bound args`() {
        TestCompileUpdateWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile delete with all types of bound args`() {
        TestCompileDeleteWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }


    @Test
    fun `compile query with all types of bound args`() {
        TestCompileQueryWithAllTypesOfBoundArgs(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        ).`perform test`()
    }
}