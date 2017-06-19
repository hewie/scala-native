package scala.scalanative.runtime

import scala.scalanative.native
import scala.scalanative.native._

object NullTerminatedArraySuite extends tests.Suite  {
  test("create empty array") {
    Zone { implicit z:Zone =>
      val array: NullTerminatedArray[Double] = NullTerminatedArray[Double](scala.Array[Ptr[Double]]())
      assert(array.toSeq.isEmpty)
    }
  }

  test("create Int Array") {
    Zone { implicit z:Zone =>
      val array: NullTerminatedArray[CInt] = NullTerminatedArray[CInt, Int](scala.Array[Int](1,2,3), (e:Int) => {
        val alloc1 = z.alloc(1 * sizeof[CInt]).asInstanceOf[Ptr[CInt]]
        !alloc1 = e
        alloc1
      })
      val seq = array.toSeq
      assert(seq.size == 3)
      assert(!(seq(0).ptr) == 1)
      assert(!(seq(1).ptr) == 2)
      assert(!(seq(2).ptr) == 3)
    }
  }

  test("create String Array") {
    Zone { implicit z:Zone =>
      val array: NullTerminatedArray[CChar] = NullTerminatedArray[CChar, String](scala.Array[String]("Hello","World!"), toCString)
      val seq = array.toSeq
      assert(seq.size == 2)
      assert(fromCString(seq(0).ptr) == "Hello")
      assert(fromCString(seq(1).ptr) == "World!")
    }
  }
}
