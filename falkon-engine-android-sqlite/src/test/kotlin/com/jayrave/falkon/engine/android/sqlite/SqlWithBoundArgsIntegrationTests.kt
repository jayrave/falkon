package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.*
import org.junit.Test

class SqlWithBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testCompileSqlWithAllTypesOfBoundArgs() {
        TestCompileSqlWithAllTypesOfBoundArgs.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileInsertWithAllTypesOfBoundArgs() {
        TestCompileInsertWithAllTypesOfBoundArgs.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileUpdateWithAllTypesOfBoundArgs() {
        TestCompileUpdateWithAllTypesOfBoundArgs.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileDeleteWithAllTypesOfBoundArgs() {
        TestCompileDeleteWithAllTypesOfBoundArgs.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileQueryWithAllTypesOfBoundArgs() {
        TestCompileQueryWithAllTypesOfBoundArgs.performTestOn(
                engine, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }
}