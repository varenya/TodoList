package com.todoist

fun main() {
    val env = System.getenv("KOTLIN_ENV") ?: "local"
    TodoAppServer(env).start()
}
