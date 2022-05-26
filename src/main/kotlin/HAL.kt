import isel.leic.UsbPort

// visualizes the access to UsbPort system (HAL == Hardware Abstraction Layer)
object HAL {
    // class initializer
    var lastWrittenValue = 0b00000000
    const val FULL_MASK = 0xFF

    fun init() {
        // initial value (each time we init() we set the value to this)
        lastWrittenValue = 0b00000000
        // send value to HW (saida/leds)
        UsbPort.write(lastWrittenValue)
    }

    // returns true if bit has the logical value 1
    fun isBit(mask: Int): Boolean {
        // read value from HW (entrada/Switch)
        val currentOut = UsbPort.read()
        return mask == currentOut and mask
    }

    // returns the values of the bits represented by mask present in the UsbPort
    fun readBits(mask: Int): Int {
        val currentOut = UsbPort.read()
        return mask and currentOut
    }

    // writes the input value in the bits represented by mask
    fun writeBits(mask: Int, value: Int) {
        // Mux with Mask as selector: When Mask = 1 then value, Mask = 0 then lastWrittenValue
        lastWrittenValue = (value and mask) or (lastWrittenValue and mask.inv())
        UsbPort.write(lastWrittenValue)
    }

    // sets the bits sent by mask to the logical value '1'
    fun setBits(mask: Int) {
        lastWrittenValue = mask or lastWrittenValue
        writeBits(FULL_MASK, lastWrittenValue)
    }

    // sets the bits sent by mask to the logical value '0'
    fun clrBits(mask: Int) {
        // lastWrittenValue and not mask: when mask = 1 then we clear all the bits
        // else when mask = 0 we keep the previous value written
        lastWrittenValue = mask.inv() and lastWrittenValue
        writeBits(FULL_MASK, lastWrittenValue)
    }
    fun timeLapse(milliSeconds: Long) {
        println("${milliSeconds / 1000.0} seconds...")
        Thread.sleep(milliSeconds)
    }
}
