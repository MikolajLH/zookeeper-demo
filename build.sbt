ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.1"

libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.9.3"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.17"

lazy val root = (project in file("."))
  .settings(
    name := "zookeeper-demo"
  )
