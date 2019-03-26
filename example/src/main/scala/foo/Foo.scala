package foo

// Picked up by "-Ywarn-unused:imports"
import scala.concurrent.Future

object Foo {

  // Picked up by "-Ywarn-unused:privates" and "-Ywarn-unused:params"
  private def unusedPrivateFunction(unusedParameter: Int): Unit = {
  }
}
