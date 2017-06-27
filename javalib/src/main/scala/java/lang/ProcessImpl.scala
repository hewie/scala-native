package java.lang

import java.io.{FileNotFoundException, InputStream, OutputStream}
import java.util
import java.util.concurrent.TimeUnit

import scala.scalanative.native.{CInt, CString, Ptr, Zone, errno, string, toCString}
import scala.scalanative.posix.errno._
import scala.scalanative.posix.unistd
import scala.scalanative.runtime.NullTerminatedArray

private class ProcessImpl() extends Process {

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

        Zone { implicit z =>
            Fork(() => {
                //chdir
                Exec(command.head, command.tail, Array[String]())
            })
        }
        return new ProcessImpl()

    }

    //Simulate execvpe because it is not available on all platforms
    private def execvpe(path: CString, argv: Ptr[CString], envp: Ptr[CString]): Unit = {
        unistd.environ = envp
        unistd.execvp(path, argv)
    }

    object Fork {
        def apply(childCode: () => Unit): Unit = {
            val processId = unistd.fork()
            if(processId < 0) {
                val error: CInt = errno.errno
                if(error != 0) {
                    if(error == EAGAIN) throw new InternalError("EAGAIN: Too many processes");
                    if(error == ENOMEM) throw new OutOfMemoryError("ENOMEM: Not enough memory");
                    else throw new Error("Unknow Error code: " + error)
                }
            }
            if(processId == 0 ){
                childCode()
            }
        }
    }

    object Exec {

        def apply(path: String, args: Seq[String], env: Seq[String]): Unit = {
            Zone { implicit z =>
                val argv: Ptr[CString] = NullTerminatedArray(args, toCString).array
                val envp: Ptr[CString] = NullTerminatedArray(env, toCString).array

                execvpe(toCString(path), argv, null)
                val error = errno.errno
                if( error == ENOENT) {
                    throw new FileNotFoundException("Could not find File: " + path)
                } else if (error != null) {
                    println(string.strerror(error))
                }
            }
        }
    }
}
