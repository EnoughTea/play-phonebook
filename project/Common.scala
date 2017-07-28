import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/** Settings that are common to all the SBT projects */
object Common extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.entea",
    version := "1.0-SNAPSHOT",
    resolvers ++= Seq(
      Resolver.typesafeRepo("releases"),
      Resolver.sonatypeRepo("releases"),
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("scalaz-bintray", "releases")
    ),
    scalaVersion := "2.12.2",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      //"-opt:l:project",
      "-encoding", "UTF-8",
      "-target:jvm-1.8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfuture"
    ),

    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true
  )
}
