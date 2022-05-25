import kotlin.test.todo

private const val LINES : Int = 2
private const val COLS : Int = 16
private const val CLEAR_DISPLAY : Int = 0x001
private const val RETURN_HOME : Int = 0x001

class LCD {
    private var serialEmitter = SerialEmitter()
    private var frame : Int = 0b0
    private var cursorLine = 0
    private var cursorColumn = 0

    private fun writeByteParallel(rs: Boolean, data: Int){
        //frame{data[0..7],roundTrip}
        frame = if(rs) 1 else 0
        frame = (data shl 1) or frame
        println("frame on LCD: " + Integer.toBinaryString(data))
        serialEmitter.init()
        println("serialEmitter init...")
        serialEmitter.send(SerialEmitter.Destination.LCD, frame)

        TODO("check the difference from writeByteParallel,writeByteSerial,writeByte")
    }

    private fun writeByteSerial(rs: Boolean, data: Int){
        TODO("check the difference from writeByteParallel,writeByteSerial,writeByte")
    }

    private fun writeByte(rs: Boolean, data: Int){
        TODO("check the difference from writeByteParallel,writeByteSerial,writeByte")
    }

    private fun writeCMD(data: Int){
        writeByte(false,data)
    }

    private fun writeData(data: Int){
        writeByte(true,data)
    }

    fun init(){

    }
    fun write(c: Char){
        writeData(c.code)
    }

    fun write(text: String){
        text.map {c -> write(c)}
    }

    fun cursor(line: Int, column: Int){
        // Error scenario
        if (line >= LINES || column >= COLS) return
        // Instuction (Set DDRAM address) set cursor on the address position, D7=1,D6..0 = cursor address
        // 0x80 put D7=1, when line difference 0 we jump 0x40 positions
        val data = 0x80 or (line*0x40 + column)
        writeCMD(data)

    }

    fun clean(){
        writeCMD(CLEAR_DISPLAY)
    }

}