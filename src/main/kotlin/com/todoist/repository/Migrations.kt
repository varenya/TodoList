package com.todoist.repository

import org.flywaydb.core.Flyway
import javax.sql.DataSource


fun migrateDataSource(dataSource: DataSource) {
    Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .baselineOnMigrate(true)
        .table("flyway_schema_history")
        .load()
        .migrate()
}
