import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val COLS : Int = 16
private const val PRICE_PRESENT = true

//const val WAIT_SELECTION = 5000L // ms
//data class Station(val price:Int, var counter:Int, val name:String)

object TUI {

    private var finish = false

    fun init() {
        LCD.init()
        KBD.init()
    }

    fun writeInitialMenuLCD() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        finish = false
        LCD.clean()
        LCD.write("Ticket to Ride")
        //lcd.write("Press #")

        val currentDate = dateFormat.format(Date())
        writeDateLCD(currentDate.toString())
    }

    fun getKey(): Char {
        return KBD.getKey()
    }

    fun waitKey(time:Long):Char {
        return KBD.waitKey(time)
    }

    fun waitMaintenanceKey(time:Long):Char { // TESTE
        return KBD.waitKeyMaintenance(time)
    }

    fun writeDateLCD(newcurrentDate:String) {
        LCD.newLine()
        LCD.write(newcurrentDate.toString())
    }

    fun writeStationInfo(title:String, bottomLeft:String, bottomRight: Int) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        LCD.write(bottomLeft)
        val convertedPrice = writeInEuroFormat(bottomRight)
        writeBottomRight(convertedPrice, 1, PRICE_PRESENT)
        LCD.writeData(LCD.EUR_ADDRESS)
    }

    fun abortVendingLCD() {
        LCD.clean()
        LCD.write("Vending aborted")
    }

    fun payScreenLCD(title:String,roundtrip:Boolean,middle: Int) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        var bottomLeft = ""
        if(roundtrip) bottomLeft = "1" else bottomLeft="0"
        LCD.write(bottomLeft)
        val formatedPrice = writeInEuroFormat(middle)
        writeCenteredText(formatedPrice, 1)
        LCD.writeData(0) // write arrow down
        if(roundtrip)
            LCD.writeData(LCD.ARROW_DOWN_ADDRESS)  // write arrow up
        writeCenteredText(formatedPrice, 1)
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
        writeBottomRight("${amount}", 1, !PRICE_PRESENT)
    }

    fun writeInEuroFormat(coinValue: Int) : String {
        return String.format("%.2f", (coinValue.toFloat()).roundToInt() / 100.0)
    }

    fun writeStationCountInfo(title:String, bottomLeft:String, bottomRight:String){
        LCD.clean()
        writeCenteredText(title, 0)
        LCD.newLine()
        LCD.write("${bottomLeft}:")
        writeBottomRight(bottomRight, 1, !PRICE_PRESENT)
    }

    fun writeCenteredText(text: String, line: Int) {
        val middle = (text.length + COLS)/2 - text.length
        LCD.cursor(line,middle)
        LCD.write(text)
    }

    fun writeBottomRight(text: String, line: Int, price: Boolean) {
        var bottomRight = COLS - text.length
        if(price) { bottomRight -= 1 }
        LCD.cursor(line,bottomRight)
        LCD.write(text)
    }
}