package java.lang;

import java.io.InputStream
import java.io.OutputStream
import java.util
import java.util.concurrent.TimeUnit;

private class ProcessImpl() extends Process {

    {
        println()
    }

    override def destroy(): Unit = ???

    override def exitValue(): Int = ???

    override def getErrorStream(): InputStream = ???

    override def getInputStream(): InputStream = ???

    override def getOutputStream(): OutputStream = ???

    override def waitFor(): Int = ???

    override def waitFor(timeout: Long, unit: TimeUnit): Boolean = ???
}

object ProcessImpl {
    def apply(
               command: util.List[String],
               environment: util.Map[String, String],
               userDirectory: String,
               out: ProcessBuilder.Redirect,
               err: ProcessBuilder.Redirect,
               in: ProcessBuilder.Redirect,
               redirectErrorStream: Boolean): Process = {
        import scala.collection.convert.WrapAsScala._

        def filePath(redirect:ProcessBuilder.Redirect): String = {
            if(redirect.file() != null)
                redirect.file().getAbsolutePath()
            else ""
        }

        println("###")
        println("Starting new Process")
        println("Command: " + asScalaBuffer(command).mkString(","))
        println("Environment: " + mapAsScalaMap(environment).mkString(","))
        println("userDirectory: " + userDirectory)
        println("Out: " + out.`type`() + filePath(out))
        println("Err: " + err.`type`() + filePath(err))
        println("In: " + in.`type`() + filePath(in))
        println("RedirectErrorStream: " + redirectErrorStream)
        println("###")
        return new ProcessImpl()
    }

}
