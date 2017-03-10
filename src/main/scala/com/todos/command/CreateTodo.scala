package com.todos.command

case class CreateTodo(
    title: String,
    completed: Boolean
)

object CreateTodo {
  val serializeId: String = "a5d8a9d3-fd9d-40b3-a841-1da4048c822a"
}
