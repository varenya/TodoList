package com.todoist

import com.todoist.config.getWebAppConfig
import com.todoist.repository.TodoRepository
import com.todoist.repository.createDataSource
import com.todoist.repository.migrateDataSource
import com.todoist.routes.AddTodo
import com.todoist.routes.ListTodos
import org.http4k.contract.ContractRoutingHttpHandler
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.EventFilters
import org.http4k.events.Events
import org.http4k.events.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Http4kServer
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.jetbrains.exposed.sql.*
import java.time.Clock

fun TodoAppRouteHandler(todoRepository: TodoRepository): ContractRoutingHttpHandler {
    val todoApp =
        "/todo" bind contract {
            renderer = OpenApi3(ApiInfo("TodoList API", "v1.0"))

            // Return Swagger API definition under /contract/api/v1/swagger.json
            descriptionPath = "/swagger.json"

            // You can use security filter tio protect routes
            security = ApiKeySecurity(Query.int().required("api"), { it == 42 }) // Allow only requests with &api=42

            // Add contract routes
            routes += AddTodo(todoRepository).contractRoute()
            routes += ListTodos(todoRepository).contractRoute()
        }
    return todoApp
}


fun TodoApp(env: String, clock: Clock, events: Events): HttpHandler {

    val appConfig = getWebAppConfig(env)
    val dataSource = createDataSource(appConfig).also(::migrateDataSource)
    val database = Database.connect(dataSource)
    val todoRepository = TodoRepository(database)
    val app = routes(
        TodoAppRouteHandler(todoRepository),
        static(ResourceLoader.Classpath("public"))
    )
    return ServerFilters.CatchAll()
        .then(DebuggingFilters.PrintRequest())
        .then(app)
}

fun TodoAppServer(env: String): Http4kServer =
    TodoApp(env, Clock.systemDefaultZone(), AutoMarshallingEvents(Jackson))
        .asServer(Undertow(9090))