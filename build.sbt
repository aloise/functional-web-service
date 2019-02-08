name := "functional-web-service"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ypartial-unification"
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
  "org.http4s" %% "http4s-dsl" % Versions.http4s,
  "org.http4s" %% "http4s-circe" % Versions.http4s,
  "io.circe" %% "circe-generic" % Versions.circe,
  "org.tpolecat" %% "doobie-core" % Versions.doobie,
  "org.tpolecat" %% "doobie-hikari" % Versions.doobie
)