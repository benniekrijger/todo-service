package com.todos.protobuf

import java.util.UUID

import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.event.event.{TodoCreatedEvt, TodoRemovedEvt}
import com.todos.model.{Todo, TodoRegistry}
import com.todos.model.model.{TodoMdl, TodoRegistryMdl}

trait TodoFromProtobufTransformers {

  def fromProtobuf(msg: TodoMdl): Todo = {
    for {
      id <- msg.id.map(UUID.fromString)
      title <- msg.title
      completed <- msg.completed
    } yield Todo(
      id = id,
      title = title,
      completed = completed
    )
  }.getOrElse(throw new RuntimeException(s"Unable to deserialize, msg=$msg"))

  def fromProtobuf(msg: TodoRegistryMdl): TodoRegistry = {
    TodoRegistry(
      todos = msg.todos.map(fromProtobuf),
      messageOffset = msg.messageOffset
    )
  }

  def fromProtobuf(msg: TodoCreatedEvt): TodoCreated = {
    for {
      id <- msg.id.map(UUID.fromString)
      title <- msg.title
      completed <- msg.completed
    } yield TodoCreated(
      id = id,
      title = title,
      completed = completed
    )
  }.getOrElse(throw new RuntimeException(s"Unable to deserialize, msg=$msg"))

  def fromProtobuf(msg: TodoRemovedEvt): TodoRemoved = {
    for {
      id <- msg.id.map(UUID.fromString)
    } yield TodoRemoved(id = id)
  }.getOrElse(throw new RuntimeException(s"Unable to deserialize, msg=$msg"))

}
