package com.jayrave.falkon.sample_android

import android.util.Log

const val ARG_PLACEHOLDER: String = "?"
private const val LOG_TAG = "falkon"

fun logInfo(message: String) {
    Log.i(LOG_TAG, message)
}

fun logDebug(message: String) {
    Log.d(LOG_TAG, message)
}

fun logError(message: String) {
    Log.e(LOG_TAG, message)
}