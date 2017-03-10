package com.todos.protobuf

import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.event.event.{TodoCreatedEvt, TodoRemovedEvt}
import com.todos.model.{Todo, TodoRegistry}
import com.todos.model.model.{TodoMdl, TodoRegistryMdl}

trait TodoToProtobufTransformers {

  def toProtobuf(mod: Todo): TodoMdl = {
    TodoMdl(
      id = Some(mod.id.toString),
      title = Some(mod.title),
      completed = Some(mod.completed)
    )
  }

  def toProtobuf(mod: TodoRegistry): TodoRegistryMdl = {
    TodoRegistryMdl(
      todos = mod.todos.map(toProtobuf),
      messageOffset = mod.messageOffset
    )
  }

  def toProtobuf(mod: TodoCreated): TodoCreatedEvt = {
    TodoCreatedEvt(
      id = Some(mod.id.toString),
      title = Some(mod.title),
      completed = Some(mod.completed)
    )
  }

  def toProtobuf(mod: TodoRemoved): TodoRemovedEvt = TodoRemovedEvt(id = Some(mod.id.toString))

}
