package com.jayrave.falkon.engine

import com.jayrave.falkon.lib.MapBackedSink
import org.assertj.core.api.Assertions.*
import org.junit.Test

class SinkKtTest {

    @Test
    fun testIsEmpty() {
        val sink = MapBackedSink()
        assertThat(sink.isEmpty()).isTrue()
        sink.put("test", 5)
        assertThat(sink.isEmpty()).isFalse()
    }

    @Test
    fun testIsNotEmpty() {
        val sink = MapBackedSink()
        assertThat(sink.isNotEmpty()).isFalse()
        sink.put("test", 5)
        assertThat(sink.isNotEmpty()).isTrue()
    }
}