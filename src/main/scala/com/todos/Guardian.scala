package com.todos

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.routing.RoundRobinPool
import com.todos.repository.{TodoRepositoryProcessor, TodoRepositoryView}

class Guardian() extends Actor with ActorLogging {
  log.info("TodoService up and running...")

  val todoRepositoryProcessor: ActorRef = context.actorOf(
    TodoRepositoryProcessor.props(),
    name = TodoRepositoryProcessor.name
  )

  val readJournal: CassandraReadJournal = PersistenceQuery(context.system)
    .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  val todoRepositoryView: ActorRef = context.actorOf(
    TodoRepositoryView.props(readJournal).withRouter(RoundRobinPool(5)),
    name = TodoRepositoryView.name
  )

  context.actorOf(
    Api.props(
      todoRepositoryViewPath = todoRepositoryView.path,
      todoRepositoryProcessorPath = todoRepositoryProcessor.path
    ),
    name = Api.name
  )

  def receive: Receive = Actor.emptyBehavior
}

object Guardian {
  val name: String = "guardian"

  def props(): Props = {
    Props(
      classOf[Guardian]
    )
  }

}
