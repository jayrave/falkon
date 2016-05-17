package com.jayrave.falkon

interface TableConfiguration {
    fun <R> getConverter(clazz: Class<out R>): Converter<R>
}