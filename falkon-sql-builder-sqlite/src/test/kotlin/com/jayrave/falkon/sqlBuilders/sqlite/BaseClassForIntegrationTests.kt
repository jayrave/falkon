package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.DbForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.sqlite.SQLiteDataSource
import java.io.File
import java.nio.file.Paths
import javax.sql.DataSource

/**
 * Before every test, a clean database is setup
 */
abstract class BaseClassForIntegrationTests {

    private lateinit var dbFilePath: String
    protected lateinit var db: DbForTest

    @Before
    fun setUp() {
        dbFilePath = Paths.get("test.db").toAbsolutePath().toString()

        // https://github.com/xerial/sqlite-jdbc. Enforce foreign key constraints.
        // By default it is turned off as documented at @ https://www.sqlite.org/foreignkeys.html
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:$dbFilePath"
        dataSource.setEnforceForeinKeys(true)

        db = object : DbForTest {
            override val intDataType: String = "INTEGER"
            override val stringDataType: String = "TEXT"
            override val dataSource: DataSource = dataSource
        }
    }


    @After
    fun tearDown() {
        // This makes sure that we have a clean slate for every test
        val dbFile = File(dbFilePath)
        if (dbFile.exists()) {
            assertThat(dbFile.delete ()).isTrue()
        }
    }
}