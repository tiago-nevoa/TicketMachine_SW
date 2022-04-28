import isel.leic.UsbPort

class SerialEmitter {
    enum class Destination { LCD, TICKET_DISPENSER }
    var busyLocation : Int = 0x??
    // class initializer
    fun init() {

    }

    // sends block to SerialReceiver identifying the destination in addr field and data bits in data field
    fun send(addr: Destination, data: Int) {
        if(addr == Destination.LCD) // send data to LCD
            else if (addr == Destination.TICKET_DISPENSER) // send data to Ticket Dispenser
                else // init()
    }

    // return true if channel serial is busy (serial receiver - comes fom VHDL)
    fun isBusy() : Boolean {
        return HAL.isBit(busyLocation)
    }
}