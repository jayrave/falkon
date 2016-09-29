package com.jayrave.falkon.sample_android.models

import java.util.*

/**
 * The models NEED NOT extend any base class or have any annotations! The models are yours!
 * Both mutable & immutable models are supported, but I lean heavily towards immutability :)
 *
 * Since Falkon only does the O & M part of a traditional ORM i.e., Falkon is just a
 * object-mapping library & it doesn't map relationships, the structure of the models
 * end up mimicking that of their respective tables
 */
data class Group(
        val id: UUID,
        val name: String,
        val photoUrl: String?
)