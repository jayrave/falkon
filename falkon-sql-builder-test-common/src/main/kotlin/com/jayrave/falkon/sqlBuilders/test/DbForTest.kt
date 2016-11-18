package com.jayrave.falkon.sqlBuilders.test

import javax.sql.DataSource

interface DbForTest {
    val intDataType: String
    val stringDataType: String
    val dataSource: DataSource
}
