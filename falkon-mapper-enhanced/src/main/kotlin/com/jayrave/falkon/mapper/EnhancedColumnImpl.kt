package com.jayrave.falkon.mapper

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator
import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo

class EnhancedColumnImpl<T : Any, C>(
        table: Table<T, *>, name: String, override val maxSize: Int?,
        override val isNonNull: Boolean, propertyExtractor: PropertyExtractor<T, C>,
        converter: Converter<C>, typeTranslator: TypeTranslator) :
        EnhancedColumn<T, C>, ColumnInfo {

    private val delegate = ColumnImpl(table, name, propertyExtractor, converter)
    override val dataType: String

    // ------------------------ Start of delegated properties & methods ----------------------------

    override val table: Table<T, *> get() = delegate.table
    override val name: String get() = delegate.name
    override val dbType: Type get() = delegate.dbType
    override fun extractPropertyFrom(t: T): C = delegate.extractPropertyFrom(t)
    override fun computeStorageFormOf(property: C): Any? = delegate.computeStorageFormOf(property)
    override fun putStorageFormIn(property: C, dataConsumer: DataConsumer) {
        delegate.putStorageFormIn(property, dataConsumer)
    }

    override fun computePropertyFrom(dataProducer: DataProducer): C = delegate.computePropertyFrom(
            dataProducer
    )

    // ------------------------- End of delegated properties & methods -----------------------------


    init {
        val dbType = delegate.dbType
        dataType = when (maxSize) {
            null -> typeTranslator.translate(dbType)
            else -> typeTranslator.translate(dbType, maxSize)
        }
    }
}