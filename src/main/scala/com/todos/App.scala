package com.todos

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

object App {

  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()

    val system = ActorSystem("todo-service", config)

    system.actorOf(
      Guardian.props(),
      name = Guardian.name
    )
  }

}