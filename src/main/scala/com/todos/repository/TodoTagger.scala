package com.todos.repository

import akka.persistence.journal.{Tagged, WriteEventAdapter}

class TodoTagger extends WriteEventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Tagged = event match {
    case evt => Tagged(evt, Set("todo"))
  }
}

