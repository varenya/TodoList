package com.todoist

import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.Todos
import com.todoist.routes.todoItemDetailsLens
import com.todoist.routes.todoItemsLens
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val client = OkHttp()
val server = TodoAppServer("test")

class TodoListIntegrationTest : WordSpec({
    val database = Database.connect(testDataSource)


    "todo app" should {
        beforeTest {
            println("resetting database!")
            transaction(database) {
                SchemaUtils.drop(Todos)
                SchemaUtils.create(Todos)
            }
            server.start()
        }

        afterEach {
            server.stop()
        }

        "add a todo item using the add api" {
            val todoItem = TodoItem(
                title = "sample title",
                description = "sample description",
                status = TodoStatus.COMPLETED
            )
            val addTodoRequest = Request(Method.POST, "http://localhost:9090/todo/add?api=42").with(
                todoItemDetailsLens of todoItem
            )
            val response = client(addTodoRequest)

            response shouldHaveStatus Status.OK

            val responseTodoItem = todoItemDetailsLens(response)

            responseTodoItem shouldBeEqual todoItem

        }
        "get all todos from the get endpoint" {
            val todoItem = TodoItem(
                title = "sample title",
                description = "sample description",
                status = TodoStatus.COMPLETED
            )
            val addTodoRequest = Request(Method.POST, "http://localhost:9090/todo/add?api=42").with(
                todoItemDetailsLens of todoItem
            )
            client(addTodoRequest)

            val getRequest = Request(Method.GET, "http://localhost:9090/todo?api=42")
            val getResponse = client(getRequest)

            getResponse shouldHaveStatus Status.OK

            val responseTodoItems = todoItemsLens(getResponse)

            responseTodoItems shouldHaveSize 1
        }
    }
})