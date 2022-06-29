
//input Locations
const val RXD_LOCATION : Int = 0x20
//output Locations
const val RXCLK_LOCATION : Int = 0x10

const val KEY_FRAME_SIZE : Int = 6
const val FRAME_START_BIT_LOCATION : Int = 0x01
const val FRAME_STOP_BIT_LOCATION : Int = 0x20
const val FRAME_KEY_CODE_LOCATION : Int = 0x1E

// Receives the frame from Keyboard Reader
object KeyReceiver {
    var RXD = 1
    var RXCLK = 0
    var keyFrame = 0
    var frameCounter = 0

    // Initializes the class
    fun init() {
        RXCLK = 0
        HAL.clrBits(RXCLK_LOCATION)
        RXD = 1
        keyFrame = 0
        frameCounter = 0
    }

    //  Receives a frame and returns the code of a key, if it exists
    fun rcv(): Int {

        // protocolo inicio leitura
        if(!HAL.isBit((RXD_LOCATION))) {
            RXD = 0
            RXCLK = 1
            HAL.setBits(RXCLK_LOCATION)
            // Leitura Key Code
            frameCounter = 0
            keyFrame = 0
            while (frameCounter in 0 until KEY_FRAME_SIZE) {
                RXD = if (HAL.isBit(RXD_LOCATION)) 1 else 0
                keyFrame = keyFrame or (RXD shl frameCounter)
                RXCLK = 0
                HAL.clrBits(RXCLK_LOCATION)
                RXCLK = 1
                HAL.setBits(RXCLK_LOCATION)
                frameCounter++
            }

            // Verificar o correcto protocolgo da trama
            if (keyFrame and FRAME_START_BIT_LOCATION == 0)
                return -1 // we need solve this error maybe init to know stage
            else if (keyFrame and FRAME_STOP_BIT_LOCATION != 0)
                return -1  // we need solve this error maybe init to know stage
            else keyFrame = (keyFrame and FRAME_KEY_CODE_LOCATION) shr 1

            // Check TxD = 1 to fall clock
            var time = 1000 + System.currentTimeMillis()
            while (!HAL.isBit(RXD_LOCATION)) {
                /*wait*/
                if (time - System.currentTimeMillis() < 0)
                    return -1 // we need solve this error maybe init to know stage
            }
            RXD = 1
            RXCLK = 0
            HAL.clrBits(RXCLK_LOCATION)

            // return key code
            return keyFrame
        }
        return -1
    }
}