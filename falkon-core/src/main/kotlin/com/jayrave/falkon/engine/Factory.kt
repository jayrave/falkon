package com.jayrave.falkon.engine

interface Factory<S> {
    fun create(): S
}