package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.engine.Type
import com.jayrave.falkon.mapper.*
import com.jayrave.falkon.sample_android.SqlBuilders
import com.jayrave.falkon.sample_android.User
import com.jayrave.falkon.sample_android.daos.UsersDao
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Table]s inform Falkon about how a model maps to a table. [BaseEnhancedTable] provides
 * a lot of defaults & is a good class to extend for you table mappings
 */
class UsersTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<User, UUID, UsersDao>(
                "users", configuration, sqlBuilders.createTableSqlBuilder) {

    /**
     * By default, the property name is snake-cased & used as the database column name.
     * For example `Model::exampleColumn1` will use `example_column_1` as its column
     * name. You can change this by passing in a custom name or a different
     * [NameFormatter] to [TableConfiguration]
     */
    val id = col(User::id)

    /**
     * You can set max-size which will be used in CREATE TABLE statement that this table
     * builds (but SQLite doesn't honor that!! this would come useful in some other db)
     */
    val firstName = col(User::firstName, maxSize = 255)
    val lastName = col(User::lastName, maxSize = 255)

    /**
     * isNonNull adds NOT NULL to the column definition in the built CREATE TABLE statement
     */
    val emailId = col(User::emailId, isNonNull = false)

    val age = col(User::age)
    val address = col(User::address)

    /**
     * isNonNull adds UNIQUE to the column definition in the built CREATE TABLE statement.
     * If multiple columns should be involved in the UNIQUE constraint, take a look at
     * [BaseEnhancedTable.addUniquenessConstraint]
     */
    val photoUrl = col(User::photoUrl, isUnique = true)

    /**
     * If special handling is required for just one field, a custom converted can be
     * directly assigned to this column
     */
    val createdAt = col(User::createdAt, converter = StringDateConverter())

    /**
     * By default `#col` knows how to extract the property from a passed in instance.
     * It is a simple KProperty#get call, but instead of that you may wanna do something
     * custom. For example, you may wanna pass back the current system time instead of
     * what `User::lastSeenAt` holds. For this case, just use a custom property extractor
     */
    val lastSeenAt = col(User::lastSeenAt, propertyExtractor = currentDatePropExtractor)

    /**
     * This points to the primary key
     */
    override val idColumn: EnhancedColumn<User, UUID> get() = id

    /**
     * This is the DAO associated with this table
     */
    override val dao: UsersDao = UsersDao(
            this,
            insertSqlBuilder = sqlBuilders.insertSqlBuilder,
            updateSqlBuilder = sqlBuilders.updateSqlBuilder,
            deleteSqlBuilder = sqlBuilders.deleteSqlBuilder,
            querySqlBuilder = sqlBuilders.querySqlBuilder
    )


    override fun create(value: Value<User>): User {

        /**
         * Value#of is an infix function which makes it read better
         */
        return User(
                value of id, value of firstName, value of lastName, value of emailId,
                value of age, value of address, value of photoUrl,
                value of createdAt, value of lastSeenAt
        )
    }


    companion object {
        private val currentDatePropExtractor = object : PropertyExtractor<User, Date> {
            override fun extractFrom(t: User): Date = Date()
        }


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