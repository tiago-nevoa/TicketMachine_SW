import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

const val WAIT_SELECTION = 5000L // ms
const val WAIT_MAINTENANCE = 2000L // ms
const val ORIGIN_STATION = 6

class App() {
    private var finish = false
    var stations = Stations()
    var selectedStation = Stations()

    fun init() {
        M.init()
        CoinDeposit.init()
        TicketDispenser.init()
        TUI.init()
        stations.init()
        CoinDeposit.init()

        ScreenWaiting()
    }

    fun ScreenWaiting() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (true) {
            TUI.WriteInitialMenuLCD()
            var currentDate = dateFormat.format(Date())

            while (!finish) {

                if (M.maintenanceActive()) {
                    ScreenMaintenance()
                }

                if (TUI.GetKey() == '#') {
                    screenSelectStation()
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
                    TUI.WritemMaintenanceOptions(M.maintenanceOptionsMenu(option))
                    option ++
                    if(option > 5) {option = 1}
                }
            }
        }
    }

    private fun printTicket() {
        TUI.WritemMaintenanceOptions(M.maintenanceOptionsMenu(1))
    }

    private fun stationCnt() {
        TUI.WritemMaintenanceOptions(M.maintenanceOptionsMenu(2))
    }

    private fun coinsCnt() {
        TUI.WritemMaintenanceOptions(M.maintenanceOptionsMenu(3))
    }

    private fun resetCnt() {
        TUI.WritemMaintenanceOptions(M.maintenanceOptionsMenu(4))
        CoinDeposit.resetCounter()
        stations.resetCounter()
    }

    private fun shutdown() {
        exitProcess(0)
    }

    private fun screenSelectStation() {
        screenStation('0') // show the first station by default
        var lastKey = '0'
        while(!finish){
            when (val k = TUI.WaitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                //'*' -> alternateSelectionMode()
                '#' -> {
                    screenPayTicket()
                    return
                }
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    screenStation(twoKeys)
                    lastKey=k
                }
            }
        }
    }

    private fun screenStation(k:Char) {
        val stationIdx = charToInt(k)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }
    private fun screenStation(str:String) {
        val stationIdx = getStationIdx(str)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.WriteStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun getStationIdx(str:String):Int {
        var stationIdx = str.toInt()
        val lst = stations.getAllStations()
        if(stationIdx >= lst.size)
            stationIdx = charToInt(str[1])
        return stationIdx
    }

    private fun screenPayTicket() {
        TUI.PayScreenLCD(selectedStation.name, false, selectedStation.price.toString()) // default pay screen with roundtrip false
        while(!finish){
            if(CoinAcceptor.hasCoin()) {
                val insertedCoin = CoinAcceptor.getCoinValue()
                CoinDeposit.coinAmounts[insertedCoin] = CoinDeposit.coinAmounts[insertedCoin]?.plus(1) // increase coin amount
                CoinAcceptor.totalCoins += insertedCoin
                CoinAcceptor.acceptCoin()
                //selectedStation.price -= insertedCoin
                var priceToCharge = selectedStation.price // TESTE
                if(selectedStation.roundtrip) { priceToCharge = selectedStation.price * 2} // TESTE
                val subCoins = priceToCharge - CoinAcceptor.totalCoins // TESTE
                if(subCoins <= 0) {
                    CollectTicket()
                    selectedStation.counter++
                    if(selectedStation.roundtrip) { allStations[ORIGIN_STATION].counter++ } // TESTE
                    stations.updateToFile() // save stations to txt. Fazemos aqui e nao no shutdown porque se a energia for abaixo perdiam-se os valores todos
                    CoinDeposit.updateToFile() // save coins to txt
                    CoinAcceptor.collectCoins() // collect and reset total coins
                    return
                } else {
                    TUI.PayScreenLCD(selectedStation.name, selectedStation.roundtrip, subCoins.toString())
                }
            }

            //when (val k = tui.WaitKey(WAIT_SELECTION)){
            when (val k = TUI.GetKey()){ // no timeout
                //KEY_NONE -> return
                '0' -> {
                    alternateRoundTrip()
                    val priceToChange = selectedStation.price // TESTE
                    val finalPrice = updateTripPrice(priceToChange) // TESTE
                    TUI.PayScreenLCD(selectedStation.name, selectedStation.roundtrip, finalPrice.toString()) // TESTE
                }
                '#' -> {
                    stations.roundTripReset()
                    TUI.AbortVendingLCD()
                    CoinAcceptor.ejectCoins()
                    CoinDeposit.readFile()
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
        TicketDispenser.print(selectedStation.id,ORIGIN_STATION,selectedStation.roundtrip)
        while(TicketDispenser.ticketCollected()) {/*wait*/}
        TUI.WriteTitleBottomLCD("Thank you :)","Have a nice trip")
        // save and go back to main screen after collecting ticket
    }

    private fun updateTripPrice(price : Int) : Int {
        var price = price
        if(!selectedStation.roundtrip) {
            price = selectedStation.price
            return price
        }
        price = selectedStation.price*2
        return price
    }
    private fun alternateRoundTrip() {
        if(selectedStation.roundtrip == false)
            selectedStation.roundtrip = true
        else
            selectedStation.roundtrip = false
    }

    private fun charToInt(c:Char): Int {
        return c.code - '0'.code
    }

}