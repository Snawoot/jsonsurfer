import org.scalastuff.json.JsonParser
import java.io.FileReader

object jsonsurfer {
  val perr = System.err.println(_: String)
  val parser = new JsonParser(new PrintingReader(perr))
  def usage() = {
    val programsrc = new Exception().getStackTrace.head.getFileName
    val program = if (programsrc.endsWith(".scala")) programsrc.dropRight(6) else programsrc
    perr(s"Usage: scala ${program} firstArg secondArg")
    System.exit(1)
  }
  def main(args: Array[String]) = {
    if (args.length != 2) {
      usage()
    }
    val reader = new FileReader(args(0))
    parser.parse(reader)
    //for (a <- args) perr(a)
  }
}
