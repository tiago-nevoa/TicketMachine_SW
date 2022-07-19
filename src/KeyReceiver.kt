
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
    private var RXD = 1
    private var RXCLK = 0
    private var keyFrame = 0
    private var frameCounter = 0

    // Initializes the class
    fun init() {
        HAL.init()
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
            HAL.clrBits(RXCLK_LOCATION)
            HAL.setBits(RXCLK_LOCATION)
            // Leitura Key Code
            frameCounter = 0
            keyFrame = 0
            while (frameCounter in 0 until KEY_FRAME_SIZE) {
                RXD = if (HAL.isBit(RXD_LOCATION)) 1 else 0
                keyFrame = keyFrame or (RXD shl frameCounter)
                HAL.clrBits(RXCLK_LOCATION)
                HAL.setBits(RXCLK_LOCATION)
                frameCounter++
            }
            HAL.clrBits(RXCLK_LOCATION)

            // Verificar o correcto protocolgo da trama
            if (keyFrame and FRAME_START_BIT_LOCATION == 0) {
                println("Frame error 1")
                return -1 // we need solve this error maybe init to know stage

            }
            else if (keyFrame and FRAME_STOP_BIT_LOCATION != 0)
            {
                println("Frame error 2")
                return -1 // we need solve this error maybe init to know stage

            }
                //return -1  // we need solve this error maybe init to know stage
            else keyFrame = (keyFrame and FRAME_KEY_CODE_LOCATION) shr 1

            // return key code
            return keyFrame
        }
        return -1
    }
}