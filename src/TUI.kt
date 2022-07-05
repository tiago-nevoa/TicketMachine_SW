import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val COLS : Int = 16
private const val PRICE_PRESENT = true
private const val TOP_LINE = 0
private const val BOTTOM_LINE = 1

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

    fun writeStationInfo(title: String, bottomLeft: Int, bottomRight: Int, arrow: Boolean) {
        LCD.clean()
        writeCenteredText(title,TOP_LINE)
        LCD.newLine()
        val convertedIdx = writeTwoDigitsFormat(bottomLeft)
        LCD.write(convertedIdx)
        if (arrow) {
            LCD.writeData(ARROW_UP_ADDRESS)
            LCD.writeData(ARROW_DOWN_ADDRESS)
        }
        else LCD.write(':')
        val convertedPrice = writeInEuroFormat(bottomRight)
        writeBottomRight(convertedPrice, BOTTOM_LINE, PRICE_PRESENT)
        LCD.writeData(EUR_ADDRESS)
    }

    fun abortVendingLCD() {
        LCD.clean()
        writeCenteredText("Vending aborted", TOP_LINE)
    }

    fun payScreenLCD(title: String,roundTrip: Boolean,middle: Int) {
        LCD.clean()
        writeCenteredText(title,TOP_LINE)
        LCD.newLine()
        LCD.writeData(ARROW_UP_ADDRESS)
        if (roundTrip) LCD.writeData(ARROW_DOWN_ADDRESS)
        val formatPrice = writeInEuroFormat(middle)
        writeCenteredText(formatPrice, BOTTOM_LINE)
        LCD.writeData(0) // write arrow down
        writeCenteredText(formatPrice, BOTTOM_LINE)
        LCD.writeData(EUR_ADDRESS)
    }

    fun writeTitleBottomLCD(title:String, bottomText:String) {
        LCD.clean()
        writeCenteredText(title,TOP_LINE)
        writeCenteredText(bottomText,BOTTOM_LINE)
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
        writeCenteredText(roundedAmount, TOP_LINE)
        LCD.writeData(EUR_ADDRESS)
        LCD.newLine()
        LCD.write("0${keyPressed}:")
        writeBottomRight("$amount", BOTTOM_LINE, !PRICE_PRESENT)
    }

    private fun writeInEuroFormat(coinValue: Int) : String {
        return String.format("%.2f", (coinValue.toFloat()).roundToInt() / 100.0)
    }

    private fun writeTwoDigitsFormat(number : Int) : String {
        return String.format("%02d", number)
    }

    fun writeStationCountInfo(title:String, bottomLeft:Int, bottomRight:String){
        LCD.clean()
        writeCenteredText(title, TOP_LINE)
        LCD.newLine()
        val convertedIdx = writeTwoDigitsFormat(bottomLeft)
        LCD.write(convertedIdx)
        LCD.write(':')
        writeBottomRight(bottomRight, BOTTOM_LINE, !PRICE_PRESENT)
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

    fun writeConfirmationMenu(text: String) {
        LCD.clean()
        writeCenteredText(text, 0)
        LCD.newLine()
        LCD.write("5-Yes")
        writeBottomRight("other-No", BOTTOM_LINE, !PRICE_PRESENT)
    }
}