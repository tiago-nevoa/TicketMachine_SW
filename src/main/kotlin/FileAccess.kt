import java.io.File
import java.io.PrintWriter

object FileAccess {

    fun init() {
        // todo
    }
    fun readLines(fileName:String) : List<String> {
        val lines = File(fileName).readLines()
        return lines
    }

    fun createWriter(fileName: String?): PrintWriter {
        return PrintWriter(fileName)
    }
}