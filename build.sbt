import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

val baseSettings = Seq(
  scalaVersion := "2.12.1",
  resolvers ++= Seq(
    Resolver.jcenterRepo,
    Resolver.bintrayRepo("hseeberger", "maven")
  ),
  PB.targets in Compile := Seq(
    scalapb.gen() -> (sourceManaged in Compile).value
  )
)

lazy val `todos-service` = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(baseSettings:_*)
  .settings(
    name := "todo-service",
    // Use customer versioning per component with different environmental variables
    version in Docker := sys.env.getOrElse("SERVICE_VERSION", default = "v0.1"),
    dockerRepository := Some(sys.env.getOrElse("DOCKER_REPOSITORY", default = "benniekrijger")),
    dockerBaseImage := "java:8",
    libraryDependencies ++= Dependencies.backend,
    dockerExposedPorts := Seq(8080),
    cancelable in Global := true,
    fork in run := true
  )