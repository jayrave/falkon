package com.jayrave.falkon.sqlBuilders.test.create

import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo

internal class TableInfoForTest(
        override val name: String,
        override val columnInfos: Iterable<ColumnInfo>,
        override val uniquenessConstraints: Iterable<Iterable<String>>,
        override val foreignKeyConstraints: Iterable<ForeignKeyConstraint>
) : TableInfo


internal class ColumnInfoForTest(
        override val name: String,
        override val dataType: String,
        override val maxSize: Int? = null,
        override val isId: Boolean = false,
        override val isNonNull: Boolean = false,
        override val autoIncrement: Boolean = false
) : ColumnInfo


internal class ForeignKeyConstraintForTest(
        override val columnName: String,
        override val foreignTableName: String,
        override val foreignColumnName: String
) : ForeignKeyConstraint