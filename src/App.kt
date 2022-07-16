import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

const val WAIT_SELECTION = 5000L // ms
const val WAIT_MAINTENANCE = 2000L // ms
const val ORIGIN_STATION = 6
const val MENU_INITIAL_POSITION = 0
const val ARROW = true
const val PRINT = true

object App {
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

// ---------- Home Section ----------- //
    private fun screenWaiting() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        while (!finish) {
            TUI.writeInitialMenuLCD()
            var currentDate = dateFormat.format(Date())

            while (!finish) {

                if (M.maintenanceActive()) {
                    screenMaintenance()
                }
                val key = TUI.getKey()
                if (key == '#') {
                    screenSelectStation(MENU_INITIAL_POSITION, !PRINT)
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
            when (TUI.waitMaintenanceKey(WAIT_MAINTENANCE)){
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
        screenSelectStation(MENU_INITIAL_POSITION, PRINT)
    }

    private fun stationCnt() {
        screenSelectCountStation(MENU_INITIAL_POSITION)
    }

    private fun coinsCnt() {
        screenSelectCoin(MENU_INITIAL_POSITION)
    }

    private fun resetCnt() {
        TUI.writeConfirmationMenu("Reset Counters")
        while(!finish){
            when (TUI.waitKey(WAIT_SELECTION)){
                '5' -> {
                    CoinDeposit.resetCounter()
                    stations.resetCounter()
                    screenMaintenance()
                }
                else -> {
                    screenMaintenance()
                }
            }
        }
    }

    private fun shutdown() {
        TUI.writeConfirmationMenu("Shutdown")
        while(!finish){
            when (TUI.waitKey(WAIT_SELECTION)){
                '5' -> exitProcess(0)
                else -> {
                    screenMaintenance()
                }
            }
        }
    }

    /* ---------- Select Station Section ---------- */
    private fun screenSelectStation(lastKey : Int, isPrint : Boolean) {
        // show the first station by default
        screenStation(lastKey.toString(), !ARROW)
        var lastKey = lastKey
        while(!finish){
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectStationArrows(lastKey, isPrint)
                    return
                }
                '#' -> {
                    if(lastKey == ORIGIN_STATION) continue
                    if (isPrint) screenPrintTicket()
                    else screenPayTicket()
                    return
                }
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    screenStation(twoKeys,!ARROW)
                    lastKey= charToInt(k)
                }
            }
        }
    }

