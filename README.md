**NOTE: This library is still in its alpha!**

#Falkon [![Build Status](https://travis-ci.org/jayrave/falkon.svg?branch=master)](https://travis-ci.org/jayrave/falkon)
Clean & simple Object-Mapping (OM) to talk with the database for Android & Kotlin

##Design Principles
 - Domain models are untouched
 - No implicit joins
 - No active record pattern
 - Minimal reflection (can be completely eschewed if desired)
 - Generated SQL should be as clean as handwritten SQL
 - Should be able to talk with different database engines/abstractions

##Overview
Falkon has been designed to be very modular. You can plug these modules together to make object mapping as featureful or as simple as possible.

- *Core modules:* Engine, Mapper & SqlBuilder
- *Non-core modules:* DAO

###Engine
Engine modules provide the functionality to talk with database engines. There are 3 such modules

 - `falkon-engine` => provides the interfaces (engine, compiled statement, logger etc.)
 - `falkon-engine-android-sqlite` => engine implementation to talk with Android's SQLite
 - `falkon-engine-jdbc` => engine implementation to talk with databases via JDBC

###Mapper
Mapper modules provide a way to map Kotlin objects to database columns. There are 3 such modules

- `falkon-mapper` => provides the interfaces (table, column, converter etc.)
- `falkon-mapper-basic` => provides basic mapper implementation
- `falkon-mapper-enhanced` => provides enhanced mapping functionality (basic + DAO)

###SqlBuilder
SqlBuilder modules provide a way to build raw SQL to send to the database. There are 2 such modules

- `falkon-sql-builder` => provides the interfaces (sql builder for insert, update, delete, query & create table statements)
- `falkon-sql-builder-simple` => provides implementation to build raw SQL

###DAO
DAO modules provide type-safe API to insert, update, delete & query. There are 2 such modules

- `falkon-dao` => provides insert, update, delete & query builder interfaces & implementation
- `falkon-dao-extn` => provides extension to dao like inserting, updating, deleting models & deleting, querying by id etc.

##Gradle dependencies
All artifacts live in Bintray's `jcenter`

    compile 'com.jayrave.falkon:falkon-dao:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-dao-extn:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-engine:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-engine-android-sqlite:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-engine-jdbc:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-mapper:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-mapper-basic:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-mapper-enhanced:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-sql-builder:0.1-alpha'
    compile 'com.jayrave.falkon:falkon-sql-builder-simple:0.1-alpha'

**To use any module, its dependencies must also be included. These dependencies are NOT automatically included!**

For example, to use `falkon-engine-android-sqlite`, its dependency `falkon-engine` is also required which means that the following 2 compile statements must be included

    compile "com.jayrave.falkon:falkon-engine:$version"
    compile "com.jayrave.falkon:falkon-engine-android-sqlite:$version"

| Modules                      | Dependencies                                                                      |
|------------------------------|-----------------------------------------------------------------------------------|
| falkon-engine                | -                                                                                 |
| falkon-engine-android-sqlite | falkon-engine                                                                     |
| falkon-engine-jdbc           | falkon-engine                                                                     |
| falkon-mapper                | falkon-engine                                                                     |
| falkon-mapper-basic          | falkon-engine, falkon-mapper                                                      |
| falkon-mapper-enhanced       | falkon-engine, falkon-mapper, falkon-sql-builder, falkon-mapper-basic, falkon-dao |
| falkon-sql-builder           | -                                                                                 |
| falkon-sql-builder-simple    | falkon-sql-builder                                                                |
| falkon-dao                   | falkon-engine, falkon-mapper, falkon-sql-builder                                  |
| falkon-dao-extn              | falkon-engine, falkon-mapper, falkon-sql-builder, falkon-dao                      |