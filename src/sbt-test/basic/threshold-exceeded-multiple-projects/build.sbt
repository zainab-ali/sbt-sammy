scalaVersion := "2.12.15"

lazy val foo = project
  .settings(
    scalacOptions += "-Ywarn-unused:imports"
  )

lazy val bar = project
  .settings(
    scalacOptions += "-Ywarn-unused:imports"
  )

lazy val root = (project in file("."))
  .aggregate(foo, bar)
