import org.scalastuff.json.JsonParser
import java.io.FileReader

import ValueType._

object jsonsurfer {
  val perr = System.err.println(_: String)
  val parser = new JsonParser(new JSONReader(json_cb))

  def usage() = {
    val programsrc = new Exception().getStackTrace.head.getFileName
    val program = if (programsrc.endsWith(".scala")) programsrc.dropRight(6) else programsrc
    perr(s"Usage: scala ${program} firstArg secondArg")
    System.exit(1)
  }

  def json_cb(path: String, vtype: ValueType, value: String) = {
    var label = vtype match {
      case NullValue => "(null)"
      case TrueValue => "(bool)"
      case FalseValue => "(bool)"
      case NumberValue => "(int)"
      case StringValue => "(string)"
    }
    println(s"${path} => ${label} '${value}'")
    true
  }

  def main(args: Array[String]) = {
    if (args.length != 2) {
      usage()
    }
    val reader = new FileReader(args(0))
    parser.parse(reader)
  }

}
