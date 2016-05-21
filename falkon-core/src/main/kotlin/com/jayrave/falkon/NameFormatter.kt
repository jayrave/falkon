package com.jayrave.falkon

interface NameFormatter {
    fun getColumnNameFor(propertyName: String): String
}