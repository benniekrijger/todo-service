package com.todos.response

import java.util.UUID

case class TodoView(
    id: UUID,
    title: String,
    completed: Boolean
)
