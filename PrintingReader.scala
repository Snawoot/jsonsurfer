import org.scalastuff.json.JsonHandler
import scala.collection.mutable.Stack

object ContainerType extends Enumeration {
      type ContainerType = Value
      val ObjectContainer, ArrayContainer, KeyContainer = Value
    }
import ContainerType._

class PrintingReader(printer: String => Unit) extends org.scalastuff.json.JsonHandler {
  var ctx = Stack[Tuple3[ContainerType,Long,String]]()
  var currentKey = ""

  def printStack() = {
    printer(ctx.toList.toString)
  }
  
  def getPath() = {
    var paths = Stack[String]()
    for (c <- ctx) {
      val p = c match {
        case (ObjectContainer, _, path: String) => path
        case (ArrayContainer, idx: Long, _) => (idx-1).toString
      }
      paths.push(p)
    }
    paths.mkString(".")
  }

  def printElem(e: String) = {
    printer(s"${getPath} => ${e}")
  }

  def incStack() = {
    if (!ctx.isEmpty) {
      val (t, i, k) = ctx.pop
      val ni = t match {
        case ArrayContainer => i + 1
        case ObjectContainer => i
        case _ => { printer("ERROR"); System.exit(5); i; }
      }
      ctx.push((t,ni,k))
    }
  }

  def startObject() = {
    printer("startObject")
    incStack
    ctx.push((ObjectContainer, 0, ""))
  }

  def startMember(name: String) = {
    printer(s"startMember '${name}'")
    val (t, i, k) = ctx.pop
    ctx.push((t,i,name))
  }

  def endObject() = {
    printer("endObject")
    ctx.pop
  }

  def startArray() = {
    printer("startArray")
    incStack
    ctx.push((ArrayContainer, 0, ""))
  }

  def endArray() = {
    printer("endArray")
    ctx.pop
  }

  def string(s: String) = {
    incStack
    printElem(s"string = '${s}'")
  }

  def number(n: String) = {
    incStack
    printElem(s"number = ${n}")
  }

  def trueValue() = {
    incStack
    printElem("true")
  }

  def falseValue() = {
    incStack
    printElem("false")
  }

  def nullValue() = {
    incStack
    printElem("null")
  }

  def error(message: String, line: Int, pos: Int, excerpt: String) {
    printer(s"Error @ line ${line} position ${pos}: ${message}");
  }
}
