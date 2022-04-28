import isel.leic.UsbPort

fun main() {
    HAL.init()
    var mask = 0b00000001
    while(!HAL.isBit(mask)) {
        // Thread.sleep(1000)
        HAL.setBits(mask)
        mask *=2
    }
}