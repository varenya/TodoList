package com.todoist

import com.todoist.config.getWebAppConfig
import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.TodoRepository
import com.todoist.repository.createDataSource
import com.todoist.routes.todoItemDetailsLens
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.jetbrains.exposed.sql.Database

val webConfig = getWebAppConfig("test")
val testDataSource = createDataSource(webConfig)


class TodoListTest : WordSpec({
    val database = Database.connect(testDataSource)
    "todo app" should {
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
            responseTodoItem.title shouldBeEqual "sample title"
            responseTodoItem.description shouldBeEqual "sample description"
            responseTodoItem.status shouldBeEqual todoItem.status
        }
    }
})
