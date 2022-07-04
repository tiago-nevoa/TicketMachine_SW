var allStations = emptyList<Stations>().toMutableList()

class Stations {
    var price:Int = 0
    var counter:Int = 0
    var name:String = ""
    var roundtrip:Boolean = false
    var id:Int = 0

    fun init() {
        FileAccess.init() // todo
        loadStations()
    }

    private fun loadStations() {
        val lines = FileAccess.readLines("stations.txt")
        for (line in lines) {
            val values = line.split(';')
            val station = Stations()
            station.price = values[0].toInt()
            station.counter = values[1].toInt()
            station.name = values[2]
            allStations += station
        }
    }

    fun getAllStations(): MutableList<Stations> {
        return allStations
    }

    fun updateToFile() {
        val pw = FileAccess.createWriter("stations.txt")
        roundTripReset()
        for(station in allStations) {
            pw.println("${station.price};${station.counter};${station.name}") // formato coin;count (50;0)
        }
        // close write process
        pw.close()
    }

    fun roundTripReset(){
        for(station in allStations) {
            station.roundtrip = false
        }
    }

    // used in maintenance mode
    fun resetCounter() {
        for (station in allStations) {
            station.counter = 0
        }
        updateToFile()
    }
}