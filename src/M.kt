
//input Locations
const val M_LOCATION : Int = 0x80
const val OPTION_1 = "1-Print Ticket"
const val OPTION_2 = "2-Station Cnt."
const val OPTION_3 = "3-Coins Cnt."
const val OPTION_4 = "4-Reset Cnt."
const val OPTION_5 = "5-Shutdown"

object M {
    // Inicia a classe
    fun init(){
      return
    }

    // Retorna true se chave manutecao estiver activa.
    fun maintenanceActive(): Boolean {
        return HAL.isBit(M_LOCATION)
    }

    fun maintenanceOptionsMenu(choice : Int) : String {
        return when(choice) {
            1 -> OPTION_1
            2 -> OPTION_2
            3 -> OPTION_3
            4 -> OPTION_4
            5 -> OPTION_5
            else -> ""
        }
    }
}