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

        screenWaiting()
    }

    fun screenWaiting() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (true) {
            TUI.writeInitialMenuLCD()
            var currentDate = dateFormat.format(Date())

            while (!finish) {

                if (M.maintenanceActive()) {
                    screenMaintenance()
                }

                if (TUI.getKey() == '#') {
                    screenSelectStation()
                    TUI.writeInitialMenuLCD()
                }

                // Update the date label if it's different from the last date
                val newcurrentDate = dateFormat.format(Date())
                if (newcurrentDate != currentDate) {
                    TUI.writeDateLCD(newcurrentDate)
                    currentDate = newcurrentDate
                }

            }
        }
    }

/* ---------- Maintenance Section ---------- */

    private fun screenMaintenance() {
        var option = 1
        while(M.maintenanceActive()){
            when (TUI.waitMaintenanceKey(WAIT_MAINTENANCE)){ // TESTE
                // when (val k = tui.WaitKey(WAIT_MAINTENANCE)){
                '1' -> printTicket()
                '2' -> stationCnt()
                '3' -> coinsCnt()
                '4' -> resetCnt()
                '5' -> shutdown()
                KEY_NONE -> {
                    TUI.writeMaintenanceOptions(M.maintenanceOptionsMenu(option))
                    option ++
                    if(option > 5) {option = 1}
                }
            }
        }
        screenWaiting()
    }

    private fun printTicket() {
        TUI.writeMaintenanceOptions(M.maintenanceOptionsMenu(1))
    }

    private fun stationCnt() {
        screenSelectCountStation()
    }

    private fun coinsCnt() {
        screenSelectCoin()
    }

    private fun resetCnt() {
        TUI.writeMaintenanceOptions(M.maintenanceOptionsMenu(4))
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
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {}
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
        TUI.writeStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }
    private fun screenStation(str:String) {
        val stationIdx = getStationIdx(str)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.writeStationInfo(station.name, stationIdx.toString(), station.price.toString())
    }

    private fun getStationIdx(str:String):Int {
        var stationIdx = str.toInt()
        val lst = stations.getAllStations()
        if(stationIdx >= lst.size)
            stationIdx = charToInt(str[1])
        return stationIdx
    }

    private fun screenPayTicket() {
        TUI.payScreenLCD(selectedStation.name, false, selectedStation.price.toString()) // default pay screen with roundtrip false
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
                    collectTicket()
                    selectedStation.counter++
                    if(selectedStation.roundtrip) { allStations[ORIGIN_STATION].counter++ } // TESTE
                    stations.updateToFile() // save stations to txt. Fazemos aqui e nao no shutdown porque se a energia for abaixo perdiam-se os valores todos
                    CoinDeposit.updateToFile() // save coins to txt
                    CoinAcceptor.collectCoins() // collect and reset total coins
                    return
                } else {
                    TUI.payScreenLCD(selectedStation.name, selectedStation.roundtrip, subCoins.toString())
                }
            }

            //when (val k = tui.WaitKey(WAIT_SELECTION)){
            when (val k = TUI.getKey()){ // no timeout
                //KEY_NONE -> return
                '0' -> {
                    alternateRoundTrip()
                    val priceToChange = selectedStation.price // TESTE
                    val finalPrice = updateTripPrice(priceToChange) // TESTE
                    TUI.payScreenLCD(selectedStation.name, selectedStation.roundtrip, finalPrice.toString()) // TESTE
                }
                '#' -> {
                    stations.roundTripReset()
                    TUI.abortVendingLCD()
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

    private fun collectTicket() {
        TUI.writeTitleBottomLCD(selectedStation.name, "Collect Ticket")
        TicketDispenser.print(selectedStation.id,ORIGIN_STATION,selectedStation.roundtrip)
        //var animationIndex = LCD.LOADING1_ADDRESS

        while(TicketDispenser.ticketCollected()) { /*wait*/ }

        //while(TicketDispenser.ticketCollected()) { // we need this?
        /*    TUI.writeTitleBottomLCD(selectedStation.name, "Collect Ticket")
            LCD.writeData(animationIndex)
            Thread.sleep(100)
            animationIndex++
            if(animationIndex == 4) animationIndex = 0 // reset animation
         */
            //if(!TicketDispenser.ticketCollected()) break
       // }
        TUI.writeTitleBottomLCD("Thank you :)","Have a nice trip")
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
        selectedStation.roundtrip = !selectedStation.roundtrip
    }

    private fun charToInt(c:Char): Int {
        return c.code - '0'.code
    }

    private fun screenSelectCoin() {
        screenCoinAmount('0')
        while(!finish){
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '#' -> screenMaintenance()
                else -> {
                    screenCoinAmount(k)
                }
            }
        }
    }

    private fun screenCoinAmount(keyPressed : Char) {
        var keyPressed = keyPressed
        var coinFacialValue : Int = 0
        if(keyPressed !in '1'..'5') {
            coinFacialValue = 5
            keyPressed = '0'
        }
            else {
           coinFacialValue = CoinDeposit.coinValues[keyPressed]!!
        }
        val amount = CoinDeposit.coinAmounts[coinFacialValue]
        TUI.writeCoinInfo(coinFacialValue, amount, keyPressed)
    }

    fun screenSelectCountStation() {
        screenCountStation('0')
        var lastKey = '0'
        while(!finish){
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '#' -> screenMaintenance()
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    screenCountStation(twoKeys)
                    lastKey=k
                }
            }
        }
    }

    fun screenCountStation(keyPressed : Char) {
        val stationIdx = charToInt(keyPressed)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.writeStationCountInfo(station.name, stationIdx.toString(), station.counter.toString())
    }

    fun screenCountStation(keyPressed:String) {
        val stationIdx = getStationIdx(keyPressed)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.writeStationCountInfo(station.name, stationIdx.toString(), station.counter.toString())
    }

}