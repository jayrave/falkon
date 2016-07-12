package com.jayrave.falkon.dao.where

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

internal data class WhereImpl(
        override val whereSections: Iterable<WhereSection>,
        override val arguments: Iterable<Any>
) : Where