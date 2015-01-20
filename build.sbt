import AssemblyKeys._

name := "key-service"

lazy val buildSettings = Seq(
  organization := "com.blinkbox.books.quartermaster",
  version := scala.util.Try(scala.io.Source.fromFile("VERSION").mkString.trim).getOrElse("0.0.0"),
  scalaVersion := "2.11.4",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-target:jvm-1.7")
)

lazy val root = (project in file(".")).
  dependsOn(public, admin).aggregate(public, admin).
  settings(buildSettings: _*).
  settings(publish := {})

lazy val public = (project in file("public")).
  dependsOn(common % "compile->compile;test->test").aggregate(common).
  settings(aggregate in publish := false).
  settings(buildSettings: _*).
  settings(rpmPrepSettings: _*)

lazy val admin = (project in file("admin")).
  dependsOn(common % "compile->compile;test->test").aggregate(common).
  settings(aggregate in publish := false).
  settings(buildSettings: _*).
  settings(rpmPrepSettings: _*)

lazy val common = (project in file("common")).settings(buildSettings: _*)