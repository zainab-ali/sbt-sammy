package sammy

import sbt._
import sbt.Keys._
import sbt.internal.inc.Analysis
import xsbti.compile.CompileAnalysis

object SammyPlugin extends AutoPlugin {

  object autoImport {
    val sammyWarningThreshold = settingKey[Int]("The maximum number of warnings your project is allowed to have")
    val sammyWarningThresholdFile =
      settingKey[Option[File]]("The file to write the warning threshold to.  This should be empty if you don't want to update warnings.")
    val policeWarnings = taskKey[Unit](
      "Check that the number of warnings is lower than the configured threshold.  Writes the new threshold to the warning file, if desired")
  }

  import autoImport._

  override def trigger = allRequirements
  override def requires = empty

  lazy val baseSammySettings: Seq[Def.Setting[_]] = Seq(
    sammyWarningThresholdFile := Some(baseDirectory.value / "sammy.sbt"),
    policeWarnings := Sammy.policeWarningsTask.value,
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

  lazy val policeWarningsTask = Def.task {
    val log = streams.value.log

    val threshold = sammyWarningThreshold.value

    val compileAnalysis = (Compile / compile).value
    val testAnalysis = (Test / compile).value
    val count = warningCount(compileAnalysis) + warningCount(testAnalysis)

    log.info(s"Detected $count compiler warnings")

    count compare threshold match {
      case 0 =>
      case n if n > 0 =>
        throw new WarningThresholdExceededException(count, threshold)
      case n if n < 0 =>
        val removed = threshold - count
        log.info(
          s"Thanks! You have removed $removed warnings from the codebase. Have a cup of tea for a job well done:")
        log.info(cupOfTea)

        sammyWarningThresholdFile.value.foreach { file =>
          log.info(s"Setting new warning threshold to $count")
          writeWarningThreshold(file, count)
        }
    }
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
      s"warningThreshold in ThisBuild := $warningThreshold"
    IO.writeLines(file, Seq(warningThresholdString))
  }
}
