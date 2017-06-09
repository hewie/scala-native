package java.lang

import java.io.{InputStream, OutputStream}
import java.util.concurrent.TimeUnit
import java.lang.Boolean
import java.lang.Long

abstract class Process {
  def destroy(): Unit

  def destroyForcibly(): Process = {
    destroy()
    this
  }

  def exitValue(): Int

  def getErrorStream(): InputStream
  def getInputStream(): InputStream
  def getOutputStream(): OutputStream

  def isAlive(): Boolean = true

  def waitFor(): Int

  def waitFor(timeout: Long, unit: TimeUnit): Boolean
}
