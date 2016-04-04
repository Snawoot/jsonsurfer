import org.scalastuff.json.JsonParser
import java.io.FileReader
import scala.util.matching.Regex

import ValueType._

object jsonsurfer {
  val perr = System.err.println(_: String)
  val parser = new JsonParser(new JSONReader(json_cb))
  var (filterRE: Regex, groupRE: Regex) = (new Regex(".*"), new Regex("(.*)"))
  var lastKey: Option[List[String]] = None
  var groupID: Long = 0

  def usage() = {
    val programsrc = new Exception().getStackTrace.head.getFileName
    val program = if (programsrc.endsWith(".scala")) programsrc.dropRight(6) else programsrc
    perr(s"Usage: scala ${program} [ <filter regexp> [group key regexp] ]")
    System.exit(1)
  }

  def json_cb(path: String, vtype: ValueType, value: String) = {
    if (filterRE.unapplySeq(path) != None) {
      val newKey = groupRE.unapplySeq(path)
      groupID = if (newKey == lastKey) groupID else {
        lastKey = newKey
        groupID + 1
      }
      var label = vtype match {
        case NullValue => "(null)"
        case TrueValue => "(bool)"
        case FalseValue => "(bool)"
        case NumberValue => "(int)"
        case StringValue => "(string)"
      }
      var valueRepr = if (vtype == StringValue) "\"" + value + "\"" else value
      println(s"${groupID-1},${path},${valueRepr}")
    }
    true
  }

  def main(args: Array[String]) = {
    if (args.length == 0 || args.length > 4) {
      usage()
    }
    val (fRE, gRE) = args.length match {
      case 1 => (new Regex(".*"), new Regex("(.*)"))
      case 2 => (new Regex(args(1)), new Regex("(.*)"))
      case 3 => (new Regex(args(1)), new Regex(args(2)))
    }
    filterRE = fRE
    groupRE = gRE
    val reader = new FileReader(args(0))
    parser.parse(reader)
  }

}
