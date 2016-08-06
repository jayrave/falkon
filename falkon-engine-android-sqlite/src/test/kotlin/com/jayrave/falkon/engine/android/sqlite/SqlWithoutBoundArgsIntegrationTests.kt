package com.jayrave.falkon.engine.android.sqlite

import com.jayrave.falkon.engine.test.*
import org.junit.Test

class SqlWithoutBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testCompileSqlWithoutBoundArgs() {
        TestCompileSqlWithoutBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileInsertWithoutBoundArgs() {
        TestCompileInsertWithoutBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileUpdateWithoutBoundArgs() {
        TestCompileUpdateWithoutBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileDeleteWithoutBoundArgs() {
        TestCompileDeleteWithoutBoundArgs.performTestOn(
                engineCore, sqlExecutorUsingDataSource, queryExecutorUsingDataSource
        )
    }


    @Test
    fun testCompileQueryWithoutBoundArgs() {
        TestCompileQueryWithoutBoundArgs.performTestOn(engineCore, sqlExecutorUsingDataSource)
    }
}