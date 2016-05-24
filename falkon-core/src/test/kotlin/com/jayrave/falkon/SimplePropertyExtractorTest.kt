package com.jayrave.falkon

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SimplePropertyExtractorTest {

    @Test
    fun testNonNullValueExtraction() {
        val nonNullIntExtractor = SimplePropertyExtractor(ModelForTest::nonNullInt)
        val nullableIntExtractor = SimplePropertyExtractor(ModelForTest::nullableInt)
        val nonNullStringExtractor = SimplePropertyExtractor(ModelForTest::nonNullString)
        val nullableStringExtractor = SimplePropertyExtractor(ModelForTest::nullableString)

        val modelForTest = ModelForTest(1, 1, "test non-null string", "test nullable string")

        assertThat(nonNullIntExtractor.extract(modelForTest)).isSameAs(modelForTest.nonNullInt)
        assertThat(nullableIntExtractor.extract(modelForTest)).isSameAs(modelForTest.nullableInt)
        assertThat(nonNullStringExtractor.extract(modelForTest)).isSameAs(modelForTest.nonNullString)
        assertThat(nullableStringExtractor.extract(modelForTest)).isSameAs(modelForTest.nullableString)
    }


    @Test
    fun testNullValueExtraction() {
        val nullableIntExtractor = SimplePropertyExtractor(ModelForTest::nullableInt)
        val nullableStringExtractor = SimplePropertyExtractor(ModelForTest::nullableString)

        val modelForTest = ModelForTest(1, null, "test", null)

        assertThat(nullableIntExtractor.extract(modelForTest)).isNull()
        assertThat(nullableStringExtractor.extract(modelForTest)).isNull()
    }


    private class ModelForTest(
            val nonNullInt: Int, val nullableInt: Int?,
            val nonNullString: String, val nullableString: String?
    )
}