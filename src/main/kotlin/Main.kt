import isel.leic.UsbPort

fun main() {
    HAL.init()
    var mask = 0b00000001
    while(mask <= 0b11111111) {
        // Thread.sleep(1000)
        HAL.setBits(mask)
        mask *=2
    }
    while(mask > 0b00000000) {
        // Thread.sleep(1000)
        HAL.clrBits(mask)
        mask /=2
    }
}

object HAL { // visualizes the access to UsbPort system (HAL == Hardware Abstraction Layer)
    var value = 0b00000000
    // class initializer
    fun init() {
        value = 0b00000000 // write to board
    }

    // returns true if bit has the logical value 1
    fun isBit(mask: Int) : Boolean {
        val currentOut = UsbPort.read() // read the usbPort INPUT (Switch)
        return mask == currentOut and mask
    }

    // returns the values of the bits represented by mask present in the UsbPort
    fun readBits(mask: Int) : Int {
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
        value = mask or value
    }

     // sets the bits sent by mask to the logical value '0'
    fun clrBits(mask: Int) {
        val maskNANDValue = ((mask and value).inv())
        value = (maskNANDValue and value)
    }
}