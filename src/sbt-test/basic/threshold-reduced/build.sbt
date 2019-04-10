scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
)

val check = taskKey[Unit]("Check that the value of sammyWarningThreshold has been modified")

check := {
  val expected = 3
  val actual = sammyWarningThreshold.value
  assert(expected == actual, "Expected warning threshold of " + expected + ", got " + actual)
}
