package scala.scalanative.runtime

import scala.scalanative.native.{CString, _}

class NullTerminatedArray[E] private(val array: Ptr[Ptr[E]]) extends AnyVal {
}

object NullTerminatedArray {
  def apply[E](array: scala.Array[Ptr[E]])(implicit z: Zone): NullTerminatedArray[E] = {
    //FIXME use this when #783 is fixed
    // this.apply(array, identity[Ptr[E]])

    val result: Ptr[Ptr[E]] = z.alloc((array.length + 1) * sizeof[Ptr[Ptr[E]]]).asInstanceOf[Ptr[Ptr[E]]]

    for (i <- array.indices){
      !(result + i) = array(i)
    }

    !(result + array.length) = null
    new NullTerminatedArray(result)
  }

  def apply[E,I](array: scala.Array[I], transform: I => Ptr[E])(implicit z: Zone): NullTerminatedArray[E] = {

    val result: Ptr[Ptr[E]] = z.alloc((array.length + 1) * sizeof[Ptr[Ptr[E]]]).asInstanceOf[Ptr[Ptr[E]]]

    for (i <- array.indices){
      !(result + i) = transform(array(i))
    }

    !(result + array.length) = null
    new NullTerminatedArray(result)
  }

  implicit class NullTerminatedArrayIterator[E](array: NullTerminatedArray[E]) extends Iterable[PtrHolder[E]] {
    override def iterator: Iterator[PtrHolder[E]] = new Iterator[PtrHolder[E]] {
      var currentIndex = 0

      override def hasNext: Boolean = {
        val nextRef = !(array.array + currentIndex)
        nextRef != null
      }

      override def next(): PtrHolder[E] = {
        val element = !(array.array + currentIndex)
        currentIndex = currentIndex+1
        PtrHolder(element)
      }
    }
  }
}

//FIXME Workaround for #783
case class PtrHolder[E](ptr: Ptr[E])
