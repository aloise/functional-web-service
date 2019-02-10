name := "functional-web-service"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-Xplugin-require:macroparadise"
)

resolvers += Resolver.sonatypeRepo("releases")

lazy val models = (project in file("modules/models")).settings(
  libraryDependencies := Dependencies.Common
)

lazy val services = (project in file("modules/services"))
  .dependsOn(models)
  .settings(
    libraryDependencies := Dependencies.Services
  )

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin, JavaServerAppPackaging, GraalVMNativeImagePlugin)
  .dependsOn(services)
  .settings(
    graalVMNativeImageOptions ++= Seq("-da"),
    libraryDependencies ++= Dependencies.Server,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, "revision" -> scala.sys.process.Process("git rev-parse HEAD").!!.trim),
    buildInfoPackage := "name.aloise.build"
  )

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full)
