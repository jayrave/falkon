package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo

data class SelectColumnInfoForTest(
        override val columnName: String,
        override val alias: String?
) : SelectColumnInfo


data class OrderInfoForTest(
        override val columnName: String,
        override val ascending: Boolean
): OrderInfo