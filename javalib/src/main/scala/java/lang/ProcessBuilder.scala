package java.lang

import java.util.List
import java.util.Map
import java.io.{File, IOException}
import java.util
import java.util.Arrays
import ProcessBuilder.Redirect

final class ProcessBuilder(private var _command: List[String]) {

  private var _directory: File = new File(userDirectory)

  private var _env: util.Map[String, String] = new util.HashMap(System.getenv())
  private var _redirectErrorStream: Boolean = false

  private var _in: Redirect = Redirect.PIPE
  private var _out: Redirect = Redirect.PIPE
  private var _err: Redirect = Redirect.PIPE

  def this(command: Array[String]) = {
    this(Arrays.asList(command))
  }

  def command(): List[String] = _command

  def command(command: Array[String]): ProcessBuilder = {
    _command = Arrays.asList(command)
    this
  }

  def command(command: List[String]): ProcessBuilder = {
    _command = command
    this
  }

  def environment(): Map[String, String] = _env

  def directory(): File = _directory

  def directory(directory: File): ProcessBuilder = {
    if (directory == null) {
      _directory = new File(userDirectory)
    } else {
      _directory = directory
    }
    this
  }

  def redirectInput(source: Redirect): ProcessBuilder = {
    if (source == null) {
      throw new NullPointerException()
    }
    if (source.`type`() == Redirect.Type.WRITE || source.`type`() == Redirect.Type.APPEND) {
      throw new IllegalArgumentException()
    }
    _in = source
    this
  }

  def redirectOutput(destination: Redirect): ProcessBuilder = {
    if (destination == null) {
      throw new NullPointerException()
    }
    if (destination.`type`() == Redirect.Type.READ) {
      throw new IllegalArgumentException()
    }
    _out = destination
    this
  }

  def redirectError(destination: Redirect): ProcessBuilder = {
    if (destination == null) {
      throw new NullPointerException()
    }
    if (destination.`type`() == Redirect.Type.READ) {
      throw new IllegalArgumentException()
    }
    _err = destination
    this
  }

  def redirectInput(file: File): ProcessBuilder = {
    redirectInput(Redirect.from(file))
  }

  def redirectOutput(file: File): ProcessBuilder = {
    redirectOutput(Redirect.to(file))
  }

  def redirectError(file: File): ProcessBuilder = {
    redirectError(Redirect.to(file))
  }

  def redirectInput(): Redirect = _in

  def redirectOutput(): Redirect = _out

  def redirectError(): Redirect = _err

  def inheritIO(): ProcessBuilder = {
    redirectInput(Redirect.INHERIT)
    redirectOutput(Redirect.INHERIT)
    redirectError(Redirect.INHERIT)
  }

  def redirectErrorStream(): Boolean = _redirectErrorStream

  def redirectErrorStream(redirectErrorStream: Boolean): ProcessBuilder = {
    _redirectErrorStream = redirectErrorStream
    this
  }

  def start(): Process = {
    if (command().isEmpty()) throw new IndexOutOfBoundsException()
    if (command().contains(null)) throw new NullPointerException()
    ProcessImpl(command(), environment(), userDirectory, redirectOutput(), redirectError(), redirectInput(), redirectErrorStream())
  }

  private def userDirectory = {
    Option(System.getProperty("user.dir")).getOrElse("")
  }
}

object ProcessBuilder {

  abstract class Redirect {
    def file(): File = null

    def `type`(): Redirect.Type

    override def equals(other: Any): scala.Boolean = other match {
      case that: Redirect =>
        file() == that.file() &&
          `type`() == that.`type`()
      case _ => false
    }

    override def hashCode(): Int = {
      var hash = 1
      hash = hash * 31 + file().hashCode()
      hash = hash * 31 + `type`().hashCode()
      hash
    }
  }

  object Redirect {

    val INHERIT: Redirect = new Redirect {
      override def `type`(): Type = Type.INHERIT
    }
    val PIPE: Redirect = new Redirect {
      override def `type`(): Type = Type.PIPE
    }

    def appendTo(_file: File): Redirect = {
      if (_file == null) throw new NullPointerException()
      new Redirect {
        override def `type`(): Type = Type.APPEND

        override def file(): File = _file
      }
    }

    def from(_file: File): Redirect = {
      if (_file == null) throw new NullPointerException()
      new Redirect {
        override def `type`(): Type = Type.READ

        override def file(): File = _file
      }
    }

    def to(_file: File): Redirect = {
      if (_file == null) throw new NullPointerException()
      new Redirect {
        override def `type`(): Type = Type.WRITE

        override def file(): File = _file
      }
    }

    class Type private(name: String, ordinal: Int) extends Enum[Type](name, ordinal)

    object Type {
      final val PIPE = new Type("PIPE", 0)
      final val INHERIT = new Type("INHERIT", 1)
      final val READ = new Type("READ", 2)
      final val WRITE = new Type("WRITE", 3)
      final val APPEND = new Type("APPEND", 4)

      def valueOf(name: String): Type = {
        if (name == null) throw new NullPointerException()
        val maybeValue = Seq(_values: _*).find(v => name.equals(v.name()))
        maybeValue.getOrElse(throw new IllegalArgumentException(s"$name is not a valid Type name"))
      }

      def values(): Array[Type] = _values.toArray

      private[this] val _values =
        Seq(PIPE, INHERIT, READ, WRITE, APPEND)
    }

  }

}
