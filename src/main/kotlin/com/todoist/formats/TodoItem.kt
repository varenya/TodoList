package com.todoist.formats

enum class TodoStatus {
    COMPLETED,
    PENDING
}

data class TodoItem(val title: String, val description: String, val status: TodoStatus)

