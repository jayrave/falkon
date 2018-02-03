**NOTE: This library is still in its alpha!**

# Falkon [![Build Status](https://travis-ci.org/jayrave/falkon.svg?branch=master)](https://travis-ci.org/jayrave/falkon) [ ![Download](https://api.bintray.com/packages/jayrave/android/falkon-android/images/download.svg) ](https://bintray.com/jayrave/android/falkon-android/_latestVersion)
Clean & simple API to interact with the database (for Android & Kotlin)

## Design Principles
 - Should let domain models untouched
 - Should keep reflection to a minimum
 - Should generate SQL that is as clean as handwritten SQL
 - Should be able to talk with different database engines
 - Should be modular to let the users pick & choose the functionality they want
 - Should let users dive down to raw SQL without any ceremony if they desire
 - Shouldn't do implicit joins

## What can it do?
- Talk with different databases. Out of the box support is available for Android's SQLite & H2
- An easy way to express how domain models are mapped to tables
- A type-safe way to insert (or replace) into, update, delete from & query the tables
- Add reactive stream semantics to SQL operations

**NOTE:** Falkon isn't a traditional ORM (object relational mapping). Although it does a great job at OM (object mapping => serialize to & deserialize from db records), relationships aren't taken care of i.e., foreign fields are not automatically converted into foreign objects. This makes sure that unnecessary data is not loaded from the db (in case of a lengthy chain of foreign fields) and forces the user to think about writing queries that just loads what they want (hopefully)

## Dependency
The following is true for an Android gradle project. For more configurations, check out the advanced section
```gradle
// This is usually in the top-level build.gradle file
allprojects {
    repositories {
        jcenter() // Since all falkon artifacts live in Bintray's jCenter
    }
}

// Include this in the module you wish to use Falkon in
dependencies {
    compile "com.jayrave.falkon:falkon-android:$falkonVersion"
    compile "com.jayrave.falkon:falkon-rxjava-1:$falkonVersion" // if reactivity is desired
}
```

## Simple tutorial
Let's look at the model that we will be saving to / retrieving from the database

```kotlin
// There is no need for the model to be a data class or to just contain read-only fields!
// These are just good recommendations (immutability for the win)
data class User(
        val id: UUID,
        val name: String,
        val age: Int?,
        val address: String?,
        val lastSeenAt: Date
)
```

### TableConfiguration
Every table needs a configuration. This carries information like
- The database engine this table will talk with
- Converters to convert between Kotlin & SQL types

#### Engine
There is a default implementation => `DefaultEngine`. This guy needs a core to talk with the actual database. To talk with Android's SQLite use `AndroidSqliteEngineCore` (There is also a `JdbcEngineCore`, if you want to connect to a database via `JDBC`)

```kotlin
val engine = DefaultEngine(AndroidSqliteEngineCore(new YourSqliteOpenHelper()))
```

There is a default implementation of `TableConfiguration` => `TableConfigurationImpl`. To build it, in addition to an engine, some more stuff are required.

```kotlin
val tableConfiguration = TableConfigurationImpl(
        engine = engine, // The engine we just built
        typeTranslator = SqliteTypeTranslator(), // How Kotlin types are stored in this particular db
        nameFormatter = CamelCaseToSnakeCaseFormatter() // How to derive table column name (if one isn' provided) from object field name
)

// Converters must be registered to inform how to handle data conversion between
// Kotlin & database types. Converters are provided for all default types - Byte,
// Boolean, Char, Short, Int, Float, Long, Double & even for Enum types.
tableConfiguration.registerDefaultConverters()

// You can register your custom converters too
// tableConfiguration.registerForNonNullValues(Date::class.java, YourCustomDateConverter())
// tableConfiguration.registerForNullableValues(UUID::class.java, YourCustomUuidConverter(), true)
```

### Table
Tables inform Falkon about how a model maps to a table. `BaseEnhancedTable` provides
a lot of defaults & is a good class to extend for you table mappings

