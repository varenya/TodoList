package com.todoist

import com.todoist.config.getWebAppConfig
import com.todoist.repository.TodoSchema
import com.todoist.repository.createDataSource
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {

    val webConfig = getWebAppConfig("test")
    val testDataSource = createDataSource(webConfig)
    transaction(Database.connect(testDataSource)) {
        SchemaUtils.drop(TodoSchema)
    }

//    println(response.bodyString())
}
