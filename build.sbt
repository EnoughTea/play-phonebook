import sbt.Keys._

// # Play configuration
// A router that will declare all the controllers that it routes to as dependencies,
// allowing controllers to be dependency injected themselves.
// When using the injected routes generator, prefixing the action with an @ symbol takes on a special meaning,
// it means instead of the controller being injected directly, a Provider of the controller will be injected.
routesGenerator := InjectedRoutesGenerator


// # Declated dependencies
// ## Common

// Convert objects to/from string using marked functions.
val jodaConvertVersion = "1.8"
val jodaConvert = "org.joda" % "joda-convert" % jodaConvertVersion

// Supports JSON-formatted logs.
val logbackJsonEncoderVersion = "4.11"
val logbackJsonEncoder = "net.logstash.logback" % "logstash-logback-encoder" % logbackJsonEncoderVersion

// Provides nameOf macro similiar to C# 'nameof' operator, after import com.github.dwickern.macros.NameOf._
// Examples at https://github.com/dwickern/scala-nameof
val scalaNameOfVersion = "1.0.3"
val scalaNameOf = "com.github.dwickern" %% "scala-nameof" % scalaNameOfVersion % Provided

// URI-related DSL, examples at https://github.com/lemonlabsuk/scala-uri:
// val uri = "http://theon.github.com/scala-uri" ? ("p1" -> "one") & ("p2" -> 2) & ("p3" -> true)
// uri.toString would result in: http://theon.github.com/scala-uri?p1=one&p2=2&p3=true
val scalaUriVersion = "0.4.16"
val scalaUri = "io.lemonlabs" %% "scala-uri" % scalaUriVersion

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
val commonDependencies = Seq(jodaConvert, logbackJsonEncoder, scalaGuice, scalaNameOf, scalaUri)
val testDependencies = Seq(scalaTestPlus)
val dbDependencies = Seq(slick, playSlick, playSlickEvolutions, h2Database)
val playModules = Seq(ehcache, guice, ws)


//////////////////////////////////////
// # Play phonebook project itself ///
//////////////////////////////////////
lazy val playPhonebook = Project(id = "play-phonebook", base = file("."))
  .enablePlugins(Common, PlayScala)
  .settings(libraryDependencies ++= Seq(commonDependencies, dbDependencies, testDependencies, playModules).flatten)