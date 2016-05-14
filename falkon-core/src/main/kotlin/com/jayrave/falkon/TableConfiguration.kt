package com.jayrave.falkon

interface TableConfiguration<in T: Table<*, *>> {
    val typesHandler: TypesHandler
    fun <R> getConverter(clazz: Class<out R>): Converter<R>
}