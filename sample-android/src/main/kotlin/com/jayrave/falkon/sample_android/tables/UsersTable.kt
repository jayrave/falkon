package com.jayrave.falkon.sample_android.tables

import com.jayrave.falkon.mapper.*
import com.jayrave.falkon.mapper.lib.SimpleIdExtractFromHelper
import com.jayrave.falkon.sample_android.SqlBuilders
import com.jayrave.falkon.sample_android.daos.UsersDao
import com.jayrave.falkon.sample_android.models.User
import java.util.*
import kotlin.reflect.KProperty1

/**
 * [Table]s inform Falkon about how a model maps to a table. [BaseEnhancedTable] provides
 * a lot of defaults & is a good class to extend for you table mappings.
 *
 * This class explains some of the features. For more, check out [MessagesTable]
 */
class UsersTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<User, UUID, UsersDao>(
                "users", configuration, sqlBuilders.createTableSqlBuilder) {

    /**
     * isId makes column a part of table's primary key
     *
     * By default, the property name is snake-cased & used as the database column name.
     * For example `Model::exampleColumn1` will use `example_column_1` as its column
     * name. You can change this by passing in a custom name or a different
     * [NameFormatter] to [TableConfiguration]
     */
    val id = col(User::id, isId = true)

    /**
     * Column's name can be explicitly assigned. If not, [TableConfiguration.nameFormatter] is
     * used to convert the model's property name into the column name
     */
    val firstName = col(User::firstName, name = "first_name")

    /**
     * You can set max-size which will be used in CREATE TABLE statement that this table
     * builds (but SQLite doesn't honor that!! this would come useful in some other db)
     */
    val lastName = col(User::lastName, maxSize = 255)

    /**
     * isNonNull adds NOT NULL to the column definition in the built CREATE TABLE statement
     */
    val emailId = col(User::emailId, isNonNull = false)

    val age = col(User::age)

    /**
     * isUnique adds UNIQUE to the column definition in the built CREATE TABLE statement.
     * If multiple columns should be involved in the UNIQUE constraint, take a look at
     * [BaseEnhancedTable.addUniquenessConstraint]
     */
    val photoUrl = col(User::photoUrl, isUnique = true)

    /**
     * By default `#col` knows how to extract the property from a passed in instance.
     * It is a simple [KProperty1.get] call, but instead of that you may wanna do something
     * custom. For example, you may wanna pass back the current system time instead of
     * what `User::lastSeenAt` holds. For this case, just use a custom property extractor
     */
    val lastSeenAt = col(User::lastSeenAt, propertyExtractor = currentDatePropExtractor)

    /**
     * This is the DAO associated with this table
     */
    override val dao: UsersDao = UsersDao(
            this,
            insertSqlBuilder = sqlBuilders.insertSqlBuilder,
            updateSqlBuilder = sqlBuilders.updateSqlBuilder,
            deleteSqlBuilder = sqlBuilders.deleteSqlBuilder,
            insertOrReplaceSqlBuilder = sqlBuilders.insertOrReplaceSqlBuilder,
            querySqlBuilder = sqlBuilders.querySqlBuilder
    )


    private val extractFromHelper = SimpleIdExtractFromHelper(id)
    override fun <C> extractFrom(id: UUID, column: Column<User, C>): C {
        return extractFromHelper.extractFrom(id, column)
    }


    override fun create(value: Value<User>): User {

        /**
         * Value#of is an infix function which makes it read better
         */
        return User(
                value of id, value of firstName, value of lastName, value of emailId,
                value of age, value of photoUrl, value of lastSeenAt
        )
    }


    companion object {
        private val currentDatePropExtractor = object : PropertyExtractor<User, Date> {
            override fun extractFrom(t: User): Date = Date()
        }
    }
}