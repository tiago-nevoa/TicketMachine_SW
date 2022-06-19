import isel.leic.utils.Time
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

data class Station(val price:Int, var counter:Int, val name:String)

class TUI(private val lcd:LCD, private val  m:M, private val  kbd:KBD, private val coinAcceptor: CoinAcceptor, private var ticketDispenser: TicketDispenser) {

    private var originstation: Int? = null
    private var roundTrip = false
    private var finish = false
    private val listOfStations = readStations()

    private fun readStations(): List<Station> {
        val lines = File("stations.txt").readLines()
        val result = emptyList<Station>().toMutableList()
        var i = 0
        for (line in lines) {
            val values = line.split(';')
            result += Station(values[0].toInt(), i, values[2])
            if (values[0].toInt() == 0) // 0 = origin station
                originstation = i
            i++
        }
        return result
    }

    fun waitingScreen() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (true) {
            finish = false
            lcd.clean()
            lcd.write("Ticket to Ride")
            lcd.newLine()
            //lcd.write("Press #")

            var currentDate = dateFormat.format(Date())
            lcd.write(currentDate.toString())

            while (!finish) {
                if (m.maintenanceActive()) {
                    ScreenMaintenance()
                }
                if (kbd.getKey() == '#') {
                    initStationSelection()
                    ScreenSelectStation()
                }

                // Update the date label if it's different from the last date
                val newcurrentDate = dateFormat.format(Date())
                if (newcurrentDate != currentDate) {
                    lcd.newLine()
                    lcd.write(newcurrentDate.toString())
                    currentDate = newcurrentDate
                }

            }
        }
    }
}