import java.util.HashMap

import scala.collection.convert.WrapAsScala.mapAsScalaMap

object Test {
  def main(args: Array[String]): Unit =
    new ProcessBuilder("ls", "/").start()
}
