package com.jayrave.falkon.engine.rxjava1

import java.util.concurrent.CountDownLatch

/**
 * Takes in a list of latches. Counts the first latch down completely & then moves on
 * to the next one & so on
 */
internal class LatchCountDownSerializer(private vararg val latches: CountDownLatch) {

    /**
     * If all latches are already count down completely, this is a no-op
     */
    fun countDown() {
        latches
                .firstOrNull { it.count > 0 }
                ?.countDown()
    }
}