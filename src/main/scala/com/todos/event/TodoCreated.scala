package com.todos.event

import java.util.UUID

case class TodoCreated(
    id: UUID,
    title: String,
    completed: Boolean
)

object TodoCreated {
  val serializeId: String = "85628f52-324d-4a51-af1e-617892b65f64"
}
