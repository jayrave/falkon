package com.jayrave.falkon.sample_android.daos

import com.jayrave.falkon.dao.DaoImpl
import com.jayrave.falkon.dao.findAll
import com.jayrave.falkon.dao.findById
import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.select
import com.jayrave.falkon.mapper.lib.extractAllModelsAndClose
import com.jayrave.falkon.sample_android.ARG_PLACEHOLDER
import com.jayrave.falkon.sample_android.Group
import com.jayrave.falkon.sample_android.User
import com.jayrave.falkon.sample_android.tables.GroupMembersTable
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
     * models out of a [CompiledQuery]. For more such options check out the file that
     * contains [extractAllModelsAndClose]
     */
    fun findAllUsersSeenAfter(thresholdDate: Date): List<User> {
        return queryBuilder()
                .where()
                .gt(usersTable.lastSeenAt, thresholdDate)
                .compile()
                .extractAllModelsAndClose(usersTable) { it.qualifiedName }
    }


    /**
     * It is really easy to do simple JOIN queries too. For complex JOIN queries, take
     * a look at [findAllUsersBelongingToGroup]
     *
     * @see findAllUsersBelongingToGroup
     */
    fun findAllUsersWhoBelongToAtLeastOneGroup(groupMembersTable: GroupMembersTable): List<User> {
        return queryBuilder()
                .join(usersTable.id, groupMembersTable.userId)
                .compile()
                .extractAllModelsAndClose(usersTable) { it.qualifiedName }
    }


    /**
     * When you find [queryBuilder] to be too restrictive to work with, go for
     * [lenientQueryBuilder]. For example, in this case where we want to get the list of users
     * belonging to a group, we can't use [queryBuilder] because it restricts us to include
     * only WHERE clauses for columns belonging to [UsersTable]!! This is where
     * [lenientQueryBuilder] comes to the rescue as it isn't table dependent.
     *
     * [lenientQueryBuilder] by default includes all the columns from all the tables involved
     * in the JOINS. To make it more efficient, just select the columns [UsersTable] is
     * concerned with & then build [User]s out of the result set
     *
     * @see findAllUsersWhoBelongToAtLeastOneGroup
     */
    fun findAllUsersBelongingToGroup(
            group: Group, groupMembersTable: GroupMembersTable): List<User> {

        return lenientQueryBuilder()
                .fromTable(usersTable)
                .join(usersTable.id, groupMembersTable.userId)
                .where().eq(groupMembersTable.groupId, group.id)
                .select(usersTable.allColumns)
                .compile()
                .extractAllModelsAndClose(usersTable) { it.qualifiedName }
    }
}