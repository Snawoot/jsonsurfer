import org.json.simple.parser.ContentHandler
import java.util.Stack

enum class ContainerType {
      ObjectContainer, ArrayContainer
}

enum class ValueType {
      TrueValue, FalseValue, NullValue, StringValue, NumberValue
}

interface PassJSONValue {
  fun passJSONValue(path: String, valtype: ValueType, strval: String) : Boolean
}

class JSONReader(val cb: PassJSONValue): ContentHandler {
  val ctx = Stack<Triple<ContainerType,Long,String>>()

  fun pushResult(t: ValueType, e: String): Boolean {
    cb.passJSONValue(getPath(), t, e)
    return true;
  }

  fun getPath() : String {
    val paths = arrayListOf<String>()
    for (c in ctx) {
      val p = when(c.first) {
        ContainerType.ObjectContainer -> c.third
        ContainerType.ArrayContainer -> (c.second-1).toString()
      }
      paths.add(p)
    }
    return paths.joinToString(".")
  }

  fun incStack() {
    if (!ctx.isEmpty()) {
      val (t, i, k) = ctx.pop()
      val ni = when(t) {
        ContainerType.ArrayContainer -> i + 1
        ContainerType.ObjectContainer -> i
      }
      ctx.push(Triple(t,ni,k))
    }
  }

  override fun startObject() : Boolean {
    incStack()
    ctx.push(Triple(ContainerType.ObjectContainer, 0, ""))
    return true;
  }

  override fun startObjectEntry(name: String) : Boolean {
    val (t, i, k) = ctx.pop()
    ctx.push(Triple(t,i,name))
    return true;
  }

  override fun endObject() : Boolean {
    ctx.pop()
    return true;
  }

  override fun startArray() : Boolean {
    incStack()
    ctx.push(Triple(ContainerType.ArrayContainer, 0, ""))
    return true;
  }

  override fun endArray() : Boolean {
    ctx.pop()
    return true;
  }

  override fun primitive(value: Any?) : Boolean {
    incStack()
    val (valtype, strval) = when(value) {
      is String -> Pair(ValueType.StringValue, value)
      is Number -> Pair(ValueType.NumberValue, value.toString())
      is Boolean -> Pair(if (value) ValueType.TrueValue else ValueType.FalseValue, value.toString())
      null -> Pair(ValueType.NullValue, "null")
      else -> {
        throw Exception("Unknown type of value ${value.toString()}")
        }
    }
    return pushResult(valtype, strval)
  }

  override fun endJSON() { }

  override fun startJSON() { }

  override fun endObjectEntry() : Boolean { return true; }
}
