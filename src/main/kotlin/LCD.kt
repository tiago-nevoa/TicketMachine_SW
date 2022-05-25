
private const val LINES : Int = 2
private const val COLS : Int = 16

class LCD {
    private var serialEmitter = SerialEmitter()
    private var data : Int = 0b0
    private var cursorLine = 0
    private var cursorColumn = 0

    private fun writeByteParallel(rs: Boolean, data: Int){

    }

    private fun writeByteSerial(rs: Boolean, data: Int){

    }

    private fun writeByte(rs: Boolean, data: Int){

    }

    private fun writeCMD(data: Int){

    }

    private fun writeData(data: Int){

    }

    fun init(){

    }
    fun write(c: Char){
        serialEmitter.send(SerialEmitter.Destination.LCD,data)
    }

    fun write(text: String){

    }

    fun cursor(line: Int, column: Int){

    }

    fun clean(){

    }

}