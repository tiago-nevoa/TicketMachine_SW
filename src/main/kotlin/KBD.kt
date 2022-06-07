import kotlin.test.todo

const val NONE = 0;
const val KEY_NONE = NONE.toChar()
val KEYBOARD_TABLE : Array<Char> = arrayOf('1','2','3','x','4','5','6','x','7','8','9','x','*','0','#','x')
class KBD { // Read keys from keyboard. Methods return ‘0’..’9’,’#’,’*’ or NONE.
    private var keyReceiver = KeyReceiver()
    // Initializes the class
    fun init()  {
        HAL.init()
    }
    // Implements the parallel interaction with the Key Decode ---> TBC at ~6:30pm
    private fun getKeyParallel(): Char {
        return getKey()
    }
    //  Implements the serial interaction with the Key Transmitter
    private fun getKeySerial(): Char {
        return getKey()
    }
    // Immediately returns the pressed key or NONE if no key was pressed
    fun getKey(): Char {
        var key : Int = NONE
        keyReceiver.init()
        key += keyReceiver.rcv()
        // when invalid keyCode key = -1
        if (key < 0 || key > 15) return KEY_NONE
        return KEYBOARD_TABLE[key]
    }
    // Returns when the key was pressed or NONE after timeout’ milliseconds have passed
    fun waitKey(timeout: Long): Char {
        var time = timeout + System.currentTimeMillis()
        while(time-System.currentTimeMillis() > 0) {
            val key = getKey()
            if (key != KEY_NONE)
                return key
        }
        return KEY_NONE
    }
}