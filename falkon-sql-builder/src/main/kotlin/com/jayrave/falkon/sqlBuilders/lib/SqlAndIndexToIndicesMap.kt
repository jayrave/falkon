package com.jayrave.falkon.sqlBuilders.lib

data class SqlAndIndexToIndicesMap(

        /**
         * Built SQL statement
         */
        val sql: String,

        /**
         * To map from one index to multiple indices
         */
        val indexToIndicesMap: IndexToIndicesMap
)