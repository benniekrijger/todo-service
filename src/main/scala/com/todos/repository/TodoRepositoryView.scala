package com.todos.repository

import akka.Done
import akka.actor.{ActorLogging, Props, Stash}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.todos.event.utils.ProcessedEvent
import com.todos.model.TodoRegistry
import com.todos.query.{FindTodo, FindTodos}
import com.todos.response.{NotFound, TodoView, TodosView}
import akka.pattern.pipe

import scala.util.Random

class TodoRepositoryView() extends PersistentActor with ActorLogging with TodoRepositoryUpdater with Stash {
  implicit val mat = ActorMaterializer()

  def persistenceId: String = TodoRepositoryView.name + "-" + self.path.name

  log.info("Started {}", persistenceId)

  var state: TodoRegistry = TodoRegistry.empty()

  context.system.eventStream.subscribe(self, classOf[ProcessedEvent])

  def receiveCommand: Receive = recovering

  // the random parts prevents all views snapshotting at the same time
  lazy val snapshotInterval: Int = 10 + Random.nextInt(20)

  def recovering: Receive = {
    case Done =>
      context.become(running)
      unstashAll()
    case _ => stash()
  }

  def running: Receive = {
    case evt: ProcessedEvent =>
      updateState(evt.event)

      if (evt.sequenceNr % snapshotInterval == 0) {
        log.debug("Snapshotting state, state={}, sequenceNr={}", state, evt.sequenceNr)

        saveSnapshot(
          state.copy(
            messageOffset = Some(evt.sequenceNr)
          )
        )
      }
    case qry: FindTodos =>
      val todoViews = state.todos
        .slice(qry.offset, qry.offset + qry.limit)
        .map { todo =>
          TodoView(
            id = todo.id,
            title = todo.title,
            completed = todo.completed
          )
        }

      sender() ! TodosView(
        todos = todoViews,
        total = state.todos.size
      )
    case qry: FindTodo =>
      state.todosById.get(qry.id) match {
        case Some(todo) =>
          val todoView = TodoView(
            id = todo.id,
            title = todo.title,
            completed = todo.completed
          )

          sender() ! todoView
        case _ =>
          sender() ! NotFound()
      }
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: TodoRegistry) =>
      log.info("Received snapshot, snapshot={}", snapshot)
      state = snapshot
    case _: RecoveryCompleted => // no action
      import context.dispatcher

      log.info("Recovered, state={}", state)

      PersistenceQuery(context.system)
        .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
        .currentEventsByPersistenceId(
          persistenceId = TodoRepositoryProcessor.name,
          fromSequenceNr = state.messageOffset.getOrElse(Long.MinValue),
          toSequenceNr = Long.MaxValue
        )
        .map { envelope =>
          log.debug("Replaying event, event={}, offset={}", envelope.event.getClass.getCanonicalName, envelope.offset)

          updateState(envelope.event)
        }
        .runWith(Sink.ignore)
        .pipeTo(self)
  }

}

object TodoRepositoryView {
  val name: String = "todo-repository-view"

  def props(): Props = {
    Props(
      classOf[TodoRepositoryView]
    )
  }
}
