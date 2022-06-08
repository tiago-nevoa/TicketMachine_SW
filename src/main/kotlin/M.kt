import isel.leic.UsbPort

//input Locations
const val M_LOCATION : Int = 0x010

class M {

    // Inicia a classe
    fun init(){
      return
    }

    // Retorna true a chave manutecao estiver activa.
    fun maintenanceActive(): Boolean {
        return HAL.isBit(M_LOCATION)
    }
}