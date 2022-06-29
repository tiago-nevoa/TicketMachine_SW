
//input Locations
const val BUSY_LOCATION : Int = 0x40
//output Locations
const val SCLK_LOCATION : Int = 0x04
const val SDX_LOCATION : Int = 0x02
const val NOT_SS_LOCATION : Int = 0x08
const val FRAME_SIZE : Int = 10

// send frames to different serial receiver modules
object SerialEmitter {
    private var SCLK = 0
    private var SDX = 0
    private var notSS = 1
    private var mask = 0
    private var parityCheck = 0
    private var frameCounter = 0

    enum class Destination { LCD, TICKET_DISPENSER }

    // class initializer
    fun init() {
        HAL.init()
        serialEmitterReset()
    }

    // sends not SS, SDX, SCLK
    // sends block to SerialReceiver identifying the destination in addr field and data bits in data field
    fun send(addr: Destination, data: Int) {
        // data[d8..0], dataToSend[d8..0,TnL] 10bits
        var dataToSend = data shl 1
        // including bit Tnl = 0 LCD, TnL = 1 TICKET_DISPENSER
        if (addr == Destination.TICKET_DISPENSER) dataToSend = dataToSend or 1

        while (isBusy()) {/*wait*/}

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
            }
        // ParityBit
        SDX = if (parityCheck == 0) 0 else 1
        if (SDX == 0) HAL.clrBits(SDX_LOCATION) else HAL.setBits(SDX_LOCATION)
        SCLK = 1
        HAL.setBits(SCLK_LOCATION)

        serialEmitterReset()
    }
    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    private fun isBusy(): Boolean {
        return HAL.isBit(BUSY_LOCATION)
    }

    // Creat right conditions to emit data
    private fun serialEmitterReset() {
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
}