```kotlin
class UsersTable(configuration: TableConfiguration, sqlBuilders: SqlBuilders) :
        BaseEnhancedTable<User, UUID, UsersDao>(
                "users", configuration, sqlBuilders.createTableSqlBuilder) {

    val id = col(name = "id", User::id, isId = true)
    val name = col(User::name, maxSize = 255) // If a name isn't given, it will be derived from the field name
    val age = col(User::age, isNonNull = false) // isNonNull adds NOT NULL to the column definition
    val address = col(User::address, isUnique = true) // isUnique adds UNIQUE to the column definition
    val lastSeenAt = col(User::lastSeenAt, converter = dateConverter) // Custom converters can be specified

    override val dao: UsersDao = UsersDao(this, sqlBuilders) // DAO associated with this table

    private val extractFromHelper = SimpleIdExtractFromHelper(id)
    override fun <C> extractFrom(id: UUID, column: Column<User, C>): C {
        return extractFromHelper.extractFrom(id, column)
    }

    override fun create(value: Value<User>): User {
        return User(value of id, value of name, value of age, value of address, value of lastSeenAt)
    }
}
```

### DAO
All the builders (for insert (or replace), update, delete & query) in the following section are context aware (suggests appropriate methods at appropriate time when used as a fluent-interface) & are type-safe

#### Insert
```kotlin
// Models can be directly inserted
usersTable.dao.insert(createRandomUser())
usersTable.dao.insert(listOf(createRandomUser(), createRandomUser(), createRandomUser()))

// An InsertBuilder is provided for greater flexibility
usersTable.dao
    .insertBuilder()
    .values {
        set(usersTable.id, UUID.randomUUID())
        set(usersTable.age, 42)
    }.insert()
```

#### Update
```kotlin
// Models can be directly updated
usersTable.dao.update(editedUser)
usersTable.dao.update(listOf(editedUser1, editedUser2))

// An UpdateBuilder is provided for greater flexibility
usersTable.dao
        .updateBuilder()
        .values { set(usersTable.lastSeenAt, Date()) }
        .where()
        .eq(usersTable.id, newUsers.first().id)
        .update()
```

#### Delete
```kotlin
// Models can be directly deleted
usersTable.dao.delete(user)
usersTable.dao.delete(listOf(user1, user2))

// Models can be deleted by IDs too
usersTable.dao.deleteById(user.id)
usersTable.dao.deleteById(listOf(user1.id, user2.id))

// A DeleteBuilder is provided for greater flexibility
usersTable.dao.deleteBuilder()
        .where()
        .eq(usersTable.id, newUsers.first().id)
        .delete()
```

#### Insert or replace
```kotlin
// Models can be directly inserted or replaced (based on whether the record already exists)
val newUser = createRandomUser()
usersTable.dao.insertOrReplace(newUser)
usersTable.dao.insertOrReplace(listOf(createRandomUser(), createRandomUser()))

val updatedUser = newUser.copy(name = "modified name")
usersTable.dao.insertOrReplace(updatedUser) // Will update already existing user

// An InsertOrReplaceBuilder is provided for greater flexibility
usersTable.dao
    .insertOrReplaceBuilder()
    .values {
        set(usersTable.name, "yoohoo")
        set(usersTable.age, 42)
    }.insertOrReplace()
```

#### Query
```kotlin
// There are several convenience methods
usersTable.dao.findById(user.id)
usersTable.dao.findAll()

// A QueryBuilder is provided for greater flexibility
usersTable.dao.queryBuilder()
        .where()
        .gt(usersTable.lastSeenAt, thresholdDate)
        .compile()
        .extractAllModelsAndClose(usersTable) { it.qualifiedName }

// An observable stream can also be setup to run queries whenever there is a change in required tables
val query = usersTable.dao.queryBuilder().build()
val observable = usersTable.configuration.engine.createCompiledQueryObservable(
        query.tableNames, query.sql, query.arguments, Schedulers.newThread()
)

observable.subscribe({ compiledQuery ->
    logInfo("This will be run whenever there is a change to ${query.tableNames.joinToString()} table(s)")
})
```

## Going deeper
There are a lot more features to discover
- Support for composite primary key
- Logger (to log all SQL, along with the arguments)
- DbEventListener (get notified on insert, update & delete events)
- Transactions (nested transactions are supported too)
- Compiled statements (for performance & re-use)
- Type-safe `JOIN` capable query builder
- Easy extraction of custom models (for e.g., from `JOIN` queries or queries using aggregates etc. via `Realizer`)
- etc.

