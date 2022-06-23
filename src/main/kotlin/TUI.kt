import isel.leic.utils.Time
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

//const val WAIT_SELECTION = 5000L // ms
//data class Station(val price:Int, var counter:Int, val name:String)

class TUI() {

    private var originstation: Int? = null
    private var roundTrip = false
    private var finish = false
    var lcd = LCD()
    var kbd = KBD()

    fun init() {
        lcd.init()
        kbd.init()
    }

    fun WriteInitialMenuLCD() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        finish = false
        lcd.clean()
        lcd.write("Ticket to Ride")
        //lcd.write("Press #")

        var currentDate = dateFormat.format(Date())
        WriteDateLCD(currentDate.toString())
    }

    fun GetKey(): Char {
        return kbd.getKey()
    }

    fun WaitKey(time:Long):Char {
        return kbd.waitKey(time)
    }

    fun WriteDateLCD(newcurrentDate:String) {
        lcd.newLine()
        lcd.write(newcurrentDate.toString())
    }

    fun WriteStationInfo(title:String, bottomLeft:String, bottomRight:String) {
        lcd.clean()
        lcd.write(title)
        lcd.newLine()
        lcd.write("${bottomLeft}            ${bottomRight}")
    }

    fun AbortVendingLCD() {
        lcd.clean()
        lcd.write("Vending aborted")
    }

    fun PayScreenLCD(title:String,roundtrip:Boolean,middle: String) {
        lcd.clean()
        lcd.write(title)
        lcd.newLine()
        var bottomLeft = ""
        if(roundtrip) bottomLeft = "1" else bottomLeft="0"
        lcd.write("${bottomLeft}      ${middle}")
    }

}