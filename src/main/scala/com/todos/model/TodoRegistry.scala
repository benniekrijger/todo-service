package com.todos.model

import java.util.UUID

case class TodoRegistry(
    todos: Seq[Todo],
    messageOffset: Option[Long]
) {
  lazy val todosById: Map[UUID, Todo] = todos
    .groupBy(_.id)
    .mapValues(_.head)
}

object TodoRegistry {
  def empty(): TodoRegistry = TodoRegistry(
    todos = Seq.empty,
    messageOffset = None
  )

  val serializeId: String = "ce894656-49c1-429d-b11b-82e2dc4a2abd"
}
