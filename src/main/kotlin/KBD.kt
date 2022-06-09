import isel.leic.UsbPort

const val NONE = 0;
const val KEY_NONE = NONE.toChar()
const val GET_SERIAL = true
const val GET_PARALLEL = !GET_SERIAL
val KEYBOARD_TABLE : Array<Char> = arrayOf('1','4','7','*','2','5','8','0','3','6','9','#')

class KBD { // Read keys from keyboard. Methods return ‘0’..’9’,’#’,’*’ or NONE.
    private var keyReceiver = KeyReceiver()
    // Initializes the class
    fun init()  {
        keyReceiver.init()
    }
    // Implements the parallel interaction with the Key Decode ---> TBC at ~6:30pm
    private fun getKeyParallel(): Char {
        TODO(/*get key directly from KeyDecode */)
        return KEY_NONE
    }
    //  Implements the serial interaction with the Key Transmitter
    private fun getKeySerial(): Char {
        var key : Int = NONE
        key += keyReceiver.rcv()

        // when invalid keyCode key = -1
        if (key < 0 || key > 11) return KEY_NONE

        // convert key code to key char
        return KEYBOARD_TABLE[key]
    }
    // Immediately returns the pressed key or NONE if no key was pressed
    fun getKey(): Char {
        if(GET_SERIAL) return getKeySerial()
        if(GET_PARALLEL) return getKeyParallel()
        return KEY_NONE
    }
    // Returns when the key was pressed or NONE after timeout’ milliseconds have passed
    fun waitKey(timeout: Long): Char {
        var time = timeout + System.currentTimeMillis()
        while(time-System.currentTimeMillis() > 0) {
            val key = getKey()
            if (key != KEY_NONE) return key
        }
        return KEY_NONE
    }
}