To learn more check out
- [Sample project](https://github.com/jayrave/falkon/tree/master/sample-android)
- Unit tests
- Dive into source code (it is open-source :))

## Advanced: pick & choose the modules
Falkon has been designed to be very modular. You can plug these modules together to make object mapping as featureful or as simple as possible.

- *Core modules:* Engine, Mapper, SqlBuilder & DAO
- *Non-core modules:* Rx

### Engine
Engine modules provide the functionality to talk with database engines. There are 3 such modules

 - `falkon-engine` => provides the interfaces (engine, compiled statement, logger etc.)
 - `falkon-engine-android-sqlite` => engine implementation to talk with Android's SQLite
 - `falkon-engine-jdbc` => engine implementation to talk with databases via JDBC

### Mapper
Mapper modules provide a way to map Kotlin objects to database columns. There are 3 such modules

- `falkon-mapper` => provides the interfaces (table, column, converter etc.)
- `falkon-mapper-basic` => provides basic mapper implementation
- `falkon-mapper-enhanced` => provides enhanced mapping functionality (basic + DAO)

### SqlBuilder
SqlBuilder modules provide a way to build raw SQL to send to the database. There are 2 such modules

- `falkon-sql-builder` => provides the interfaces (sql builder for insert (or replace), update, delete, query & create table statements)
- `falkon-sql-builder-h2` => provides implementation for H2 database
- `falkon-sql-builder-sqlite` => provides implementation for SQLite database

### DAO
DAO modules provide type-safe API to insert, update, delete & query. There are 2 such modules

- `falkon-dao` => provides insert, update, delete & query builder interfaces & implementation
- `falkon-dao-extn` => provides extension to dao like inserting, updating, deleting models & deleting, querying by id etc.

### Rx
Rx modules introduce reactive stream semantics to SQL operations

- `falkon-rxjava-1` => provides extensions to engines to setup observable streams

## Gradle dependencies
All artifacts live in Bintray's `jcenter`

```gradle
compile 'com.jayrave.falkon:falkon-android:$falkonVersion'
compile 'com.jayrave.falkon:falkon-dao:$falkonVersion'
compile 'com.jayrave.falkon:falkon-dao-extn:$falkonVersion'
compile 'com.jayrave.falkon:falkon-engine:$falkonVersion'
compile 'com.jayrave.falkon:falkon-engine-android-sqlite:$falkonVersion'
compile 'com.jayrave.falkon:falkon-engine-jdbc:$falkonVersion'
compile 'com.jayrave.falkon:falkon-mapper:$falkonVersion'
compile 'com.jayrave.falkon:falkon-mapper-basic:$falkonVersion'
compile 'com.jayrave.falkon:falkon-mapper-enhanced:$falkonVersion'
compile "com.jayrave.falkon:falkon-rxjava-1:$falkonVersion"
compile 'com.jayrave.falkon:falkon-sql-builder:$falkonVersion'
compile 'com.jayrave.falkon:falkon-sql-builder-h2:$falkonVersion'
compile 'com.jayrave.falkon:falkon-sql-builder-sqlite:$falkonVersion'
```

## Building Falkon
Just clone the repository & run `./gradlew build` from project root. It requires the following
- Java 6
- Android SDK 24 (not required, if Android modules need not participate in the build)

**Note:** To exclude Android modules from the build, add `falkon.excludeAndroidModulesFromBuild = true` to *local.properties* file in project root

## Credits
- [Andrew O'Malley](https://github.com/andrewoma) - creator of [kwery](https://github.com/andrewoma/kwery) which gave me the idea & some core concepts
- [Gray](https://github.com/j256) - creator of [ORMLite](https://github.com/j256/ormlite-core) which inducted me into the world of ORMs & has been my choice of work-horse for the past few years


## Check this out
If you like keeping your models clean, you may be interested in checking out another library => [Moshi: Pristine Models](https://github.com/jayrave/moshi-pristine-models) (Disclaimer: I am the author), an add-on to [Moshi] (https://github.com/square/moshi), which helps to keep your models free of JSON serializing/deserializing specific annotations. Like this library, it enables to programmatically define the mapping between models & JSON
