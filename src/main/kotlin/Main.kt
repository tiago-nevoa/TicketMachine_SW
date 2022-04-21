import isel.leic.UsbPort;

fun main(args: Array<String>) {
    HAL.init()
    var mask = 0b00000000
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
        value = 0b00000000
    }

    // returns true if bit has the logical value 1
    fun isBit(mask: Int) : Boolean {
        return mask == value and mask
    }

    // returns the values of the bits represented by mask present in the UsbPort
    fun readBits(mask: Int) : Int {
        return mask and value
    }

    // writes the input value in the bits represented by mask
    fun writeBits(mask: Int, value: Int) {
        UsbPort.write(mask and value) // confirm with professor
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