#Version 0.2-alpha (2016-09-20)

Fix: Android's SQLiteCursor can't handle `table_name.column_name`! Use aliases (using `AS`) in SQL queries to ensure that the column names are unique even in case of JOINs

#Version 0.1-alpha (2016-09-18)

Initial release