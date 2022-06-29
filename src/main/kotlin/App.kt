import isel.leic.utils.Time
import main.kotlin.CoinDeposit
import main.kotlin.Stations
import main.kotlin.allStations
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

const val WAIT_SELECTION = 5000L // ms
const val WAIT_MAINTENANCE = 2000L // ms

class App() {
    private var originStation: Int = 6
    private var finish = false
    var m = M()
    var coinAcceptor = CoinAcceptor()
    var stations = Stations()
    var coinDeposit = CoinDeposit()
    var selectedStation = Stations()

    fun init() {
        println("Hal init...") // should not be used here
        HAL.init()
        println("lastWrittenValue at init= " + HAL.lastWrittenValue)
        coinAcceptor.init()
        TUI.init()
        stations.init()
        coinDeposit.init()
        ScreenWaiting()
    }

    fun ScreenWaiting() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (true) {
            TUI.WriteInitialMenuLCD()
            var currentDate = dateFormat.format(Date())

            while (!finish) {

                if (m.maintenanceActive()) {
                    ScreenMaintenance()
                }

                if (TUI.GetKey() == '#') {
                    ScreenSelectStation()
                    TUI.WriteInitialMenuLCD()
                }

                // Update the date label if it's different from the last date
                val newcurrentDate = dateFormat.format(Date())
                if (newcurrentDate != currentDate) {
                    TUI.WriteDateLCD(newcurrentDate)
                    currentDate = newcurrentDate
                }

            }
        }
    }

/* ---------- Maintenance Section ---------- */

    private fun ScreenMaintenance() {
        var option = 1
        while(!finish){
            when (TUI.WaitMaintenanceKey(WAIT_MAINTENANCE)){ // TESTE
            // when (val k = tui.WaitKey(WAIT_MAINTENANCE)){
                '1' -> printTicket()
                '2' -> stationCnt()
                '3' -> coinsCnt()
                '4' -> resetCnt()
                '5' -> shutdown()
                KEY_NONE -> {
                    TUI.WritemMaintenanceOptions(m.maintenanceOptionsMenu(option))
                    option ++
                    if(option > 5) {option = 1}
                }
            }
        }
    }

    private fun printTicket() {
        TUI.WritemMaintenanceOptions(m.maintenanceOptionsMenu(1))
    }

    private fun stationCnt() {
        TUI.WritemMaintenanceOptions(m.maintenanceOptionsMenu(2))
    }

    private fun coinsCnt() {
        TUI.WritemMaintenanceOptions(m.maintenanceOptionsMenu(3))
    }

    private fun resetCnt() {
        TUI.WritemMaintenanceOptions(m.maintenanceOptionsMenu(4))
        coinDeposit.resetCounter()
        stations.resetCounter()
    }

    private fun shutdown() {
        exitProcess(0)
    }

    private fun ScreenSelectStation() {
        ScreenStation('0') // show the first station by default
        var lastKey = '0'
        while(!finish){
            when (val k = TUI.WaitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                //'*' -> alternateSelectionMode()
                '#' -> {
                    ScreenPayTicket()
                    return
                }
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
        selectedStation.id = stationIdx
        TUI.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }
    private fun ScreenStation(str:String) {
        val stationIdx = GetStationIdx(str)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun GetStationIdx(str:String):Int {
        var stationIdx = str.toInt()
        val lst = stations.getAllStations()
        if(stationIdx >= lst.size)
            stationIdx = charToInt(str[1])
        return stationIdx
    }

    private fun ScreenPayTicket() {
        TUI.PayScreenLCD(selectedStation.name, false, selectedStation.price.toString()) // default pay screen with roundtrip false
        while(!finish){
            if(coinAcceptor.hasCoin()) {
                val insertedCoin = coinAcceptor.getCoinValue()
                coinDeposit.coinAmounts[insertedCoin] = coinDeposit.coinAmounts[insertedCoin]?.plus(1) // increase coin amount
                coinAcceptor.totalCoins += insertedCoin
                coinAcceptor.acceptCoin()
                //selectedStation.price -= insertedCoin
                var priceToCharge = selectedStation.price // TESTE
                if(selectedStation.roundtrip) { priceToCharge = selectedStation.price * 2} // TESTE
                val subCoins = priceToCharge - coinAcceptor.totalCoins // TESTE
                if(subCoins <= 0) {
                    CollectTicket()
                    selectedStation.counter++
                    if(selectedStation.roundtrip) { allStations[originStation].counter++ } // TESTE
                    stations.updateToFile() // save stations to txt. Fazemos aqui e nao no shutdown porque se a energia for abaixo perdiam-se os valores todos
                    coinDeposit.updateToFile() // save coins to txt
                    coinAcceptor.collectCoins() // collect and reset total coins
                    return
                } else {
                    TUI.PayScreenLCD(selectedStation.name, selectedStation.roundtrip, subCoins.toString())
                }
            }

            //when (val k = tui.WaitKey(WAIT_SELECTION)){
            when (val k = TUI.GetKey()){ // no timeout
                //KEY_NONE -> return
                '0' -> {
                    AlternateRoundTrip()
                    val priceToChange = selectedStation.price // TESTE
                    val finalPrice = UpdateTripPrice(priceToChange) // TESTE
                    TUI.PayScreenLCD(selectedStation.name, selectedStation.roundtrip, finalPrice.toString()) // TESTE
                }
                '#' -> {
                    stations.roundTripReset()
                    TUI.AbortVendingLCD()
                    coinAcceptor.ejectCoins()
                    coinDeposit.readFile()
                    return
                }
                else -> {
                    // do nothing
                }
            }

        }
    }

    private fun CollectTicket() {
        TUI.WriteTitleBottomLCD(selectedStation.name, "Collect Ticket")
        TicketDispenser.print(selectedStation.id,originStation,selectedStation.roundtrip)
        while(TicketDispenser.ticketCollected()) {/*wait*/}
        TUI.WriteTitleBottomLCD("Thank you :)","Have a nice trip")
        // save and go back to main screen after collecting ticket
    }

    private fun UpdateTripPrice(price : Int) : Int {
        var price = price // TESTE
        if(!selectedStation.roundtrip) { // TESTE
            price = selectedStation.price // TESTE
            return price // TESTE
        }
        price = selectedStation.price*2 // TESTE
        return price // TESTE
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