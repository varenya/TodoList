package com.todoist

import com.todoist.config.getWebAppConfig
import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.TodoRepository
import com.todoist.repository.Todos
import com.todoist.repository.createDataSource
import com.todoist.routes.todoItemDetailsLens
import com.todoist.routes.todoItemsLens
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import org.http4k.core.Method
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


val webConfig = getWebAppConfig("test")
val testDataSource = createDataSource(webConfig)


class TodoListTest : WordSpec({
    val database = Database.connect(testDataSource)
    beforeTest {
        transaction(database) {
            SchemaUtils.drop(Todos)
            SchemaUtils.create(Todos)
        }
    }
    "todo app" should {
        "should get all todos when get method is called" {
            val todoApp = TodoAppRouteHandler(TodoRepository(database))
            val getRequest = Request(Method.GET, "/todo?api=42")
            val response = todoApp(getRequest)
            val actualResponse = todoItemsLens(response)

            response shouldHaveStatus OK
            actualResponse shouldHaveSize 0
        }
        "respond with todo details" {
            val todoItem = TodoItem(
                title = "sample title",
                description = "sample description",
                status = TodoStatus.COMPLETED
            )
            val todoApp = TodoAppRouteHandler(TodoRepository(database))
            val response = todoApp(
                Request(POST, "/todo/add?api=42")
                    .with(
                        todoItemDetailsLens of todoItem
                    )
            )

            val responseTodoItem = todoItemDetailsLens(response)

            response shouldHaveStatus OK
            responseTodoItem shouldBeEqual todoItem
        }
    }
})
