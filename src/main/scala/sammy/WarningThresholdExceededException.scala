package sammy

final case class WarningThresholdExceededException(numberOfWarnings: Int,
                                                   threshold: Int)
    extends Exception {
  override def toString: String =
    s"The number of warnings has increased from $threshold to $numberOfWarnings.  Please reduce warnings in your project."
}
