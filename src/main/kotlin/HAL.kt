import isel.leic.UsbPort

object HAL { // visualizes the access to UsbPort system (HAL == Hardware Abstraction Layer)
    // class initializer
    var lastWrittenValue = 0b00000000

    const val FULL_MASK = 0xFF

    fun init() {
        lastWrittenValue = 0b00000000// initial value (each time we init() / reset we set the value to this)
        UsbPort.write(lastWrittenValue)

    }

    // returns true if bit has the logical value 1
    fun isBit(mask: Int): Boolean {
        val currentOut = UsbPort.read() // read the usbPort INPUT (entrada) (Switch) --- leds are the output (saída) -> não há interligação entre leds e switches **
        return mask == currentOut and mask
    }

    // returns the values of the bits represented by mask present in the UsbPort
    fun readBits(mask: Int): Int {
        val currentOut = UsbPort.read() // read the usbPort INPUT (Switch)
        return mask and currentOut
    }

    // writes the input value in the bits represented by mask
    fun writeBits(mask: Int, value: Int) {
        val bitsToChange = mask and value // Select the bits we need to change
        lastWrittenValue = lastWrittenValue or bitsToChange
        UsbPort.write(lastWrittenValue)
    }

    // sets the bits sent by mask to the logical value '1'
    fun setBits(mask: Int) { // não usamos read -> guardamos numa variável o último valor que foi escrito (pois não conseguimos ler o que escrevemos!)
        val value = mask or lastWrittenValue
        writeBits(FULL_MASK, value)
    }

    // sets the bits sent by mask to the logical value '0'
    fun clrBits(mask: Int) {
        val maskNANDValue = ((mask and lastWrittenValue).inv())
        val value = (maskNANDValue and lastWrittenValue)
        writeBits(FULL_MASK, value)
    }
}
