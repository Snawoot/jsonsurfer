import org.json.simple.parser.ContentHandler
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

class JSONReader(cb: (String, ValueType, String) => Boolean) extends org.json.simple.parser.ContentHandler {
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
    true
  }

  def startObject() = {
    incStack
    ctx.push((ObjectContainer, 0, ""))
    true
  }

  def startObjectEntry(name: String) = {
    val (t, i, k) = ctx.pop
    ctx.push((t,i,name))
    true
  }

  def endObject() = {
    ctx.pop
    true
  }

  def startArray() = {
    incStack
    ctx.push((ArrayContainer, 0, ""))
    true
  }

  def endArray() = {
    ctx.pop
    true
  }

  def primitive(value: Any) = {
    incStack
    val (valtype, strval) = value match {
      case v: String => (StringValue, v)
      case v: Number => (NumberValue, v.toString())
      case v: java.lang.Boolean => (if (v) TrueValue else FalseValue, v.toString())
      case null => (NullValue, "null")
      case _ => {
        throw new Exception(s"Unknown type of value ${value.toString()}")
        (NullValue, "null")
        }
    }
    pushResult(valtype, strval)
  }

  def endJSON() = { }

  def startJSON() = { }

  def endObjectEntry() = { true }
}
