
//input Locations
const val COIN_LOCATION : Int = 0x08
const val CIDATA_LOCATION : Int = 0x07

//output Locations
const val ACCEPT_LOCATION : Int = 0x20
const val COLLECT_LOCATION : Int = 0x40
const val EJECT_LOCATION : Int = 0x80

val COIN_TABLE : Array<Int> = arrayOf(5,10,20,50,100,200)

// Implementa a interface com o moedeiro.
class CoinAcceptor {
        private var accept = 0
        private var collect = 0
        private var eject = 0
        public var totalCoins = 0

        // Inicia a classe
        fun init() {
                accept = 0
                HAL.clrBits(ACCEPT_LOCATION)
                collect = 0
                HAL.clrBits(COLLECT_LOCATION)
                eject = 0
                HAL.clrBits(EJECT_LOCATION)
        }

        // Retorna true se foi introduzida uma nova moeda.
        fun hasCoin(): Boolean {
                return HAL.isBit(COIN_LOCATION)
        }

        // Retorna o valor facial da moeda introduzida.
        fun getCoinValue(): Int {
               return COIN_TABLE[HAL.readBits(CIDATA_LOCATION)]
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
                totalCoins = 0
                eject = 0
                HAL.clrBits(EJECT_LOCATION)
        }

        // Recolhe as moedas que estão no moedeiro.
        fun collectCoins() {
                collect = 1
                HAL.setBits(COLLECT_LOCATION)
                HAL.timeLapse(2000)
                totalCoins = 0
                collect = 0
                HAL.clrBits(COLLECT_LOCATION)
        }
}