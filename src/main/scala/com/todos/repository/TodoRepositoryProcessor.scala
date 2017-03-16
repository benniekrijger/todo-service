package com.todos.repository

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import akka.persistence.{PersistentActor, Recovery}
import com.todos.command.{CreateTodo, RemoveTodo}
import com.todos.event.utils.ProcessedEvent
import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.response.Success

class TodoRepositoryProcessor() extends PersistentActor with ActorLogging {
  log.info("Started {}", self.path.name)

  def persistenceId: String = self.path.name

  def receiveCommand: Receive = {
    case cmd: CreateTodo =>
      persist(
        event = TodoCreated(
          id = UUID.randomUUID(),
          title = cmd.title,
          completed = cmd.completed
        )
      ) { persistedEvent =>
        sender() ! Success()

        context.system.eventStream.publish(ProcessedEvent(persistedEvent, lastSequenceNr))
      }
    case cmd: RemoveTodo =>
      persist(event = TodoRemoved(cmd.id)) { persistedEvent =>
        sender() ! Success()

        context.system.eventStream.publish(ProcessedEvent(persistedEvent, lastSequenceNr))
      }
  }

  override def recovery: Recovery = Recovery.none

  override def receiveRecover: Receive = Actor.emptyBehavior

}

object TodoRepositoryProcessor {
  val name: String = "todo-repository-processor"

  def props(): Props = {
    Props(
      classOf[TodoRepositoryProcessor]
    )
  }

}
