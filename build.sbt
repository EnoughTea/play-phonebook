import sbt.Keys._

// # Play configuration
// A router that will declare all the controllers that it routes to as dependencies,
// allowing controllers to be dependency injected themselves.
// When using the injected routes generator, prefixing the action with an @ symbol takes on a special meaning,
// it means instead of the controller being injected directly, a Provider of the controller will be injected.
routesGenerator := InjectedRoutesGenerator


// # Declared dependencies

// ## Common
// Supports JSON-formatted logs.
val logbackJsonEncoderVersion = "4.11"
val logbackJsonEncoder = "net.logstash.logback" % "logstash-logback-encoder" % logbackJsonEncoderVersion

// DI framework.
val scalaGuiceVersion = "4.1.0"
val scalaGuice = "net.codingwell" %% "scala-guice" % scalaGuiceVersion


// ## DB-related
val slickVersion = "3.2.0"
val slick = "com.typesafe.slick" %% "slick" % slickVersion

val playSlickVersion = "3.0.0"
val playSlick = "com.typesafe.play" %% "play-slick" % playSlickVersion
val playSlickEvolutions = "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion

val h2DatabaseVersion = "1.4.196"
val h2Database = "com.h2database" % "h2" % h2DatabaseVersion


// ## Test frameworks
val scalaTestPlusVersion = "3.0.0"
val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % Test


// # Dependencies conveniently sorted.
val commonDependencies = Seq(logbackJsonEncoder, scalaGuice)
val testDependencies = Seq(scalaTestPlus)
val dbDependencies = Seq(slick, playSlick, playSlickEvolutions, h2Database)
val playModules = Seq(ehcache, guice, ws)


//////////////////////////////////////
// # Play phonebook project itself ///
//////////////////////////////////////
lazy val playPhonebook = Project(id = "play-phonebook", base = file("."))
  .enablePlugins(Common, PlayScala)
  .settings(libraryDependencies ++= Seq(commonDependencies, dbDependencies, testDependencies, playModules).flatten)