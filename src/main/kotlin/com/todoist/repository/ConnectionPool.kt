package com.todoist.repository

import com.todoist.config.AppConfig
import com.zaxxer.hikari.HikariDataSource

fun createDataSource(appConfig: AppConfig) = HikariDataSource().apply {
    jdbcUrl = appConfig.dbConfig.dbUrl.toString()
    driverClassName = "org.sqlite.JDBC"
}
