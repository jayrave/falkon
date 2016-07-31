package com.jayrave.falkon.sample_android

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.Converter
import com.jayrave.falkon.mapper.DataConsumer
import com.jayrave.falkon.mapper.DataProducer
import java.util.*

/**
 * It is really easy to write custom converters. Data can be sent to [DataConsumer] &
 * acquired back from [DataProducer] in a type-safe manner
 */

class NullableUuidConverter : Converter<UUID?> {

    /**
     * This informs about how UUID will be stored in the database. We are gonna
     * stringify it & store in
     */
    override val dbType: Type = Type.STRING

    /**
     * From the passed in [dataProducer], you could get back whatever you stored
     * in the format you want. Since we stored UUID as a string, we can extract it
     * back like that & then create the UUID
     */
    override fun from(dataProducer: DataProducer): UUID? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> UUID.fromString(dataProducer.getString())
        }
    }

    /**
     * This is where the UUID has to be stringified to be consumed by the [dataConsumer]
     */
    override fun to(value: UUID?, dataConsumer: DataConsumer) {
        dataConsumer.put(value?.toString())
    }
}


class NullableDateConverter : Converter<Date?> {

    /**
     * This informs about how Date will be stored in the database. We are gonna
     * store the milliseconds since epoch
     */
    override val dbType: Type = Type.LONG

    /**
     * Since we stored Date as a long, we can extract it back like that & then create it
     */
    override fun from(dataProducer: DataProducer): Date? {
        return when (dataProducer.isNull()) {
            true -> null
            else -> Date(dataProducer.getLong()) // We know for sure that it can't be null now
        }
    }

    /**
     * This is where we get the milliseconds & let the [dataConsumer] consume it
     */
    override fun to(value: Date?, dataConsumer: DataConsumer) {
        dataConsumer.put(value?.time)
    }
}