package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.*
import org.junit.Test

class SqlWithBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testCompileSqlWithAllTypesOfBoundArgs() {
        TestCompileSqlWithAllTypesOfBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileInsertWithAllTypesOfBoundArgs() {
        TestCompileInsertWithAllTypesOfBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileUpdateWithAllTypesOfBoundArgs() {
        TestCompileUpdateWithAllTypesOfBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileDeleteWithAllTypesOfBoundArgs() {
        TestCompileDeleteWithAllTypesOfBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileQueryWithAllTypesOfBoundArgs() {
        TestCompileQueryWithAllTypesOfBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }
}