#Version 0.13-alpha (2017-01-10)
- Bug fix: `CompiledStatement` extension method, `bindColumns` was not using storage forms of column values

#Version 0.12-alpha (2017-01-10)
- Bug fix: `WhereBuilder` was not using storage forms of the passed in column values

#Version 0.11-alpha (2017-01-10)
- Allow `INSERT OR REPLACE` for tables with only primary key columns

#Version 0.10-alpha (2017-01-03)
- Add API to query builders to specify the type of JOIN

#Version 0.9-alpha (2016-12-05)
- `QueryBuilder`: raw select & capability to specify column alias
- Add `Realizer` (useful to extract custom models from `JOIN` queries, queries using aggregates etc.)

#Version 0.8-alpha (2016-11-23)
- Support `INSERT OR REPLACE` (using db specific constructs that provide atomicity)
- Add helpers to implement `Table#extractFrom` for both simple & composite primary keys
- Better `InsertBuilder` & `UpdateBuilder` for setting values for multiple columns
- Move `insert`, `update` & `delete` methods into their respective builders

#Version 0.7-alpha (2016-11-19)
- Simplified consumption of falkon (use `compile` instead of `compileOnly` & `provided` => used to use those because of gross misunderstanding of gradle's multi-project builds)
- `falkon-android` doesn't pull in `falkon-rxJava-1` module anymore

#Version 0.6-alpha (2016-11-18)
- Support composite primary keys
- Support `AUTO INCREMENT` columns
- Use database specific SQL builders (SQLite & H2 supported)
- Use database specific `TypeTranslator` (SQLite & H2 supported)
- Remove `RIGHT OUTER JOIN` as neither SQLite, nor H2 support it

#Version 0.5-alpha (2016-10-15)
- All `WhereBuilder`'s `IN` & `NOT IN` can now work with sub-queries
- `Insert`, `Update`, `Delete` & `Query` classes carry info about the tables they correspond to

#Version 0.4-alpha (2016-10-07)
- add & publish `falkon-rxjava-1`
- `falkon-android` now also brings `falkon-rxjava-1` as a transitive dependency

#Version 0.3-alpha (2016-09-29)
- add & publish `falkon-android` module to make getting started with Falkon easy
- improve read performance by optimizing column index computation
- fix bug: acquire appropriate converter from table configuration depending on the nullability of the property

#Version 0.2-alpha (2016-09-20)

Fix: Android's SQLiteCursor can't handle `table_name.column_name`! Use aliases (using `AS`) in SQL queries to ensure that the column names are unique even in case of JOINs

#Version 0.1-alpha (2016-09-18)

Initial release