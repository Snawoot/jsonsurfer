import java.io.FileReader
import org.json.simple.parser.JSONParser

fun perr(s: String) = System.err.println(s)

fun usage() {
  val programsrc = Exception().stackTrace.last().getFileName()
  val program = if (programsrc.endsWith(".kt")) programsrc.dropLast(3) else programsrc
  perr("Usage: java -jar ${program}.jar <json file> [ <filter regexp> [group key regexp] ]")
  System.exit(1)
}

class KeyProcessor(private val filterRE: Regex, private val groupRE: Regex) : PassJSONValue {
  private var groupID: Long = 0
  private var lastKey: List<String>? = null

  override fun passJSONValue(path: String, vtype: ValueType, value: String) : Boolean {
    if (filterRE.matchEntire(path) != null) {
      val newKeyMatch = groupRE.matchEntire(path)
      val newKey = newKeyMatch?.groupValues?.drop(1)
      groupID = if (newKey == lastKey) groupID else {
        lastKey = newKey
        groupID + 1
      }
      /* var label = vtype match {
        ValueType.NullValue => "(null)"
        ValueType.TrueValue => "(bool)"
        ValueType.FalseValue => "(bool)"
        ValueType.NumberValue => "(int)"
        ValueType.StringValue => "(string)"
      } */
      val valueRepr = if (vtype == ValueType.StringValue) "\"" + value + "\"" else value
      println("${groupID},${path},${valueRepr}")
    }
    return true
  }
}

fun main(args: Array<String>) {
  if (args.size == 0 || args.size > 4) {
    usage()
  }
  val (fRE, gRE) = when(args.size) {
    1 -> Pair(Regex(".*"), Regex("(.*)"))
    2 -> Pair(Regex(args[1]), Regex("(.*)"))
    else -> Pair(Regex(args[1]), Regex(args[2]))
  }

  val kp = KeyProcessor(fRE, gRE)
  val parser = JSONParser()
  parser.parse(FileReader(args[0]), JSONReader(kp), true)
}
