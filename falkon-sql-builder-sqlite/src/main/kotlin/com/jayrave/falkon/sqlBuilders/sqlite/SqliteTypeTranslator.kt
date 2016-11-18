package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator

class SqliteTypeTranslator : TypeTranslator {

    override fun translate(type: Type): String {
        return when (type) {
            Type.SHORT -> "INTEGER"
            Type.INT -> "INTEGER"
            Type.LONG -> "INTEGER"
            Type.FLOAT -> "REAL"
            Type.DOUBLE -> "REAL"
            Type.STRING -> "TEXT"
            Type.BLOB -> "BLOB"
        }
    }

    override fun translate(type: Type, maxLength: Int): String {
        return translate(type)
    }
}