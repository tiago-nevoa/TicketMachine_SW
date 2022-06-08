import isel.leic.UsbPort

//input Locations
const val COIN_LOCATION : Int = 0x020
const val CIDATA_LOCATION : Int = 0x1C0

//output Locations
const val ACCEPT_LOCATION : Int = 0x020
const val COLLECT_LOCATION : Int = 0x080
const val EJECT_LOCATION : Int = 0x040

val COIN_TABLE : Array<Int> = arrayOf(5,10,20,50,100,200)

// Implementa a interface com o moedeiro.
class CoinAcceptor {
        private var accept = 0
        private var collect = 0
        private var eject = 0
        var coin_value = 0

        // Inicia a classe
        fun init() {
                HAL.init()
                accept = 0
                collect = 0
                eject = 0
                coin_value = 0
        }

        // Retorna true se foi introduzida uma nova moeda.
        fun hasCoin(): Boolean {
                return HAL.isBit(COIN_LOCATION)
        }

        // Retorna o valor facial da moeda introduzida.
        fun getCoinValue(): Int {
               return COIN_TABLE[HAL.readBits(CIDATA_LOCATION) shr 6]
        }

        // Informa o moedeiro que a moeda foi contabilizada.
        fun acceptCoin() {
                accept = 1
                HAL.setBits(ACCEPT_LOCATION)
                while (hasCoin()) {/*wait*/}
                accept = 0
                HAL.clrBits(ACCEPT_LOCATION)
        }

        // Devolve as moedas que estão no moedeiro.
        fun ejectCoins() {
                eject = 1
                HAL.setBits(EJECT_LOCATION)
                HAL.timeLapse(2000)
                eject = 0
                HAL.clrBits(EJECT_LOCATION)
        }

        // Recolhe as moedas que estão no moedeiro.
        fun collectCoins() {
                collect = 1
                HAL.setBits(COLLECT_LOCATION)
                HAL.timeLapse(2000)
                collect = 0
                HAL.clrBits(COLLECT_LOCATION)
        }
}