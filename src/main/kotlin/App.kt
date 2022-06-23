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
    var selectedStation = Stations()

    fun init() {
        println("Hal init...") // should not be used here
        HAL.init()
        println("lastWrittenValue at init= " + HAL.lastWrittenValue)
        coinAcceptor.init()
        tui.init()
        stations.loadStations()
        ScreenWaiting()
    }

    fun ScreenWaiting() {
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
        ScreenStation('0') // show the first station by default
        var lastKey = '0'
        while(!finish){
            when (val k = tui.WaitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                //'*' -> alternateSelectionMode()
                '#' -> ScreenPayTicket()
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    ScreenStation(twoKeys)
                    lastKey=k
                }
            }
        }
    }

    private fun ScreenStation(k:Char) {
        val stationIdx = charToInt(k)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        tui.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }
    private fun ScreenStation(str:String) {
        val stationIdx = GetStationIdx(str)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        tui.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun GetStationIdx(str:String):Int {
        var stationIdx = str.toInt()
        val lst = stations.getAllStations()
        if(stationIdx >= lst.size)
            stationIdx = charToInt(str[1])
        return stationIdx
    }

    private fun ScreenPayTicket() {
        tui.PayScreenLCD(selectedStation.name, false, selectedStation.price.toString()) // default pay screen with roundtrip false
        while(!finish){
            when (val k = tui.WaitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '0' -> {
                    AlternateRoundTrip()
                    UpdateTripPrice()
                    tui.PayScreenLCD(selectedStation.name, selectedStation.roundtrip, selectedStation.price.toString())
                }
                '#' -> {
                    tui.AbortVendingLCD()
                    // wait x seconds
                    //tui.WriteInitialMenuLCD()
                    return
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun UpdateTripPrice() {
        if(selectedStation.roundtrip == false)
            selectedStation.price = selectedStation.price/2
        else
            selectedStation.price = selectedStation.price*2
    }

    private fun AlternateRoundTrip() {
        if(selectedStation.roundtrip == false)
            selectedStation.roundtrip = true
        else
            selectedStation.roundtrip = false
    }

    private fun charToInt(c:Char): Int {
       return c.code - '0'.code
    }

}