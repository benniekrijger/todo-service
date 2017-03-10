package com.todos.model

import java.util.UUID

case class Todo(
    id: UUID,
    title: String,
    completed: Boolean
)

object Todo {
  val serializeId: String = "68b7e4ed-1288-41cf-b93c-0080e5b39473"
}
