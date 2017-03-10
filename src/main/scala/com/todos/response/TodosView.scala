package com.todos.response

case class TodosView(
    todos: Seq[TodoView],
    total: Int
)
