package main.kotlin

import java.io.File

var allStations = emptyList<Stations>().toMutableList()

class Stations {
    var price:Int = 0
    var id:Int = 0
    var name:String = ""

    fun loadStations() {
            val file = FileAccess()
            val lines = file.readLines("stations.txt")

            for (line in lines) {
                val values = line.split(';')
                val station = Stations()
                station.price = values[0].toInt()
                station.id = values[1].toInt()
                station.name = values[2].toString()
                allStations += station
            }
    }

    fun getAllStations(): MutableList<Stations> {
        return allStations
    }
}