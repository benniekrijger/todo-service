package com.todos.event

import java.util.UUID

case class TodoRemoved(id: UUID)

object TodoRemoved {
  val serializeId: String = "bfb3f7e5-306f-4971-88d4-aef52be98a44"
}
