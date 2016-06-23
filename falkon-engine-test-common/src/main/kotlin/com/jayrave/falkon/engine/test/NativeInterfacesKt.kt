package com.jayrave.falkon.engine.test

import org.assertj.core.api.Assertions.assertThat

fun NativeQueryExecutor.getCount(tableName: String): Int {
    val source = execute("SELECT COUNT(*) AS count FROM $tableName")

    assertThat(source.moveToFirst()).isTrue()
    val count = source.getInt(source.getColumnIndex("count"))
    assertThat(source.moveToNext()).isFalse()

    return count
}