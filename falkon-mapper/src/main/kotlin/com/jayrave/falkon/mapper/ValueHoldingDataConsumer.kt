package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypedNull

/**
 * A consumer that stores the most recent value it is asked to consume in [mostRecentConsumedValue]
 */
internal class ValueHoldingDataConsumer : NullHandlingDataConsumer() {

    lateinit var mostRecentConsumedValue: Any
        private set

    override fun put(short: Short) { mostRecentConsumedValue = short }
    override fun put(int: Int) { mostRecentConsumedValue = int }
    override fun put(long: Long) { mostRecentConsumedValue = long }
    override fun put(float: Float) { mostRecentConsumedValue = float }
    override fun put(double: Double) { mostRecentConsumedValue = double }
    override fun putNonNullString(string: String) { mostRecentConsumedValue = string }
    override fun putNonNullBlob(blob: ByteArray) { mostRecentConsumedValue = blob }
    override fun putNull(type: Type) { mostRecentConsumedValue = TypedNull(type) }
}