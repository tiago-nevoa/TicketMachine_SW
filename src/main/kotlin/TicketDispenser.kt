import isel.leic.UsbPort
import TicketDispenser

fun main() {
    var ticketDispenser = TicketDispenser()
    ticketDispenser.print(0b1110, 0b0010, false)
    ticketDispenser.init()
    ticketDispenser.print(0b0010, 0b1110, false)
    ticketDispenser.init()
    ticketDispenser.print(0b1001, 0b0110, true)
    ticketDispenser.init()
}

class TicketDispenser {
    private var serialEmitter = SerialEmitter()
    private var data : Int = 0b0

    fun init() {
        data = 0b0
    }

    fun print(destinyId: Int, originId: Int, roundTrip: Boolean) {
        val roundTripBit:Int = if(roundTrip) 1 else 0

        // se a trama estivesse na ordem igual ao enunciado:
        //data = data shl 8
        //data = data or (destinyId shl 4)
        //data = data or originId

        data = data or roundTripBit // RT indica tipo de bilhete. Fica no bit de menor peso
        data = data or (originId shl 5) // origin fica no meio
        data = data or (destinyId shl 1)
        serialEmitter.send(SerialEmitter.Destination.TICKET_DISPENSER, data)
    }

}

