package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.mapper.BaseEnhancedTable
import com.jayrave.falkon.mapper.EnhancedColumn
import com.jayrave.falkon.mapper.TableConfiguration
import com.jayrave.falkon.mapper.Value
import com.jayrave.falkon.sample_android.ARG_PLACEHOLDER
import com.jayrave.falkon.sample_android.models.GroupMember
import com.jayrave.falkon.sample_android.SqlBuilders
import java.util.*

/**
 * For a table implementation that shows off the capabilities of `Falkon`, check out [UsersTable].
 * This implementation is pretty bare bones
 */
class GroupMembersTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<GroupMember, UUID, Dao<GroupMember, UUID>>(
                "group_members", configuration, sqlBuilders.createTableSqlBuilder) {

    val id = col(GroupMember::id)
    val groupId = col(GroupMember::groupId)
    val userId = col(GroupMember::userId)
    val invitedAt = col(GroupMember::invitedAt)

    override val idColumn: EnhancedColumn<GroupMember, UUID> = id
    override val dao: Dao<GroupMember, UUID> = DaoImpl(
            this, ARG_PLACEHOLDER,
            sqlBuilders.insertSqlBuilder, sqlBuilders.updateSqlBuilder,
            sqlBuilders.deleteSqlBuilder, sqlBuilders.querySqlBuilder
    )


    override fun create(value: Value<GroupMember>): GroupMember {
        return GroupMember(value of id, value of groupId, value of userId, value of invitedAt)
    }
}