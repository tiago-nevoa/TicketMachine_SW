import isel.leic.utils.Time
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

//const val WAIT_SELECTION = 5000L // ms
//data class Station(val price:Int, var counter:Int, val name:String)

object TUI {

    private var originstation: Int? = null
    private var roundTrip = false
    private var finish = false

    fun init() {
        LCD.init()
        KBD.init()
    }

    fun WriteInitialMenuLCD() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        finish = false
        LCD.clean()
        LCD.write("Ticket to Ride")
        //lcd.write("Press #")

        var currentDate = dateFormat.format(Date())
        WriteDateLCD(currentDate.toString())
    }

    fun GetKey(): Char {
        return KBD.getKey()
    }

    fun WaitKey(time:Long):Char {
        return KBD.waitKey(time)
    }

    fun WriteDateLCD(newcurrentDate:String) {
        LCD.newLine()
        LCD.write(newcurrentDate.toString())
    }

    fun WriteStationInfo(title:String, bottomLeft:String, bottomRight:String) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        LCD.write("${bottomLeft}           ${bottomRight}")
    }

    fun AbortVendingLCD() {
        LCD.clean()
        LCD.write("Vending aborted")
    }

    fun PayScreenLCD(title:String,roundtrip:Boolean,middle: String) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        var bottomLeft = ""
        if(roundtrip) bottomLeft = "1" else bottomLeft="0"
        LCD.write("${bottomLeft}      ${middle}")
    }

    fun WriteTitleBottomLCD(title:String, bottomText:String) {
        LCD.clean()
        LCD.write(title)
        LCD.newLine()
        LCD.write(bottomText)
    }

    fun WritemMaintenanceOptions(bottomText: String){
        LCD.clean()
        LCD.write("Maintenance Mode")
        LCD.newLine()
        LCD.write(bottomText)
    }


}