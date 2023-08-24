package com.todoist

import com.todoist.config.getWebAppConfig
import com.todoist.repository.Todos
import com.todoist.repository.createDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {

    val webConfig = getWebAppConfig("test")
    val testDataSource = createDataSource(webConfig)
    transaction(Database.connect(testDataSource)) {
        SchemaUtils.drop(Todos)
    }

//    println(response.bodyString())
}
