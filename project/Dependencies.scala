import sbt._

object Dependencies {

  val Common = Seq[ModuleID](
    "org.typelevel" %% "cats-core" % Versions.cats,
    "org.typelevel" %% "cats-free" % Versions.cats,
    "org.typelevel" %% "cats-effect" % Versions.catsEffects,
    "eu.timepit" %% "refined" % Versions.refined,
    "eu.timepit" %% "refined-cats" % Versions.refined,
    "org.typelevel" %% "cats-tagless-macros" % Versions.catsTagless
  )

  val Server = Common ++ Seq[ModuleID](
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
    "org.http4s" %% "http4s-dsl" % Versions.http4s,
    "org.http4s" %% "http4s-circe" % Versions.http4s,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-refined" % Versions.circe,
    "io.chrisdavenport" %% "log4cats-core" % Versions.log4cats,
    "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4cats,
    "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureconfig,
    "io.github.jmcardon" %% "tsec-common" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-password" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-cipher-jca" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-cipher-bouncy" % Versions.tsecV,
    "eu.timepit" %% "refined-pureconfig" % Versions.refined

  )

  val DB = Common ++ Seq[ModuleID](
    "org.tpolecat" %% "doobie-core" % Versions.doobie,
    // And add any of these as needed
    // "org.tpolecat" %% "doobie-h2"        % Versions.doobie,          // H2 driver 1.4.197 + type mappings.
    "org.tpolecat" %% "doobie-hikari"    % Versions.doobie,          // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres"  % Versions.doobie,
    "org.tpolecat" %% "doobie-refined"  % Versions.doobie
  )

  val Services = Common
}
