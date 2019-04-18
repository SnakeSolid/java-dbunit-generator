# DBUnit generator

Simple GUI application to generate DBUnit data set from queries.

## Examples

Input text example:

```sql
-- *
select *
from table_a as a
  inner join table_b as b using ( id )
where a.name = 'test' ;
-- table_c
select *
from table_c
where category = 2
```

First query will be used as template and split to two different queries:

```sql
-- table_a
select a.*
from table_a as a
  inner join table_b as b using ( id )
where a.name = 'test'
```

```sql
-- table_b
select b.*
from table_a as a
  inner join table_b as b using ( id )
where a.name = 'test'
```

Both queries will be executed independently. Last query will be used as is.
Result will contain three tables: `table_a`,  `table_b` and  `table_c`.

## Configuration

Configuration file example:

```yaml
---
font: # font settings for query and result editors
  name: "Monospaced" # Font family, default value "Monospaced"
  style: BOLD # Font style: PLAIN, BOLD, ITALIC or BOLD_ITALIC
  size: 14 # Font size in pixels

# Behavior if query has no comment with table name. Possible values: TEMPLATE or ERROR (default).
# * ERROR - queries with out table name are not allowed.
# * TEMPLATE - queries without table name used as templates.
noTableMode: ERROR

# If defined this table name will be used to use query as template.
templateTableName: "*"

# If defined this name prefix can be used to ignore queries.
skipTablePrefix: "-"

# Table name case change policy: UPPER or LOWER.
# If defined table name will be changed to corresponding case.
tableNameCase: UPPER

# Map connection name to driver setting. Several connections can
# use similar settings with different parameters.
drivers:
  "PostgreSQL": # Connection name. This name will be shown in driver settings dalog.
    # URL-like path to JDBC driver library.
    driverPath: "file:lib/postgresql-42.2.5.jar"

    # Full JDBC driver class name
    driverClass: "org.postgresql.Driver"

    # JDBC connection URL to use for this connection. URL can contain
    # special place holders for parameters. Placeholder must be in
    # format "{parameter_name}", where parameter_name - parameter name.
    url: "jdbc:postgresql://{host}:{port}/{database}?user={user}&password={password}"

    # Type mapping. By default all types will be shown as raw strings.
    # If some type requires some conversion, it can be defined in mappings.
    # Allowed following conversions: ASCII, UTF8, HEX, BASE64, BASE64_WITH_PREFIX
    typeMappings:
      "bytea": BASE64

    # Parameter names for JDBC URL placeholders. All these parameters
    # will be shown in connection dialog.
    parameters:
      - "host"
      - "port"
      - "database"
      - "user"
      - "password"
```
