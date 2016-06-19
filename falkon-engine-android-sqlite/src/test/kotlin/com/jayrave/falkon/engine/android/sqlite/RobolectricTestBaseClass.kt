package com.jayrave.falkon.engine.android.sqlite

import android.os.Build
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
@RunWith(RobolectricGradleTestRunner::class)
abstract class RobolectricTestBaseClass