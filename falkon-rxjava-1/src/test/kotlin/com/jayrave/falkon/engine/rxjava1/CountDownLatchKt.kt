package com.jayrave.falkon.engine.rxjava1

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal fun CountDownLatch.awaitWithDefaultTimeout() {
    await(1000, TimeUnit.MILLISECONDS)
}