    /* ---------- Select Station Section with Arrows ---------- */
    private fun screenSelectStationArrows(lastKey : Int, isPrint: Boolean) {
        screenStation(lastKey.toString(), ARROW)
        var lastKey = lastKey
        while(!finish){
            when (TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectStation(lastKey, isPrint)
                    return
                }
                '2' -> {
                    lastKey++
                    if (lastKey > 15) lastKey = 0
                    screenStation(lastKey.toString(),ARROW)
                }
                '8' -> {
                    lastKey--
                    if (lastKey < 0) lastKey = 15
                    screenStation(lastKey.toString(),ARROW)
                }
                '#' -> {
                    if(lastKey == ORIGIN_STATION) continue
                    if (isPrint) screenPrintTicket()
                    else screenPayTicket()
                    return
                }
                else -> {

                }
            }
        }
    }

    private fun screenStation(str: String, arrow: Boolean) {
        val stationIdx = getStationIdx(str)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.writeStationInfo(station.name, stationIdx, station.price, arrow)
    }

    private fun getStationIdx(str:String):Int {
        var stationIdx = str.toInt()
        val lst = stations.getAllStations()
        if(stationIdx >= lst.size)
            stationIdx = charToInt(str[1])
        return stationIdx
    }

    /* ---------- Pay Ticket Section ---------- */
    private fun screenPayTicket() {
        // default pay screen with roundtrip false
        TUI.payScreenLCD(selectedStation.name, false, selectedStation.price)
        while(!finish){
            if(CoinAcceptor.hasCoin()) {
                val insertedCoin = CoinAcceptor.getCoinValue()
                // increase coin amount
                CoinDeposit.coinAmounts[insertedCoin] = CoinDeposit.coinAmounts[insertedCoin]?.plus(1)
                CoinAcceptor.totalCoins += insertedCoin
                CoinAcceptor.acceptCoin()
                var priceToCharge = selectedStation.price
                if(selectedStation.roundtrip) priceToCharge = selectedStation.price * 2
                val subCoins = priceToCharge - CoinAcceptor.totalCoins
                if(subCoins <= 0) {
                    collectTicket()
                    selectedStation.counter++
                    if(selectedStation.roundtrip) allStations[ORIGIN_STATION].counter++
                    // save stations to txt. Fazemos aqui e nao no shutdown porque se a energia for abaixo perdiam-se os valores todos
                    stations.updateToFile()
                    // save coins to txt
                    CoinDeposit.updateToFile()
                    // collect and reset total coins
                    CoinAcceptor.collectCoins()
                    return
                } else {
                    TUI.payScreenLCD(selectedStation.name, selectedStation.roundtrip, subCoins)
                }
            }

            when (TUI.getKey()){
                '0' -> {
                    alternateRoundTrip()
                    val priceToChange = selectedStation.price
                    val finalPrice = updateTripPrice(priceToChange)
                    TUI.payScreenLCD(selectedStation.name, selectedStation.roundtrip, finalPrice)
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

    /* ---------- Print Ticket Section ---------- */
    private fun screenPrintTicket() {
        // default pay screen with roundtrip false
        TUI.printScreenLCD(selectedStation.name, false)
        while(!finish){
            when (TUI.getKey()){
                '0' -> {
                    alternateRoundTrip()
                    TUI.printScreenLCD(selectedStation.name, selectedStation.roundtrip)
                }
                '*' -> {
                    collectTicket()
                    return
                }
                '#' -> {
                    stations.roundTripReset()
                    TUI.abortVendingLCD()
                    return
                }
                else -> {
                    // do nothing
                }
            }

        }
    }

    /* ---------- Collect Ticket Section ---------- */
    private fun collectTicket() {
        TUI.writeTitleBottomLCD(selectedStation.name, "Collect Ticket")
        TicketDispenser.print(selectedStation.id,ORIGIN_STATION,selectedStation.roundtrip)

        while(TicketDispenser.ticketCollected()) { /*wait*/ }

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

    private fun screenSelectCoin(lastKey: Int) {
        screenCoinAmount(lastKey.toChar(), !ARROW)
        var lastKey = lastKey
        while(!finish){
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectCoinArrow(lastKey)
                    return
                }
                '#' -> {
                    screenMaintenance()
                    return
                }
                else -> {
                    screenCoinAmount(k, !ARROW)
                    lastKey = charToInt(k)
                }
            }
        }
    }

    private fun screenSelectCoinArrow(lastKey: Int) {
        screenCoinAmount(lastKey.toChar(), ARROW)
        var lastKey : Int = lastKey
        while(!finish){
           screenCoinAmount(lastKey.toChar(), ARROW)
            when (TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectCoin(lastKey)
                    return
                }
                '2' -> {
                    lastKey++
                    if (lastKey > 5) lastKey = 0
                    screenCoinAmount(lastKey.toChar(), ARROW)
                }
                '8' -> {
                    lastKey--
                    if (lastKey < 0) lastKey = 5
                    screenCoinAmount(lastKey.toChar(), ARROW)
                }
                '#' -> {
                    screenMaintenance()
                    return
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun screenCoinAmount(keyPressed : Char, arrow: Boolean) {
        var keyPressed = keyPressed
        var coinFacialValue = 0
        if(keyPressed !in '1'..'5') {
            coinFacialValue = 5
            keyPressed = '0'
        }
            else {
           coinFacialValue = CoinDeposit.coinValues[keyPressed]!!
        }
        val amount = CoinDeposit.coinAmounts[coinFacialValue]
        TUI.writeCoinInfo(coinFacialValue, amount, keyPressed, arrow)
    }

    private fun screenSelectCountStation(lastKey : Int) {
        screenCountStation(lastKey.toString(), !ARROW)
        var lastKey = lastKey
        while(!finish){
            when (val k = TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectCountStationARROW(lastKey)
                    return
                }
                '#' -> screenMaintenance()
                else -> {
                    val twoKeys = lastKey.toString()+k.toString()
                    screenCountStation(twoKeys, !ARROW)
                    lastKey= charToInt(k)
                }
            }
        }
    }

    private fun screenSelectCountStationARROW(lastKey: Int) {
        screenCountStation(lastKey.toString(), ARROW)
        var lastKey = lastKey
        while(!finish){
            when (TUI.waitKey(WAIT_SELECTION)){
                KEY_NONE -> return
                '*' -> {
                    screenSelectCountStation(lastKey)
                    return
                }
                '2' -> {
                    lastKey++
                    if (lastKey > 15) lastKey = 0
                    screenCountStation(lastKey.toString(), ARROW)
                }
                '8' -> {
                    lastKey--
                    if (lastKey < 0) lastKey = 15
                    screenCountStation(lastKey.toString(), ARROW)
                }
                '#' -> screenMaintenance()
                else -> {

                }
            }
        }
    }

    private fun screenCountStation(keyPressed: String ,arrow : Boolean) {
        val stationIdx = getStationIdx(keyPressed)
        val lst = stations.getAllStations()
        val station = lst[stationIdx]
        selectedStation = station
        selectedStation.id = stationIdx
        TUI.writeStationCountInfo(station.name, stationIdx, station.counter.toString(), arrow)
    }
}