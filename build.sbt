enablePlugins(JavaAppPackaging)

name := "LibrarySystem"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.2.20",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3"
)

mainClass in (Compile, run) := Some("LibrarySystem")
