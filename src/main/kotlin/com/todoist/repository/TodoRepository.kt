package com.todoist.repository

import com.todoist.formats.TodoItem
import com.todoist.formats.TodoStatus
import com.todoist.repository.Todos.description
import com.todoist.repository.Todos.status
import com.todoist.repository.Todos.title
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val title: Column<String> = text("title")
    val description: Column<String> = text("description")
    val status: Column<TodoStatus> = enumeration("status")
    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
}

class TodoRepository(val database: Database) {
    fun addTodoItem(todoItem: com.todoist.formats.TodoItem) {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            Todos.insert {
                it[title] = todoItem.title
                it[description] = todoItem.description
                it[status] = todoItem.status
            }
        }
    }

    fun getAllTodos(): List<TodoItem> {
        val todoItems = transaction(database) {
            Todos.selectAll().map {
                TodoItem(title = it[title], description = it[description], status = it[status])
            }
        }
        return todoItems
    }
}