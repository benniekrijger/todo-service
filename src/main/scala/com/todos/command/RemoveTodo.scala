package com.todos.command

import java.util.UUID

case class RemoveTodo(id: UUID)

object RemoveTodo {
  val serializeId: String = "be2da7ce-66a6-4af6-a84a-7f6409d92eb0"
}
