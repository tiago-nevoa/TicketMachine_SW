const val NONE = 0;
class KBD { // Read keys from keyboard. Methods return ‘0’..’9’,’#’,’*’ or NONE.
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
    }
    // Returns when the key was pressed or NONE after timeout’ milliseconds have passed
    fun waitKey(timeout: Long): Char {
    }
}