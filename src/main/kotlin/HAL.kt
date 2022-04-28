import isel.leic.UsbPort

object HAL { // visualizes the access to UsbPort system (HAL == Hardware Abstraction Layer)
    // class initializer
    // var value = 0b00000000
    fun init() {
        // value = UsbPort.read()// write to board
    }

    // returns true if bit has the logical value 1
    fun isBit(mask: Int): Boolean {
        val currentOut =
            UsbPort.read() // read the usbPort INPUT (entrada) (Switch) --- leds are the output (saída) -> não há interligação entre leds e switches **
        return mask == currentOut and mask
    }

    // returns the values of the bits represented by mask present in the UsbPort
    fun readBits(mask: Int): Int {
        val currentOut = UsbPort.read() // read the usbPort INPUT (Switch)
        return mask and currentOut
    }

    // writes the input value in the bits represented by mask
    fun writeBits(mask: Int, value: Int) {
        val currentOut = UsbPort.read() // read the usbPort INPUT (Switch)
        val bitsToChange = mask and value // Select the bits we need to change
        UsbPort.write(currentOut or bitsToChange)
    }

    // sets the bits sent by mask to the logical value '1'
    fun setBits(mask: Int) {
        val currentOut = UsbPort.read()
        val value = mask or currentOut
        UsbPort.write(value)
    }

    // sets the bits sent by mask to the logical value '0'
    fun clrBits(mask: Int) {
        val currentOut = UsbPort.read()
        val maskNANDValue = ((mask and currentOut).inv())
        val value = (maskNANDValue and currentOut)
        UsbPort.write(value)
    }
}
