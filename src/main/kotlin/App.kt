import isel.leic.utils.Time
import main.kotlin.CoinDeposit
import main.kotlin.Stations
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

const val WAIT_SELECTION = 5000L // ms

class App() {
    private var originstation: Int? = null
    private var roundTrip = false
    private var finish = false
    var m = M()
    var coinAcceptor = CoinAcceptor()
    var ticketDispenser = TicketDispenser()
    var tui = TUI()
    var stations = Stations()
    var coinDeposit = CoinDeposit()

    fun init() {
        println("Hal init...") // should not be used here
        HAL.init()
        println("lastWrittenValue at init= " + HAL.lastWrittenValue)
        coinAcceptor.init()
        tui.init()
        stations.loadStations()
        waitingScreen()
    }

    fun waitingScreen() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (true) {
            tui.WriteInitialMenuLCD()
            var currentDate = dateFormat.format(Date())

            while (!finish) {
                //if (m.maintenanceActive()) {
                //ScreenMaintenance()
                //}
                if (tui.GetKey() == '#') {
                    ScreenSelectStation()
                    tui.WriteInitialMenuLCD()
                }

                // Update the date label if it's different from the last date
                val newcurrentDate = dateFormat.format(Date())
                if (newcurrentDate != currentDate) {
                    tui.WriteDateLCD(newcurrentDate)
                    currentDate = newcurrentDate
                }

            }
        }
    }

    private fun ScreenSelectStation() {
        StationScreen('0') // show the first station by default
        finish = false
        var lastKey = '0'
        while(!finish){
            when (val k = tui.WaitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                //'*' -> alternateSelectionMode()
                //'#' -> ScreenPayTicket()
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    StationScreen(twoKeys)
                    lastKey=k
                }
            }
        }
    }

    private fun StationScreen(k:Char) {
        val stationIdx = charToInt(k)
        println("Station IDX " + stationIdx)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        tui.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun StationScreen(str:String) {
        var stationIdx = str.toInt()
        println("Station IDX " + stationIdx)
        val lst = stations.getAllStations()
        var station = lst[charToInt(str[1])]
        if(stationIdx < lst.size)
            station = lst[stationIdx]
        else
            stationIdx = charToInt(str[1])
        tui.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun charToInt(c:Char): Int {
       return c.code - '0'.code
    }

}