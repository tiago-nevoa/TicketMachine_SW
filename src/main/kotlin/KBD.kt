import kotlin.test.todo

const val NONE = 0;
const val KEY_NONE = NONE.toChar()
class KBD { // Read keys from keyboard. Methods return ‘0’..’9’,’#’,’*’ or NONE.
    private var keyReceiver = KeyReceiver()
    // Initializes the class
    fun init()  {
    }
    // Implements the parallel interaction with the Key Decode ---> TBC at ~6:30pm
    private fun getKeyParallel(): Char {
    }
    //  Implements the serial interaction with the Key Transmitter
    private fun getKeySerial(): Char {
    }
    // Immediately returns the pressed key or NONE if no key was pressed
    fun getKey(): Char {
        var key : Int = NONE
        keyReceiver.init()
        key += keyReceiver.rcv()
        TODO(/* creat code tabel with correct key, keys[]= {0,1,2,3,4,5,6,7,8,9,*,#}*/)
        var keyChar: Char = key.toChar()
        TODO(/*if(we have key, Call Key)*/)
        return keyChar
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