package main.kotlin

import java.io.File

class FileAccess {
    fun readLines(fileName:String) : List<String> {
        val lines = File(fileName).readLines()
        return lines
    }


}