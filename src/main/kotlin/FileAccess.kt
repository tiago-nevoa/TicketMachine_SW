package main.kotlin

import java.io.File
import java.io.PrintWriter

class FileAccess {
    fun readLines(fileName:String) : List<String> {
        val lines = File(fileName).readLines()
        return lines
    }

    fun createWriter(fileName: String?): PrintWriter {
        return PrintWriter(fileName)
    }


}