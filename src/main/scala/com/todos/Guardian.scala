package com.todos

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.todos.repository.{TodoRepositoryProcessor, TodoRepositoryView}

class Guardian() extends Actor with ActorLogging {
  log.info("TodoService up and running...")

  val todoRepositoryProcessor: ActorRef = context.actorOf(
    TodoRepositoryProcessor.props(),
    name = TodoRepositoryProcessor.name
  )

  val todoRepositoryView: ActorRef = context.actorOf(
    TodoRepositoryView.props().withRouter(RoundRobinPool(5)),
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
