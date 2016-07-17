package com.jayrave.falkon.engine

/**
 * To find the engine friendly versions of [Type]. Mostly used while building
 * CREATE TABLE SQL statements
 */
interface TypeTranslator {

    /**
     * @return the engine's particular type for handling a column of [type] whose size can
     * reach the max allowed
     */
    fun translate(type: Type): String

    /**
     * @return the engine's particular type for handling a column of [type] whose size caps
     * at [maxLength]
     */
    fun translate(type: Type, maxLength: Int): String
}