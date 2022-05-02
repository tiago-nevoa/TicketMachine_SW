import isel.leic.UsbPort

// the mask of the bit Busy (the first bit is 1 and the remaining are 0)
const val BUSY_LOCATION : Int = 0x80
const val SCLK_LOCATION : Int = 0x01
const val SDX_LOCATION : Int = 0x02
const val NOT_SS_LOCATION : Int = 0x04

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
                HAL.writeBits(notSS,NOT_SS_LOCATION)
                SDX = dataToSend and mask // We need find way send this correctly
                HAL.writeBits(SDX,SDX_LOCATION)
                if(SCLK == 1){
                    mask shl 1
                    SCLK = 0
                    HAL.clrBits(SCLK_LOCATION)
                }
                else{
                    SCLK = 1
                    HAL.setBits(SCLK_LOCATION)
                }
            }
        init()
    }

    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    fun isBusy() : Boolean {
        return HAL.isBit(BUSY_LOCATION)
    }
}