package com.todoist.routes

import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.TodoRepository
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.*

val todoItemsLens = Body.auto<List<com.todoist.formats.TodoItem>>().toLens()

class ListTodos(val todoRepository: TodoRepository) {
    private val spec = "/" meta {
        summary = "get all todos"
        returning(
            Status.OK,
            todoItemsLens to listOf(
                TodoItem(
                    title = "sample",
                    description = "this is a sample todo",
                    status = TodoStatus.PENDING
                )
            )
        )
    } bindContract Method.GET

    private val todoListHandler: HttpHandler = {
        val todoItems = todoRepository.getAllTodos()
        Response(Status.OK).with(todoItemsLens of todoItems)
    }

    fun contractRoute(): ContractRoute = spec to todoListHandler
}