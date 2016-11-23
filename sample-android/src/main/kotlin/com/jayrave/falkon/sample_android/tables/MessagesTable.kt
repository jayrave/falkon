package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.*
import com.jayrave.falkon.mapper.lib.SimpleIdExtractFromHelper
import com.jayrave.falkon.sample_android.models.Message
import com.jayrave.falkon.sample_android.SqlBuilders
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Table]s inform Falkon about how a model maps to a table. [BaseEnhancedTable] provides
 * a lot of defaults & is a good class to extend for you table mappings
 *
 * This class explains some of the features. For more, check out [UsersTable]
 */
class MessagesTable(
        configuration: TableConfiguration, sqlBuilders: SqlBuilders, usersTable: UsersTable) :
        BaseEnhancedTable<Message, UUID, Dao<Message, UUID>>(
                "messages", configuration, sqlBuilders.createTableSqlBuilder) {

    val id = col(Message::id, isId = true)
    val content = col(Message::content)
    val sentAt = col(Message::sentAt)

    /**
     * If special handling is required for just one field, a custom converter can be
     * directly assigned to this column
     */
    val receivedAt = col(Message::receivedAt, converter = StringDateConverter())

    /**
     * Foreign references can be established using [foreignCol]
     */
    val fromUserId = foreignCol(Message::fromUserId, foreignColumn = usersTable.id)
    val toUserId = foreignCol(Message::toUserId, foreignColumn = usersTable.id)

    override val dao: Dao<Message, UUID> = DaoImpl(
            this, sqlBuilders.insertSqlBuilder, sqlBuilders.updateSqlBuilder,
            sqlBuilders.deleteSqlBuilder, sqlBuilders.insertOrReplaceSqlBuilder,
            sqlBuilders.querySqlBuilder
    )


    private val extractFromHelper = SimpleIdExtractFromHelper(id)
    override fun <C> extractFrom(id: UUID, column: Column<Message, C>): C {
        return extractFromHelper.extractFrom(id, column)
    }


    override fun create(value: Value<Message>): Message {
        return Message(
                value of id, value of content, value of sentAt, value of receivedAt,
                value of fromUserId, value of toUserId
        )
    }


    companion object {

        /**
         * To stringify [Date] & save, restore from the database
         */
        private class StringDateConverter : Converter<Date> {

            override val dbType: Type = Type.STRING
            private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            override fun from(dataProducer: DataProducer): Date {
                return dateFormat.parse(dataProducer.getString())
            }

            override fun to(value: Date, dataConsumer: DataConsumer) {
                dataConsumer.put(dateFormat.format(value))
            }
        }
    }
}