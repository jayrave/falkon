package com.jayrave.falkon.mapper

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

        assertThat(nonNullIntExtractor.extractFrom(modelForTest)).isSameAs(modelForTest.nonNullInt)
        assertThat(nullableIntExtractor.extractFrom(modelForTest)).isSameAs(
                modelForTest.nullableInt
        )

        assertThat(nonNullStringExtractor.extractFrom(modelForTest)).isSameAs(
                modelForTest.nonNullString
        )

        assertThat(nullableStringExtractor.extractFrom(modelForTest)).isSameAs(
                modelForTest.nullableString
        )
    }


    @Test
    fun testNullValueExtraction() {
        val nullableIntExtractor = SimplePropertyExtractor(ModelForTest::nullableInt)
        val nullableStringExtractor = SimplePropertyExtractor(ModelForTest::nullableString)

        val modelForTest = ModelForTest(1, null, "test", null)

        assertThat(nullableIntExtractor.extractFrom(modelForTest)).isNull()
        assertThat(nullableStringExtractor.extractFrom(modelForTest)).isNull()
    }


    private class ModelForTest(
            val nonNullInt: Int, val nullableInt: Int?,
            val nonNullString: String, val nullableString: String?
    )
}