package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo

data class SelectColumnInfoForTest(
        override val columnName: String,
        override val alias: String?
) : SelectColumnInfo