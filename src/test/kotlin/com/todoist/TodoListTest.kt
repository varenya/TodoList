package com.todoist

import com.todoist.formats.TodoStatus
import com.todoist.routes.TodoItemDetails
import com.todoist.routes.todoItemDetailsLens
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TodoListTest : WordSpec({
    "todo app" should {
        "respond with todo details" {
            val todoItem = TodoItemDetails(
                title = "sample title",
                description = "sample description",
                status = TodoStatus.COMPLETED
            )
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
