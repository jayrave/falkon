package com.jayrave.falkon.sample_android.daos

import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.dao.findAll
import com.jayrave.falkon.dao.findById
import com.jayrave.falkon.dao.lib.extractAllModelsAndClose
import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.select
import com.jayrave.falkon.sample_android.ARG_PLACEHOLDER
import com.jayrave.falkon.sample_android.models.User
import com.jayrave.falkon.sample_android.tables.MessagesTable
import com.jayrave.falkon.sample_android.tables.UsersTable
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import java.util.*

/**
 * Subclassing [DaoImpl] provides builders for INSERT, UPDATE, DELETE & SELECT statements.
 * Also if you include the `falkon-dao-extn` module, you will get more fancy features.
 * Check out the docs for more info
 */
class UsersDao(
        private val usersTable: UsersTable, insertSqlBuilder: InsertSqlBuilder,
        updateSqlBuilder: UpdateSqlBuilder, deleteSqlBuilder: DeleteSqlBuilder,
        querySqlBuilder: QuerySqlBuilder) :
        DaoImpl<User, UUID>(
                usersTable, ARG_PLACEHOLDER, insertSqlBuilder, updateSqlBuilder,
                deleteSqlBuilder, querySqlBuilder) {

    /**
     * [findById] is provided by the `falkon-dao-extn` module
     */
    fun findUser(id: UUID): User? = findById(id)


    /**
     * [findAll] is provided by the `falkon-dao-extn` module
     */
    fun findAllUsers(): List<User> = findAll()


    /**
     * Queries can also be built using [queryBuilder]. The builder is context aware (when
     * used in a fluent-interface manner) & type-safe
     *
     * Placeholders are used by default for all arguments to prevent SQL injection.
     * [extractAllModelsAndClose] is an extension function that allows safe extraction of
     * models out of a `CompiledStatement<Source>`. For more such options check out the file that
     * contains [extractAllModelsAndClose]
     */
    fun findAllUsersSeenAfter(thresholdDate: Date): List<User> {
        return queryBuilder()
                .where()
                .gt(usersTable.lastSeenAt, thresholdDate)
                .compile()
                .extractAllModelsAndClose(usersTable) {
                    it.qualifiedName // Query builder uses qualified column names to avoid collision
                }
    }


    /**
     * It is really easy to do simple JOIN queries too. For complex JOIN queries, take
     * a look at [findAllUsersWhoReceivedAtLeastOneMessageInTheLastWeek]
     *
     * @see findAllUsersWhoReceivedAtLeastOneMessageInTheLastWeek
     */
    fun findAllUsersWhoHaveSentAtLeastOneMessage(messagesTable: MessagesTable): List<User> {
        return queryBuilder()
                .join(usersTable.id, messagesTable.fromUserId)
                .groupBy(usersTable.id)
                .compile()
                .extractAllModelsAndClose(usersTable) {
                    it.qualifiedName // Query builder uses qualified column names to avoid collision
                }
    }


    /**
     * When you find [queryBuilder] to be too restrictive to work with, go for
     * [lenientQueryBuilder]. For example, in this case where we want to get the list of users
     * who received at least one message in the last week, we can't use [queryBuilder] because
     * it restricts us to include only WHERE clauses for columns belonging to [UsersTable]!!
     * This is where [lenientQueryBuilder] comes to the rescue as it isn't table dependent.
     *
     * [lenientQueryBuilder] by default includes all the columns from all the tables involved
     * in the JOINS. To make it more efficient, just select the columns [UsersTable] is
     * concerned with & then build [User]s out of the result set
     *
     * @see findAllUsersWhoHaveSentAtLeastOneMessage
     */
    fun findAllUsersWhoReceivedAtLeastOneMessageInTheLastWeek(
            messagesTable: MessagesTable): List<User> {

        val cutOffDate = Date(Date().time - (7 * 24 * 60 * 60 * 1000))
        return lenientQueryBuilder()
                .fromTable(usersTable)
                .join(usersTable.id, messagesTable.toUserId)
                .where().ge(messagesTable.receivedAt, cutOffDate)
                .select(usersTable.allColumns)
                .compile()
                .extractAllModelsAndClose(usersTable) {
                    it.qualifiedName // Query builder uses qualified column names to avoid collision
                }
    }
}