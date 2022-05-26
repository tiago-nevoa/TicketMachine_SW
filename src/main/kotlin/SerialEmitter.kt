import isel.leic.UsbPort

//input Locations
const val BUSY_LOCATION : Int = 0x80
//output Locations
const val SCLK_LOCATION : Int = 0x01
const val SDX_LOCATION : Int = 0x02
const val NOT_SS_LOCATION : Int = 0x04
const val FRAME_SIZE : Int = 10

// send frames to different serial receiver modules
class SerialEmitter {
    var SCLK = 0
    var SDX = 0
    var notSS = 1
    var mask = 0
    var parityCheck = 0
    var frameCounter = 0

    enum class Destination { LCD, TICKET_DISPENSER }

    // class initializer
    fun init() {
        SCLK = 0
        HAL.clrBits(SCLK_LOCATION)
        SDX = 0
        HAL.clrBits(SDX_LOCATION)
        notSS = 1
        HAL.setBits(NOT_SS_LOCATION)
        mask = 1
        parityCheck = 0
        frameCounter = 0
    }

    // sends not SS, SDX, SCLK
    // sends block to SerialReceiver identifying the destination in addr field and data bits in data field
    fun send(addr: Destination, data: Int) {
        // data[d8..0], dataToSend[d8..0,TnL] 10bits
        var dataToSend = data shl 1
        // including bit Tnl = 0 LCD, TnL = 1 TICKET_DISPENSER
        if (addr == Destination.TICKET_DISPENSER) dataToSend = dataToSend or 1

        println("Put isBusy at false in 5sec... (BUSY_LOCATION : Int = 0x80)")
        HAL.timeLapse(5000)

        println("toSend:")
        checkVariables(dataToSend)
        HAL.timeLapse(2000)

        if (!isBusy()) {
            for (frameCounter in 0 until FRAME_SIZE * 2) {

                notSS = 0
                HAL.clrBits(NOT_SS_LOCATION)
                SDX = if ((dataToSend and mask) == 0) 0 else 1
                if (SDX == 0) HAL.clrBits(SDX_LOCATION) else HAL.setBits(SDX_LOCATION)
                if (SCLK == 1) {
                    mask = mask shl 1
                    parityCheck = parityCheck xor SDX
                    SCLK = 0
                    HAL.clrBits(SCLK_LOCATION)
                } else {
                    SCLK = 1
                    HAL.setBits(SCLK_LOCATION)
                }

                println("sending:")
                checkVariables(dataToSend)
                HAL.timeLapse(200)
            }

            SDX = if (parityCheck == 0) 0 else 1
            if (SDX == 0) HAL.clrBits(SDX_LOCATION) else HAL.setBits(SDX_LOCATION)
            SCLK = 1
            HAL.setBits(SCLK_LOCATION)

            println("parityCheck:")
            checkVariables(dataToSend)
            HAL.timeLapse(2000)
        }
        println("SerialEmitter init...")
        init()
    }
    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    fun isBusy(): Boolean {
        return HAL.isBit(BUSY_LOCATION)
    }
    fun checkVariables(dataToSend: Int) {
        println("dataToSend on SerialEmitter = " + Integer.toBinaryString(dataToSend))
        println("mask                        = " + Integer.toBinaryString(mask))
        println("frameCounter  = ${frameCounter / 2}")
        println("SCLK  = " + Integer.toBinaryString(SCLK) + " (SCLK : Int = 0x1)")
        println("SDX   = " + Integer.toBinaryString(SDX) + " (SDX : Int = 0x02)")
        println("notSS = " + Integer.toBinaryString(notSS) + " (notSS : Int = 0x04)")
        println("parityCheck  = " + Integer.toBinaryString(parityCheck))
    }
}