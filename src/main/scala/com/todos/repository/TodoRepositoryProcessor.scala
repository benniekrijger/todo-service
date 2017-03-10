package com.todos.repository

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import com.todos.command.{CreateTodo, RemoveTodo}
import com.todos.event.utils.ProcessedEvent
import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.model.TodoRegistry
import com.todos.response.Success

class TodoRepositoryProcessor() extends PersistentActor with ActorLogging with TodoRepositoryUpdater {
  log.info("Started {}", self.path.name)

  def persistenceId: String = self.path.name

  var state: TodoRegistry = TodoRegistry.empty()

  def receiveCommand: Receive = {
    case cmd: CreateTodo =>
      persist(
        event = TodoCreated(
          id = UUID.randomUUID(),
          title = cmd.title,
          completed = cmd.completed
        )
      ) { persistedEvent =>
        updateState(persistedEvent)
        sender() ! Success()

        context.system.eventStream.publish(ProcessedEvent(persistedEvent, lastSequenceNr))
      }
    case cmd: RemoveTodo =>
      persist(event = TodoRemoved(cmd.id)) { persistedEvent =>
        updateState(persistedEvent)

        sender() ! Success()

        context.system.eventStream.publish(ProcessedEvent(persistedEvent, lastSequenceNr))
      }
  }

  override def receiveRecover: Receive = updateState

}

object TodoRepositoryProcessor {
  val name: String = "todo-repository-processor"

  def props(): Props = {
    Props(
      classOf[TodoRepositoryProcessor]
    )
  }

}
