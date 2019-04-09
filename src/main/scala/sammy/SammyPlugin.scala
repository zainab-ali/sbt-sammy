package sammy

import sbt._
import sbt.Keys._
import sbt.internal.inc.Analysis
import xsbti.compile.CompileAnalysis

object SammyPlugin extends AutoPlugin {

  object autoImport {
    val sammyWarningThreshold = settingKey[Int](
      "The maximum number of warnings your project is allowed to have")
    val sammyWarningThresholdFile =
      settingKey[Option[File]](
        "The file to write the warning threshold to.  This should be empty if you don't want to update warnings.")
  }

  import autoImport._

  override def trigger = allRequirements
  override def requires = empty

  lazy val baseSammySettings: Seq[Def.Setting[_]] = Seq(
    sammyWarningThreshold in ThisBuild := Int.MaxValue,
    sammyWarningThresholdFile in ThisBuild := Some(
      baseDirectory.value / "sammy.sbt"),
    commands in ThisBuild += Sammy.policeWarningsCommand
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseSammySettings
}

object Sammy {

  import SammyPlugin.autoImport._

  lazy val cupOfTea: String = """
         ((
    _..,--))-,.._
 .-;'-.,_____,.-';
(( |             |
 `)|             '
  ` \           /
 .-' `,._____.,''-.
(     '-------'     )
 `--.._________..--'

"""

  lazy val policeWarningsCommandName: String = "policeWarnings"
  lazy val policeWarningsBriefHelp: String =
    "Checks that the compiler warning threshold is met"
  lazy val policeWarningsDetail: String = s"""
$policeWarningsCommandName

         Checks that the number of warnings produced by the compile and test tasks does not exceed the value of the sammyWarningThreshold setting.
         If the number of warnings has been reduced, the sammyWarningThreshold is reduced to equal it.
         If the sammyWarningThresholdFile setting has been provided, the new sammyWarningThreshold is also persisted to a file
"""

  lazy val policeWarningsCommand = Command.command(
    policeWarningsCommandName,
    policeWarningsBriefHelp,
    policeWarningsDetail
  )(policeWarnings)

  private def policeWarnings: State => State = { state =>
    val log = state.log

    val extracted: Extracted = Project.extract(state)
    val thresholdFile = extracted.get(sammyWarningThresholdFile)
    val threshold = extracted.get(sammyWarningThreshold)
    val (state0, compileAnalysis) = runTask(compile in Compile)(state)
    val (state1, testAnalysis) = runTask(compile in Test)(state0)

    val analyses = List(compileAnalysis, testAnalysis)
    val count = analyses.map(warningCount).sum

    log.info(s"Detected $count compiler warnings")

    count compare threshold match {
      case 0 =>
        log.info("The number of compiler warnings remains the same")
        state
      case n if n > 0 =>
        log.error(
          s"The number of warnings has increased from $threshold to $count.  Please reduce warnings in your project.")
        state.fail
      case n if n < 0 =>
        val removed = threshold - count
        log.info(
          s"Thanks! You have removed $removed warnings from the codebase. Have a cup of tea for a job well done:")
        log.info(cupOfTea)

        thresholdFile.foreach { file =>
          log.info(s"Setting new warning threshold to $count")
          writeWarningThreshold(file, count)
        }
        updateWarningThresholdSetting(count)(state1)
    }
  }

  /** Run an sbt task */
  private def runTask[A](task: TaskKey[A])(state: State): (State, A) = {
    val extracted: Extracted = Project.extract(state)
    extracted.runTask(task, state)
  }

  /** Counts the number of warnings produced by a compile analysis stage */
  private def warningCount(compileAnalysis: CompileAnalysis): Int = {
    val analysis = compileAnalysis match {
      case a: Analysis => a
    }
    analysis.infos.allInfos.values
      .flatMap(i => i.getReportedProblems ++ i.getUnreportedProblems)
      .size
  }

  /** Writes the sbt warning threshold setting to a file */
  private def writeWarningThreshold(file: File, warningThreshold: Int): Unit = {
    val warningThresholdString =
      s"sammyWarningThreshold in ThisBuild := $warningThreshold"
    IO.writeLines(file, Seq(warningThresholdString))
  }

  /** Updates the warning threshold setting */
  private def updateWarningThresholdSetting(
      warningThreshold: Int): State => State = { state =>
    val extracted = Project extract state
    extracted.appendWithSession(Seq(sammyWarningThreshold := warningThreshold),
                                state)
  }
}
