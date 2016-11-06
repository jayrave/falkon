package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.engine.TypeTranslator

class H2TypeTranslator : TypeTranslator {

    override fun translate(type: Type): String {
        return when (type) {
            Type.SHORT -> "SMALLINT"
            Type.INT -> "INTEGER"
            Type.LONG -> "BIGINT"
            Type.FLOAT -> "REAL"
            Type.DOUBLE -> "DOUBLE"
            Type.STRING -> "VARCHAR"
            Type.BLOB -> "BLOB"
        }
    }

    override fun translate(type: Type, maxLength: Int): String {
        return "${translate(type)}($maxLength)"
    }
}