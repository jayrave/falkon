package com.jayrave.falkon.sample_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.jayrave.falkon.dao.delete
import com.jayrave.falkon.dao.insert
import com.jayrave.falkon.dao.insertOrReplace
import com.jayrave.falkon.dao.update
import com.jayrave.falkon.engine.*
import com.jayrave.falkon.engine.android.sqlite.AndroidSqliteEngineCore
import com.jayrave.falkon.engine.rxjava1.createCompiledQueryObservable
import com.jayrave.falkon.mapper.CamelCaseToSnakeCaseFormatter
import com.jayrave.falkon.mapper.TableConfiguration
import com.jayrave.falkon.mapper.TableConfigurationImpl
import com.jayrave.falkon.mapper.registerDefaultConverters
import com.jayrave.falkon.sample_android.models.User
import com.jayrave.falkon.sample_android.tables.MessagesTable
import com.jayrave.falkon.sample_android.tables.UsersTable
import com.jayrave.falkon.sqlBuilders.sqlite.*
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var usersTable: UsersTable
    private lateinit var messagesTable: MessagesTable
    private lateinit var logView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Extract log view
        logView = findViewById(R.id.log_view)

        // Setup falkon stuff to run on button click
        findViewById<View>(R.id.perform_falkon_magic).setOnClickListener {
            uiLog("Get ready to be blown away")
            performFalkonMagic()
            uiLog("That's all folks")
        }
    }


    private fun performFalkonMagic() {
        /**
         * Most of the inserts, updates, deletes & queries are done on the main thread.
         * This is just for the sake of demonstrating the capabilities of this library.
         * Please don't do this in production code
         */

        createTableObjects()
        uiLog("Table objects created")

        insertRecords()
        uiLog("Done demonstrating inserting records")

        updateRecords()
        uiLog("Done demonstrating updating records")

        deleteRecords()
        uiLog("Done demonstrating deleting records")

        insertOrReplaceRecords()
        uiLog("Done demonstrating inserting or replacing records")

        queryRecords()
        uiLog("Done demonstrating querying objects")
    }


    private fun createTableObjects() {
        val tableConfiguration = buildTableConfiguration()
        val sqlBuilders = buildSqlBuilders()

        usersTable = UsersTable(tableConfiguration, sqlBuilders)
        messagesTable = MessagesTable(tableConfiguration, sqlBuilders, usersTable)
    }


    private fun insertRecords() {

        /**
         * `falkon-dao-extn` provides some cool methods to insert stuff into your tables.
         * It is super easy to insert records. You just build them & pass to the DAO.
         * You can do it one or a group of them at a time
         */
        usersTable.dao.insert(createRandomUser())
        usersTable.dao.insert(listOf(createRandomUser(), createRandomUser(), createRandomUser()))

        /**
         * Also an InsertBuilder is provided for greater flexibility
         */
        usersTable.dao
                .insertBuilder()
                .values {
                    set(usersTable.id, UUID.randomUUID())
                    set(usersTable.firstName, "example_first_name")
                    set(usersTable.lastName, "example_last_name")
                    set(usersTable.emailId, "example-user@example.com")
                    set(usersTable.lastSeenAt, Date())
                }.insert()
    }


    private fun updateRecords() {

        /**
         * `falkon-dao-extn` provides some cool methods to update stuff in your tables.
         * It is super easy to update records. You just build them & pass to the DAO.
         * You can do it one or a group of them at a time
         */

        // Let us insert some users
        val newUsers = listOf(createRandomUser(), createRandomUser(), createRandomUser())
        usersTable.dao.insert(newUsers)

        usersTable.dao.update(newUsers.first().copy(firstName = "modified first name"))
        usersTable.dao.update(listOf(
                newUsers[1].copy(firstName = "Joey"),
                newUsers[2].copy(lastSeenAt = Date(0)),
                createRandomUser() // This user wouldn't have any effect as he doesn't exist in the table
        ))

        /**
         * Also a UpdateBuilder is provided for greater flexibility. The builder is context
         * aware (when used in a fluent-interface manner) & type-safe
         */
        usersTable.dao
                .updateBuilder()
                .values { set(usersTable.lastSeenAt, Date()) }
                .where()
                .eq(usersTable.id, newUsers.first().id)
                .update()
    }


    private fun deleteRecords() {

        /**
         * `falkon-dao-extn` provides some cool methods to delete stuff in your tables.
         * It is super easy to delete records. You just build them & pass to the DAO.
         * You can do it one or a group of them at a time
         */

        // Let us insert some users
        val newUsers = listOf(createRandomUser(), createRandomUser(), createRandomUser())
        usersTable.dao.insert(newUsers)

        usersTable.dao.delete(newUsers.first())
        usersTable.dao.delete(listOf(
                newUsers[1],
                newUsers[2],
                createRandomUser() // This user wouldn't have any effect as he doesn't exist in the table
        ))

        /**
         * Also a DeleteBuilder is provided for greater flexibility. The builder is context
         * aware (when used in a fluent-interface manner) & type-safe
         */
        usersTable.dao
                .deleteBuilder()
                .where()
                .eq(usersTable.id, newUsers.first().id)
                .delete()
    }


    private fun insertOrReplaceRecords() {

        /**
         * `falkon-dao-extn` provides some cool methods to insert or replace stuff into your
         * tables. It is super easy to do it. You just build them & pass to the DAO. You can
         * do it one or a group of them at a time
         */
        val newUser = createRandomUser()
        usersTable.dao.insertOrReplace(newUser)
        usersTable.dao.insertOrReplace(listOf(
                createRandomUser(), createRandomUser(), createRandomUser()
        ))

        val updatedUser = newUser.copy(firstName = "modified first name")
        usersTable.dao.insertOrReplace(updatedUser) // Will update already existing user

        /**
         * Also an InsertOrReplaceBuilder is provided for greater flexibility
         */
        usersTable.dao
                .insertOrReplaceBuilder()
                .values {
                    set(usersTable.id, UUID.randomUUID())
                    set(usersTable.firstName, "example_first_name")
                    set(usersTable.lastName, "example_last_name")
                    set(usersTable.emailId, "example-user@example.com")
                    set(usersTable.lastSeenAt, Date())
                }.insertOrReplace()
    }


    private fun queryRecords() {

        /**
         * Falkon has some nifty ways to query for stuff in the database. Check out `UsersDao`
         */

        usersTable.dao.findUser(UUID.randomUUID())
        usersTable.dao.findAllUsers()
        usersTable.dao.findAllUsersSeenAfter(Date())
        usersTable.dao.findAllUsersWhoHaveSentAtLeastOneMessage(messagesTable)
        usersTable.dao.findAllUsersWhoReceivedAtLeastOneMessageInTheLastWeek(messagesTable)

        /**
         * You can also setup an observable pipeline to run queries every time there
         * is a change to a particular table(s)
         */
        val query = usersTable.dao.queryBuilder().build()
        val observable = usersTable.configuration.engine.createCompiledQueryObservable(
                query.tableNames, query.sql, query.arguments, Schedulers.newThread()
        )

        observable.subscribe({ compiledQuery ->
            logInfo(
                    "This will be run whenever there is a change to " +
                            "${query.tableNames.joinToString()} table(s)"
            )
        })
    }


    private fun buildTableConfiguration(): TableConfiguration {
        /**
         * `TypeTranslator` gives info about how `Kotlin` types correspond to the database
         * types. The `TypeTranslator` you choose should correspond to the `Engine` you
         * wanna work with
         *
         * `NameFormatter` is used to derive a column's db name from its kotlin property
         * (if a name is not provided explicitly)
         */

        val configuration = TableConfigurationImpl(
                engine = buildEngine(),
                typeTranslator = SqliteTypeTranslator(),
                nameFormatter = CamelCaseToSnakeCaseFormatter()
        )

        /**
         * Converters must be registered to inform how to handle data conversion between
         * Kotlin & database types. Converters are provided for all default types - [Byte],
         * [Boolean], [Char], [Short], [Int], [Float], [Long], [Double] & even for
         * [Enum] types. You can register your custom converters too
         */
        configuration.registerDefaultConverters()
        configuration.registerForNullableValues(UUID::class.java, NullableUuidConverter(), true)
        configuration.registerForNullableValues(Date::class.java, NullableDateConverter(), true)
        return configuration
    }


    private fun buildSqlBuilders(): SqlBuilders {

        /**
         * These builders are provided by `falkon-sql-builder-simple` module
         */

        return SqlBuilders(
                SqliteCreateTableSqlBuilder(),
                SqliteInsertSqlBuilder(),
                SqliteUpdateSqlBuilder(),
                SqliteDeleteSqlBuilder(),
                SqliteInsertOrReplaceSqlBuilder(),
                SqliteQuerySqlBuilder()
        )
    }


    private fun buildEngine(): Engine {

        /**
         * [Engine] takes care of compiling the SQL provided into `CompiledStatement`s
         * which can then be executed (they also facilitate reuse). It is an interface
         * that needs to be implemented to talk to your db of choice.
         *
         * [DefaultEngine] is the default implementation & it is good enough for majority
         * of the use cases. [DefaultEngine] needs an `EngineCore` to talk to the db.
         * `AndroidSqliteEngineCore` comes from `falkon-engine-android-sqlite` module &
         * it can talk with the default SQLite that Android ships with. There are other
         * engine cores too. There is `JdbcEngineCore` that can talk with DB through
         * the JDBC API. Coding up your own `Engine` & `EngineCore` is pretty easy
         *
         * Also, if you want to log the SQL generated & executed by Falkon, just send
         * an instance of a [Logger] to [DefaultEngine] (don't do this in production)
         */


        val sampleSqliteOpenHelper = SampleSqliteOpenHelper(this)
        val logger = object : Logger {
            override fun onExecutionFailed(sql: String, arguments: Iterable<Any?>) {
                logError(buildLogMessage(sql, arguments))
            }

            override fun onSuccessfullyExecuted(sql: String, arguments: Iterable<Any?>) {
                logDebug(buildLogMessage(sql, arguments))
            }

            private fun buildLogMessage(sql: String, arguments: Iterable<Any?>): String {
                val args = arguments.joinToString()
                return when {
                    args.isBlank() -> "sql: $sql"
                    else -> "sql: $sql; arguments: $args"
                }
            }
        }

        return DefaultEngine(AndroidSqliteEngineCore(sampleSqliteOpenHelper), logger)
    }


    private fun uiLog(message: String) {
        logView.append("\n$message")
    }



    companion object {

        private val random = Random()
        private val <T> List<T>.randomElement: T
            get() = get(random.nextInt(size))

        private val firstNames = listOf(
                "Walker", "Elisha", "Gregory", "Benny", "Mitch", "Andreas",
                "Leana", "Beatris", "Marylyn", "Nicolasa", "Adelle", "Rosalind"
        )

        private val lastNames = listOf(
                "Tomas", "Kilkenny", "Rosemond", "Pyles", "Wyche", "Vancleave",
                "Jennette", "Quist", "Kilmer", "Drummond", "Campana", "Blakeney"
        )

        private fun createRandomUser(): User {
            val firstName = firstNames.randomElement
            val lastName = lastNames.randomElement
            val email = "$firstName.$lastName@example.com"
            val photoUrl = "https://www.example.com/$firstName.$lastName/${UUID.randomUUID()}"

            return User(
                    UUID.randomUUID(), firstName, lastName, email,
                    random.nextInt(75), photoUrl, Date()
            )
        }
    }
}
