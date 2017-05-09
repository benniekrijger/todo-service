import sbt._

object  Dependencies {
  object Version {
    val akka = "2.5.1"
    val akkaHttp = "10.0.6"
    val akkaHttpCors = "0.2.1"
    val akkaHttpJson4s = "1.15.0"
    val log4j = "2.8.2"
    val akkaLog4j = "1.4.0"
    val scalaTest = "3.0.2"
    val json4s = "3.5.2"
    val jodaTime = "2.9.9"
    val jodaConvert = "1.8.1"
    val ficus = "1.4.0"
    val akkaPersistenceCassandra = "0.52"
    val log4jOverSlf4j = "1.7.25"
  }

  private[this] lazy val tests = Seq(
    "org.scalatest"                       %% "scalatest"                          % Version.scalaTest       % "test",
    "com.typesafe.akka"                   %% "akka-testkit"                       % Version.akka            % "test",
    "org.slf4j"                           %  "log4j-over-slf4j"                   % Version.log4jOverSlf4j  % "test"
  )

  lazy val base = Seq(
    "joda-time"                           %  "joda-time"                          % Version.jodaTime,
    "org.joda"                            %  "joda-convert"                       % Version.jodaConvert,
    "com.typesafe.akka"                   %% "akka-actor"                         % Version.akka,
    "org.apache.logging.log4j"            %  "log4j-core"                         % Version.log4j,
    "org.apache.logging.log4j"            %  "log4j-slf4j-impl"                   % Version.log4j,
    "de.heikoseeberger"                   %% "akka-log4j"                         % Version.akkaLog4j,
    "com.iheart"                          %% "ficus"                              % Version.ficus
  )

  private[this] lazy val api = Seq(
    "com.typesafe.akka"                   %% "akka-http"                          % Version.akkaHttp,
    "com.typesafe.akka"                   %% "akka-http-testkit"                  % Version.akkaHttp,
    "de.heikoseeberger"                   %% "akka-http-json4s"                   % Version.akkaHttpJson4s,
    "org.json4s"                          %% "json4s-jackson"                     % Version.json4s,
    "org.json4s"                          %% "json4s-ext"                         % Version.json4s,
    "ch.megard"                           %% "akka-http-cors"                     % Version.akkaHttpCors
  )

  private[this] lazy val persistence = Seq(
    "com.typesafe.akka"                   %% "akka-persistence"                   % Version.akka,
    "com.typesafe.akka"                   %% "akka-persistence-cassandra"         % Version.akkaPersistenceCassandra
  )

  lazy val backend: Seq[ModuleID] = base ++ persistence ++ api ++ tests

}