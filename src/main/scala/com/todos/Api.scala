package com.todos

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorPath, ActorSelection, Props, Status}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route, RouteConcatenation}
import akka.pattern.{pipe, _}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.todos.command.{CreateTodo, RemoveTodo}
import com.todos.query.{FindTodo, FindTodos}
import com.todos.response.{NotFound, Success, TodoView, TodosView}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.jackson._
import org.json4s.{Formats, NoTypeHints, Serialization, jackson}

import scala.concurrent.duration._

class Api(
    todoRepositoryViewPath: ActorPath,
    todoRepositoryProcessorPath: ActorPath
) extends Actor with ActorLogging
  with Json4sSupport with RouteConcatenation with Directives {
  import context.dispatcher

  log.info("Api up and running...")

  implicit lazy val formats: Formats = Serialization.formats(NoTypeHints) ++ org.json4s.ext.JavaTypesSerializers.all
  implicit val jacksonSerialization: Serialization = jackson.Serialization
  implicit val timeout = Timeout(3.seconds)
  implicit val mat = ActorMaterializer()

  def todoRepositoryView: ActorSelection = context.actorSelection(todoRepositoryViewPath)
  def todoRepositoryProcessor: ActorSelection = context.actorSelection(todoRepositoryProcessorPath)

  val routes: Route =
    pathPrefix("api" / "v1") {
      cors() {
        pathPrefix("todos") {
          pathEnd {
            get {
              parameters("offset".as[Int].?, "limit".as[Int].?) { (offset, limit) =>
                onSuccess(
                  todoRepositoryView ? FindTodos(
                    offset = offset.getOrElse(0),
                    limit = limit.getOrElse(100)
                  )
                ) {
                  case resp: TodosView =>
                    complete(StatusCodes.OK, resp)
                  case _ =>
                    complete(StatusCodes.BadRequest)
                }
              }
            } ~
              post {
                entity(as[CreateTodo]) { command =>
                  onSuccess(todoRepositoryProcessor ? command) {
                    case _: Success =>
                      complete(StatusCodes.OK)
                    case _ =>
                      complete(StatusCodes.BadRequest)
                  }
                }
              }
          } ~
            path(JavaUUID) { id =>
              get {
                onSuccess(todoRepositoryView ? FindTodo(id)) {
                  case resp: TodoView =>
                    complete(StatusCodes.OK, resp)
                  case _: NotFound =>
                    complete(StatusCodes.NotFound)
                  case _ =>
                    complete(StatusCodes.BadRequest)
                }
              } ~
                delete {
                  onSuccess(todoRepositoryProcessor ? RemoveTodo(id)) {
                    case _: Success =>
                      complete(StatusCodes.OK)
                    case _ =>
                      complete(StatusCodes.BadRequest)
                  }
                }
            }
        }
      }
    }

  Http(context.system)
    .bindAndHandle(routes, "0.0.0.0", 8080)
    .pipeTo(self)

  def receive: Receive = {
    case Http.ServerBinding(a) => handleBinding(a)
    case Status.Failure(c)     => handleBindFailure(c)
  }

  private def handleBinding(address: InetSocketAddress) = {
    log.info("Listening on {}", address)
    context.become(Actor.emptyBehavior)
  }

  private def handleBindFailure(cause: Throwable) = {
    log.error(cause, "Can't bind to 0.0.0.0:8080!")
    context.stop(self)
  }

}

object Api {
  val name: String = "api"

  def props(
    todoRepositoryViewPath: ActorPath,
    todoRepositoryProcessorPath: ActorPath
  ): Props = {
    Props(
      classOf[Api],
      todoRepositoryViewPath,
      todoRepositoryProcessorPath
    )
  }

}