name := "circuit-fiber"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "2.2.0",
  "io.chrisdavenport" %% "circuit" % "0.4.3"
  )
addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
