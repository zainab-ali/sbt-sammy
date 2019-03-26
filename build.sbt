enablePlugins(SbtPlugin)

scalaVersion := "2.12.8"
organization := "com.github.zainab-ali"
name := "sbt-sammy"
description := "A friendly policer of compiler warnings"
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq("-Dplugin.version=" + version.value)
