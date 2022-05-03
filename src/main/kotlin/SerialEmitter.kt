import isel.leic.UsbPort

//input Locations
const val BUSY_LOCATION : Int = 0x80
//output Locations
const val SCLK_LOCATION : Int = 0x01
const val SDX_LOCATION : Int = 0x02
const val NOT_SS_LOCATION : Int = 0x04
const val FRAME_SIZE : Int = 11*2


// sends block to the different Serial
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
        notSS = 1
        HAL.setBits(NOT_SS_LOCATION)
        SDX = 0
        mask = 1
        parityCheck = 0
        frameCounter = 0
    }

    // sends not SS, SDX, SCLK
    // sends block to SerialReceiver identifying the destination in addr field and data bits in data field
    fun send(addr: Destination, data: Int) {
        var dataToSend = data shl 1 // data[parityBit,d8..0], dataToSend[parityBit,d8..0,TnL] 11bits
        if(addr == Destination.TICKET_DISPENSER) dataToSend = dataToSend or 1 // including bit Tnl = 0 LCD, TnL = 1 TICKET_DISPENSER
        println("dataToSend on SerialEmitter: " + Integer.toBinaryString(dataToSend))
        println("Put isBusy at false in 5sec... (BUSY_LOCATION : Int = 0x80)")
        Thread.sleep(5000)
        if(!isBusy()){
            for (frameCounter in 0..FRAME_SIZE) {
                println("5sec...")
                Thread.sleep(5000)

                println("dataToSend on SerialEmitter = " + Integer.toBinaryString(dataToSend))
                println("mask                        = " + Integer.toBinaryString(mask))
                println("parityCheck  = " + Integer.toBinaryString(parityCheck))
                println("frameCounter  = $frameCounter")
                println("notSS = " + Integer.toBinaryString(notSS) + " (notSS : Int = 0x04)")
                println("SDX   = " + Integer.toBinaryString(SDX) + " (SDX : Int = 0x02)")
                println("SCLK  = " + Integer.toBinaryString(SCLK) + " (SCLK : Int = 0x1)")
                println("parityCheck  = " + Integer.toBinaryString(parityCheck))
                notSS = 0
                HAL.clrBits(NOT_SS_LOCATION)
                //HAL.writeBits(notSS,NOT_SS_LOCATION) // // check why notSS is not write on Hard
                SDX = if ((dataToSend and mask) == 0) 0 else 1
                if (frameCounter == FRAME_SIZE) SDX = parityCheck
                //HAL.writeBits(SDX,SDX_LOCATION) // check why SDX is not write on Hard
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
            }
            }
        println("SerialEmitter init...")
        init()
    }

    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    fun isBusy() : Boolean {
        return HAL.isBit(BUSY_LOCATION)
    }
}