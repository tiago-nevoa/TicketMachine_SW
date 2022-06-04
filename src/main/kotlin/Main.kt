import isel.leic.UsbPort

fun main() {

    println("Hal init...")
    HAL.init()
    println("lastWrittenValue at init= " + HAL.lastWrittenValue)
/*
    //lastWrittenValue test
    HAL.lastWrittenValue = 0xF0
    println("lastWrittenValue = " + HAL.lastWrittenValue)

    // isBit Test
    println("Testing isBit...")
    println("5sec to select input...")
    Thread.sleep(1000)
    println("4sec to select input...")
    Thread.sleep(1000)
    println("3sec to select input...")
    Thread.sleep(1000)
    println("2sec to select input...")
    Thread.sleep(1000)
    println("1sec to select input...")
    Thread.sleep(1000)
    println("starting...")
    Thread.sleep(1000)

    var mask = 0x01
    println("mask = " + mask)

    while(mask <= HAL.FULL_MASK) {
        println("At mask position: " + mask)
        if (HAL.isBit(mask)) println(":Bit is True!")
        else println(":Bit is False!")
        mask *= 2
    }

    // readBits test
    println("Testing readBits...")
    println("5sec to select input...")
    Thread.sleep(1000)
    println("4sec to select input...")
    Thread.sleep(1000)
    println("3sec to select input...")
    Thread.sleep(1000)
    println("2sec to select input...")
    Thread.sleep(1000)
    println("1sec to select input...")
    Thread.sleep(1000)
    println("starting...")
    Thread.sleep(1000)

    mask = 0x01
    println("mask = " + mask)

    while(mask <= HAL.FULL_MASK) {
        println("At mask position: " + mask)
        println(":We read: " + HAL.readBits(mask) )
        mask *= 2
    }

    // writeBits test
    HAL.lastWrittenValue = 0x01
    UsbPort.write(HAL.lastWrittenValue)
    println("lastWrittenValue = " + HAL.lastWrittenValue)
    Thread.sleep(5000)
    println("Testing writeBits...")
    println("Mask: 0x0F, Value: 0xF0")
    HAL.writeBits(0x0F,0xF0)
    Thread.sleep(5000)
    println("Mask: 0xFF, Value: 0xF0")
    HAL.writeBits(0xFF,0xF0)
    Thread.sleep(5000)
    println("Mask: 0x00, Value: 0x0F")
    HAL.writeBits(0x00,0x0F)
    Thread.sleep(5000)
    println("Mask: 0x0F, Value: 0xFF")
    HAL.writeBits(0x0F,0xFF)

    // setBits test
    HAL.lastWrittenValue = 0x56
    UsbPort.write(HAL.lastWrittenValue)
    println("lastWrittenValue = " + HAL.lastWrittenValue)
    println("Testing setBits...")
    Thread.sleep(5000)
    println("Mask: 0xF0")
    HAL.setBits(0xF0)
    Thread.sleep(5000)
    println("Mask: 0x0F")
    HAL.setBits(0x0F)

    // clrBits test
    HAL.lastWrittenValue = 0x65
    UsbPort.write(HAL.lastWrittenValue)
    println("lastWrittenValue = " + HAL.lastWrittenValue)
    println("Testing clrBits...")
    Thread.sleep(5000)
    println("Mask: 0xF0")
    HAL.clrBits(0xF0)
    Thread.sleep(5000)
    println("Mask: 0x0F")
    HAL.clrBits(0x0F)
 */
    // TicketDispenser test
    var ticketDispenser = TicketDispenser()

    ticketDispenser.print(0b0011, 0b1100, true)

    println("ticketDispenser init...")
    ticketDispenser.init()

    ticketDispenser.print(0b1011, 0b0110, true)

    println("ticketDispenser init...")
    ticketDispenser.init()

    ticketDispenser.print(0b0011, 0b1100, false)

    println("ticketDispenser init...")
    ticketDispenser.init()

    ticketDispenser.print(0b1011, 0b0110, false)

    println("ticketDispenser init...")
    ticketDispenser.init()

    var lcdEmitter = LCD()
    println("LCD init...")
    lcdEmitter.init()
    println("LCD Write String...")
    lcdEmitter.write("Grupo: 2")
    lcdEmitter.cursor(1,0)
    lcdEmitter.write("LIC_Teste")
    lcdEmitter.cursor(1,12)
    lcdEmitter.write("Done")
    HAL.timeLapse(5000)
    lcdEmitter.clean()
    lcdEmitter.cursor(0,0)
    lcdEmitter.write("Tiago, Rui")
    lcdEmitter.cursor(1,0)
    lcdEmitter.write("eeee, Margarida!")

}