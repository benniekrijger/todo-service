package com.todos.repository

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.model.{Todo, TodoRegistry}

trait TodoRepositoryUpdater {
  this: PersistentActor with ActorLogging =>

  var state: TodoRegistry

  val updateState: Receive = {
    case evt: TodoCreated =>
      state = state.copy(
        todos = state.todos :+ Todo(
          id = evt.id,
          title = evt.title,
          completed = evt.completed
        )
      )
    case evt: TodoRemoved =>
      state = state.copy(
        todos = state.todos.filterNot(_.id == evt.id)
      )
  }

}
