package com.todos.protobuf

import akka.serialization.SerializerWithStringManifest
import com.todos.event.event.{TodoCreatedEvt, TodoRemovedEvt}
import com.todos.event.{TodoCreated, TodoRemoved}
import com.todos.model.TodoRegistry
import com.todos.model.model.TodoRegistryMdl

class TodoSerializer extends SerializerWithStringManifest
  with TodoToProtobufTransformers with TodoFromProtobufTransformers {

  // should be unique, check other serializers before assigning
  def identifier: Int = 256

  def manifest(o: AnyRef): String = {
    o match {
      case _: TodoRegistry => TodoRegistry.serializeId
      case _: TodoCreated => TodoCreated.serializeId
      case _: TodoRemoved => TodoRemoved.serializeId
    }
  }

  def toBinary(o: AnyRef): Array[Byte] = {
    o match {
      case o: TodoRegistry => toProtobuf(o).toByteArray
      case o: TodoCreated => toProtobuf(o).toByteArray
      case o: TodoRemoved => toProtobuf(o).toByteArray
    }
  }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    manifest match {
      case TodoRegistry.serializeId => fromProtobuf(TodoRegistryMdl.parseFrom(bytes))
      case TodoCreated.serializeId => fromProtobuf(TodoCreatedEvt.parseFrom(bytes))
      case TodoRemoved.serializeId => fromProtobuf(TodoRemovedEvt.parseFrom(bytes))
    }
  }
}
