name := "functional-web-service"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
  "org.http4s" %% "http4s-dsl" % Versions.http4s,
  "org.http4s" %% "http4s-circe" % Versions.http4s,
  "io.circe" %% "circe-generic" % Versions.circe,
  "org.tpolecat" %% "doobie-core" % Versions.doobie,
  "org.tpolecat" %% "doobie-hikari" % Versions.doobie,
  "io.frees" %% "frees-http4s" % Versions.frees,
  "io.frees" %% "frees-logging" % Versions.frees,
  "io.chrisdavenport" %% "log4cats-core" % Versions.log4cats,
  "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4cats,
  "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig
)



addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.8" cross CrossVersion.binary)

enablePlugins(JavaServerAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)

graalVMNativeImageOptions ++= Seq("-da")