import isel.leic.UsbPort

fun main() {
    HAL.init()
    var mask = 0x0F
    println(mask)

    /*while(!HAL.isBit(mask)) {
        Thread.sleep(500)
        HAL.setBits(mask)
        mask *=2
    }*/
    HAL.lastWrittenValue = 0xF0
    // println(HAL.lastWrittenValue)

    // HAL.clrBits(mask)
    HAL.writeBits(0x0F, 0b00011011)
}