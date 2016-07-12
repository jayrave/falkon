package com.jayrave.falkon.dao.where

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

interface Where {

    /**
     * contains the WHERE sections of an SQL statement
     */
    val whereSections: Iterable<WhereSection>

    /**
     * contains arguments for placeholders used in [whereSections]
     */
    val arguments: Iterable<Any>
}