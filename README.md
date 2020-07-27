# sbt-sammy

[![Build Status](https://travis-ci.org/zainab-ali/sbt-sammy.svg?branch=master)](https://travis-ci.org/zainab-ali/sbt-sammy)

[![Download](https://api.bintray.com/packages/zainab-ali/sbt-plugins/sbt-sammy/images/download.svg)](https://bintray.com/zainab-ali/sbt-plugins/sbt-sammy/_latestVersion)

A friendly policeman to help reduce your compiler warnings.

> Tomorrow the sun will come up again, and I'm pretty sure that whatever happens we won't have found Freedom, and there won't be a whole lot of Justice, and I'm damn sure we won't have found Truth. But it's just possible that I might get a hard-boiled egg.
> -- Sam Vimes, Night Watch by Terry Pratchett

In a perfect world, all projects would start off life with [@tpolecat's scala compiler options](https://tpolecat.github.io/2017/04/25/scalac-flags.html) and we would never encounter compiler warnings.  In reality, we have to delve into mature, imperfect codebases where the lofty heights of `Xfatal-warnings` are difficult to achieve.  We have to take a pragmatic approach to cleaning these up.

`sbt-sammy` prevents you from making your codebase worse by enforcing a warning threshold.  This threshold decreases whenever you make improvements, nudging you to take small steps to `-Xfatal-warnings`.

# To use

This plugin requires sbt 1.0+.

Add this to your `project/plugins.sbt` or as a global plugin in `~/.sbt/1.0/plugins/plugins.sbt`:

```scala
addSbtPlugin("com.github.zainab-ali" % "sbt-sammy" % version)
```

Where `version` is set to the latest version of sbt-sammy

# Setup

## With a floating threshold (recommended)

`sbt-sammy` can reduce its warning threshold to the maximum number of warnings in your codebase.  This means that each time you make a positive change, you can reduce your warning threshold.

If you want to do this, simply run `sbt policeWarnings`. The warning threshold should decrease to the maximum number of warnings in your project.

## With a fixed threshold

If you would like to enforce a fixed threshold that `sbt-sammy` won't reduce then add the following to your `build.sbt`:

```scala
sammyWarningThresholdFile := None
sammyWarningThreshold := yourFixedThreshold
```

## With suggestions

`sbt-sammy` is geared towards incremental fixes, and can suggest files to be fixed.  It does this by diffing the files with warnings with those in a changeset. You need to supply it with a `sammyDiffCommand` that generates a list of changed files.

For example, if you would like to look for changes between the current branch and the master:

```scala
sammyDiffCommand := Some("git diff --name-only master...HEAD")
```

# Run

Run `sbt policeWarnings`.  This task will fail if the number of warnings exceeds the threshold.
