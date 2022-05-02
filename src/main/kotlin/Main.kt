import isel.leic.UsbPort

fun main() {

    println("Hal init...")
    HAL.init()

    var mask = 0x0F
    println("mask =" + mask)

    HAL.lastWrittenValue = 0xF0
    println("lastWrittenValue =" + HAL.lastWrittenValue)

    // HAL.clrBits(mask)
    //HAL.writeBits(0x0F, 0b00011011)
}

/*while(!HAL.isBit(mask)) {
       Thread.sleep(500)
       HAL.setBits(mask)
       mask *=2
   }*/