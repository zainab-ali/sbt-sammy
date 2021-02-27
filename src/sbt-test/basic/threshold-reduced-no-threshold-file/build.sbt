scalaVersion := "2.12.13"

scalacOptions ++= Seq(
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
)

sammyWarningThreshold  := 5
sammyWarningThresholdFile := None

val check = taskKey[Unit]("Check that the value of sammyWarningThreshold has been modified")

check := {
  val expected = 3
  val actual = sammyWarningThreshold.value
  assert(expected == actual, "Expected warning threshold of " + expected + ", got " + actual)
}
