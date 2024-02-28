scalaVersion := "2.12.19"

scalacOptions ++= Seq(
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
)

val IntegrationTest = config("it") extend(Test)

settings ++= inConfig(IntegrationTest)(Defaults.testSettings) ++
    Seq(scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala")
