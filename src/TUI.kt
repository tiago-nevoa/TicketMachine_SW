import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val COLS : Int = 16
private const val PRICE_PRESENT = true

object TUI {

    fun init() {
        LCD.init()
        KBD.init()
    }

    fun writeInitialMenuLCD() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        LCD.clean()
        writeCenteredText("Ticket to Ride",0)
        val currentDate = dateFormat.format(Date())
        writeDateLCD(currentDate.toString())
    }

    fun getKey(): Char {
        return KBD.getKey()
    }

    fun waitKey(time:Long):Char {
        return KBD.waitKey(time)
    }

    fun waitMaintenanceKey(time:Long):Char {
        return KBD.waitKeyMaintenance(time)
    }

    fun writeDateLCD(newCurrentDate:String) {
        LCD.newLine()
        LCD.write(newCurrentDate)
    }

    fun writeStationInfo(title: String, bottomLeft: String, bottomRight: Int) {
        LCD.clean()
        writeCenteredText(title,0)
        LCD.newLine()
        LCD.write(bottomLeft)
        val convertedPrice = writeInEuroFormat(bottomRight)
        writeBottomRight(convertedPrice, 1, PRICE_PRESENT)
        LCD.writeData(LCD.EUR_ADDRESS)
    }

    fun abortVendingLCD() {
        LCD.clean()
        writeCenteredText("Vending aborted", 0)
    }

    fun payScreenLCD(title: String,roundTrip: Boolean,middle: Int) {
        LCD.clean()
        writeCenteredText(title,0)
        LCD.newLine()
        LCD.writeData(LCD.ARROW_UP_ADDRESS)
        if (roundTrip) LCD.writeData(LCD.ARROW_DOWN_ADDRESS)
        val formatPrice = writeInEuroFormat(middle)
        writeCenteredText(formatPrice, 1)
        LCD.writeData(0) // write arrow down
        writeCenteredText(formatPrice, 1)
        LCD.writeData(LCD.EUR_ADDRESS)
    }

    fun writeTitleBottomLCD(title:String, bottomText:String) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        LCD.write(bottomText)
    }

    fun writeMaintenanceOptions(bottomText: String){
        LCD.clean()
        LCD.write("Maintenance Mode")
        LCD.newLine()
        LCD.write(bottomText)
    }

    fun writeCoinInfo(coinValue : Int, amount :Int?, keyPressed: Char){
        LCD.clean()
        val roundedAmount = writeInEuroFormat(coinValue)
        writeCenteredText(roundedAmount, 0)
        LCD.writeData(LCD.EUR_ADDRESS)
        LCD.newLine()
        LCD.write("0${keyPressed}:")
        writeBottomRight("$amount", 1, !PRICE_PRESENT)
    }

    private fun writeInEuroFormat(coinValue: Int) : String {
        return String.format("%.2f", (coinValue.toFloat()).roundToInt() / 100.0)
    }

    fun writeStationCountInfo(title:String, bottomLeft:String, bottomRight:String){
        LCD.clean()
        writeCenteredText(title, 0)
        LCD.newLine()
        LCD.write("${bottomLeft}:")
        writeBottomRight(bottomRight, 1, !PRICE_PRESENT)
    }

    private fun writeCenteredText(text: String, line: Int) {
        val middle = (text.length + COLS)/2 - text.length
        LCD.cursor(line,middle)
        LCD.write(text)
    }

    private fun writeBottomRight(text: String, line: Int, price: Boolean) {
        var bottomRight = COLS - text.length
        if(price) { bottomRight -= 1 }
        LCD.cursor(line,bottomRight)
        LCD.write(text)
    }
}