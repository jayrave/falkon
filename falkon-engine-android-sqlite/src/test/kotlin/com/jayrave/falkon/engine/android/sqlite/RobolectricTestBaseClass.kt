package com.jayrave.falkon.engine.android.sqlite

import android.os.Build
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(
        sdk = [Build.VERSION_CODES.P],
        packageName = "com.jayrave.falkon.engine.android.sqlite"
)
@RunWith(RobolectricTestRunner::class)
abstract class RobolectricTestBaseClass