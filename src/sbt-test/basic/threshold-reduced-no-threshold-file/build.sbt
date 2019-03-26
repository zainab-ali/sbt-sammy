scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
)

sammyWarningThreshold  := 5
sammyWarningThresholdFile := None
