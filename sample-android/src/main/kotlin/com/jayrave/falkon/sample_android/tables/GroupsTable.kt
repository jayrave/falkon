package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.mapper.BaseEnhancedTable
import com.jayrave.falkon.mapper.EnhancedColumn
import com.jayrave.falkon.mapper.TableConfiguration
import com.jayrave.falkon.mapper.Value
import com.jayrave.falkon.sample_android.ARG_PLACEHOLDER
import com.jayrave.falkon.sample_android.Group
import com.jayrave.falkon.sample_android.SqlBuilders
import java.util.*

/**
 * For a table implementation that shows off the capabilities of `Falkon`, check out [UsersTable].
 * This implementation is pretty bare bones
 */
class GroupsTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<Group, UUID, Dao<Group, UUID>>(
                "groups", configuration, sqlBuilders.createTableSqlBuilder) {

    val id = col(Group::id)
    val groupName = col(Group::name, "name")
    val photoUrl = col(Group::photoUrl)

    override val idColumn: EnhancedColumn<Group, UUID> = id
    override val dao: Dao<Group, UUID> = DaoImpl(
            this, ARG_PLACEHOLDER,
            sqlBuilders.insertSqlBuilder, sqlBuilders.updateSqlBuilder,
            sqlBuilders.deleteSqlBuilder, sqlBuilders.querySqlBuilder
    )


    override fun create(value: Value<Group>): Group {
        return Group(value of id, value of groupName, value of photoUrl)
    }
}