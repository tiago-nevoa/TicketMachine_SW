import isel.leic.UsbPort

// the mask of the bit Busy (the first bit is 1 and the remaining are 0)
const val BUSY_LOCATION : Int = 0x80

// sends block to the different Serial
class SerialEmitter {
    var SCLK = 0
    var SDX = 0
    var notSS = 1
    var mask = 0
    enum class Destination { LCD, TICKET_DISPENSER }
    // class initializer
    fun init() {
        SCLK = 0
        notSS = 1
        SDX = 0
        mask = 1
    }

    // sends not SS, SDX, SCLK
    // sends block to SerialReceiver identifying the destination in addr field and data bits in data field
    fun send(addr: Destination, data: Int) { // sends data to LCD (HAL.write)
        var dataToSend = data shl 1
        if(addr == Destination.TICKET_DISPENSER) dataToSend = dataToSend or 1 // including bit Tnl in data to send to Ticket Dispenser
            while(!isBusy()) {
                notSS = 0
                SDX = dataToSend and mask
                SDX = HAL.writeBits()
        }
    }

    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    fun isBusy() : Boolean {
        return HAL.isBit(BUSY_LOCATION)
    }
}