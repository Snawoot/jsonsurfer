import org.scalastuff.json.JsonHandler
import scala.collection.mutable.Stack

class PrintingReader(printer: String => Unit) extends org.scalastuff.json.JsonHandler {
  var paths = Stack[String]()
  var arrayIndex: Long = 0
  var currentKey = "<root>"

  def startObject() = {
    printer("startObject")
    paths.push(currentKey)
  }
  def startMember(name: String) = {
    currentKey = name
    printer(s"startMember '${name}'")
  }
  def endObject() = {
    printer("endObject")
  }

  def startArray() = {
    printer("startArray")
  }
  def endArray() = {
    printer("endArray")
  }

  def string(s: String) = {
    printer(s"string = '${s}'")
  }
  def number(n: String) = {
    printer(s"number = ${n}")
  }
  def trueValue() = {
    printer("true")
  }
  def falseValue() = {
    printer("false")
  }
  def nullValue() = {
    printer("null")
  }

  def error(message: String, line: Int, pos: Int, excerpt: String) {
    printer(s"Error @ line ${line} position ${pos}: ${message}");
  }
}
