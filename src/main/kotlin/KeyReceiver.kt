import isel.leic.UsbPort

//input Locations
const val RXD_LOCATION : Int = 0x80
//output Locations
const val RXCLK_LOCATION : Int = 0x01

const val KEY_FRAME_SIZE : Int = 6
const val FRAME_START_BIT_LOCATION : Int = 0x01
const val FRAME_STOP_BIT_LOCATION : Int = 0x20
const val FRAME_KEY_CODE_LOCATION : Int = 0x1E

// Receives the frame from Keyboard Reader
class KeyReceiver {
    var RXD = 0
    var RXCLK = 0
    var keyFrame = 0
    var frameCounter = 0

    // Initializes the class
    fun init() {
        HAL.init()
        RXCLK = 0
        HAL.clrBits(RXCLK_LOCATION)
        keyFrame = 0
        frameCounter = 0
    }

    //  Receives a frame and returns the code of a key, if it exists
    fun rcv(): Int {
        // protocolo inicio leitura
        if (HAL.isBit(RXD_LOCATION)) {
            RXD = 1

            // Criar wait Time longer that hardware clock
            HAL.timeLapse(100)

            if (!HAL.isBit(RXD_LOCATION)) {
                RXD = 0

                HAL.setBits(RXCLK_LOCATION)
                // Leitura Key Code
                while (frameCounter in 0 until KEY_FRAME_SIZE) {
                    RXD = if (HAL.isBit(RXD_LOCATION)) 1 else 0
                    keyFrame = keyFrame or (RXD shl frameCounter)
                    RXCLK = 0
                    HAL.clrBits(RXCLK_LOCATION)
                    RXCLK = 1
                    HAL.setBits(RXCLK_LOCATION)
                }

            }
        }

        // Verificar o correcto protocolgo da trama
        if (keyFrame and FRAME_START_BIT_LOCATION == 0)
        // error return -1? TODO
        else if (keyFrame and FRAME_STOP_BIT_LOCATION != 0)
        // error return -1? TODO
        else keyFrame = (keyFrame and FRAME_KEY_CODE_LOCATION) shr 1

        // Verificar se precisamos colocar TXclk a 0
        RXCLK = 0
        HAL.clrBits(RXCLK_LOCATION)

        // return key code
        return keyFrame
    }
}