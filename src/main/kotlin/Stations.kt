package main.kotlin

import java.io.File

var allStations = emptyList<Stations>().toMutableList()

class Stations {
    var price:Int = 0
    var counter:Int = 0
    var name:String = ""
    var roundtrip:Boolean = false
    var id:Int = 0

    fun init() {
        loadStations()
    }

    fun loadStations() {
            val file = FileAccess()
            val lines = file.readLines("stations.txt")

            for (line in lines) {
                val values = line.split(';')
                val station = Stations()
                station.price = values[0].toInt()
                station.counter = values[1].toInt()
                station.name = values[2].toString()
                allStations += station
            }
    }

    fun getAllStations(): MutableList<Stations> {
        return allStations
    }

    fun updateToFile() {
        val fileAccess = FileAccess()
        val pw = fileAccess.createWriter("stations.txt")
        for(station in allStations) {
            station.roundtrip = false
            pw.println("${station.price};${station.counter};${station.name}") // formato coin;count (50;0)
        }
        pw.close() // fechar processo de escrita
    }
}