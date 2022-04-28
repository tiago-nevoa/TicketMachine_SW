import isel.leic.UsbPort

fun main() {
    HAL.init()
    while(true){
        if (HAL.isBit(0b1000_0000)) break
        
    }
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