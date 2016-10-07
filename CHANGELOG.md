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