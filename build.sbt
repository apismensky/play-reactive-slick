name := """play-reactive-slick"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

javaOptions in Test ++= Seq("-Dconfig.file=conf/test.conf")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  cache,
  ws,
  "com.typesafe.slick" %% "slick"      % "3.0.0",
  "org.slf4j"           % "slf4j-nop"  % "1.6.4",
  "postgresql"          % "postgresql" % "9.1-901.jdbc4",
  specs2 % Test,
  "org.scalatest"       % "scalatest_2.11" % "2.2.4" % Test,
  javaWs % Test
)
