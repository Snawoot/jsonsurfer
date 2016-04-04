import org.scalastuff.json.JsonHandler
import scala.collection.mutable.{Stack,ListBuffer}

object ContainerType extends Enumeration {
      type ContainerType = Value
      val ObjectContainer, ArrayContainer = Value
    }
import ContainerType._

object ValueType extends Enumeration {
      type ValueType = Value
      val TrueValue, FalseValue, NullValue, StringValue, NumberValue = Value
    }
import ValueType._

class JSONReader(cb: (String, ValueType, String) => Any) extends org.scalastuff.json.JsonHandler {
  var ctx = Stack[Tuple3[ContainerType,Long,String]]()
  val output = cb
  
  def pushResult(t: ValueType, e: String) = {
    output(getPath, t, e)
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

  def incStack() = {
    if (!ctx.isEmpty) {
      val (t, i, k) = ctx.pop
      val ni = t match {
        case ArrayContainer => i + 1
        case ObjectContainer => i
      }
      ctx.push((t,ni,k))
    }
  }

  def startObject() = {
    incStack
    ctx.push((ObjectContainer, 0, ""))
  }

  def startMember(name: String) = {
    val (t, i, k) = ctx.pop
    ctx.push((t,i,name))
  }

  def endObject() = {
    ctx.pop
  }

  def startArray() = {
    incStack
    ctx.push((ArrayContainer, 0, ""))
  }

  def endArray() = {
    ctx.pop
  }

  def string(s: String) = {
    incStack
    pushResult(StringValue, s)
  }

  def number(n: String) = {
    incStack
    pushResult(NumberValue, n)
  }

  def trueValue() = {
    incStack
    pushResult(TrueValue, "true")
  }

  def falseValue() = {
    incStack
    pushResult(FalseValue, "false")
  }

  def nullValue() = {
    incStack
    pushResult(NullValue, "null")
  }

  def error(message: String, line: Int, pos: Int, excerpt: String) {
    System.err.println(s"Error @ line ${line} position ${pos}: ${message}");
  }
}
