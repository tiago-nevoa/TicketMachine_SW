import isel.leic.UsbPort
class TicketDispenser {
    private var serialEmitter = SerialEmitter()
    private var data : Int = 0b0

    fun init() {
        data = 0b0
    }

    fun print(destinyId: Int, originId: Int, roundTrip: Boolean) {
        // data{orig[0..3],dest[0..3],roundTrip}
        data = if(roundTrip) 1 else 0
        data = data or (destinyId shl 1)
        data = data or (originId shl 5)

        println("data on TicketDispenser: " + Integer.toBinaryString(data))
        serialEmitter.init()
        println("serialEmitter init...")

        serialEmitter.send(SerialEmitter.Destination.TICKET_DISPENSER, data)
    }

}

