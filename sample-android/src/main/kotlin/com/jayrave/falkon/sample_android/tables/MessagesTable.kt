package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.mapper.BaseEnhancedTable
import com.jayrave.falkon.mapper.EnhancedColumn
import com.jayrave.falkon.mapper.TableConfiguration
import com.jayrave.falkon.mapper.Value
import com.jayrave.falkon.sample_android.ARG_PLACEHOLDER
import com.jayrave.falkon.sample_android.models.Message
import com.jayrave.falkon.sample_android.SqlBuilders
import java.util.*

/**
 * For a table implementation that shows off the capabilities of `Falkon`, check out [UsersTable].
 * This implementation is pretty bare bones
 */
class MessagesTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<Message, UUID, Dao<Message, UUID>>(
                "messages", configuration, sqlBuilders.createTableSqlBuilder) {

    val id = col(Message::id)
    val content = col(Message::content)
    val createdAt = col(Message::createdAt)
    val sentAt = col(Message::sentAt)
    val receivedAt = col(Message::receivedAt)
    val fromUserId = col(Message::fromUserId)
    val toUserId = col(Message::toUserId)
    val groupId = col(Message::groupId)

    override val idColumn: EnhancedColumn<Message, UUID> = id
    override val dao: Dao<Message, UUID> = DaoImpl(
            this, ARG_PLACEHOLDER,
            sqlBuilders.insertSqlBuilder, sqlBuilders.updateSqlBuilder,
            sqlBuilders.deleteSqlBuilder, sqlBuilders.querySqlBuilder
    )


    override fun create(value: Value<Message>): Message {
        return Message(
                value of id, value of content, value of createdAt, value of sentAt,
                value of receivedAt, value of fromUserId, value of toUserId, value of groupId
        )
    }
}