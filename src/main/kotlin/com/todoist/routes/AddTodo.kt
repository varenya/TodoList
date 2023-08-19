package com.todoist.routes

import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.TodoRepository
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK

data class TodoItemDetails(val title: String, val description: String, val status: TodoStatus)

val todoItemDetailsLens = Body.auto<TodoItemDetails>().toLens()

class AddTodo(val todoRepository: TodoRepository) {
    private val spec = "/add" meta {
        summary = "Add todo item to the list"
        receiving(todoItemDetailsLens)
        returning(
            OK,
            todoItemDetailsLens to TodoItemDetails(
                title = "sample",
                description = "this is a sample todo",
                status = TodoStatus.PENDING
            )
        )
    } bindContract Method.POST

    private val todoHandler: HttpHandler = { request ->
        val todoItem = todoItemDetailsLens(request)
        Response(OK).with(todoItemDetailsLens of todoItem)
    }

    fun contractRoute(): ContractRoute = spec to todoHandler
}