import kotlin.test.todo

private const val LINES : Int = 2
private const val COLS : Int = 16
private const val CLEAR_DISPLAY : Int = 0x001
private const val RETURN_HOME : Int = 0x002
private const val DISPLAY_OFF : Int = 0x008
// 0x00E without cursor blink, 0x00F with cursor blink
private const val DISPLAY_ON : Int = 0x00F

class LCD {
    private var serialEmitter = SerialEmitter()
    private var frame : Int = 0b0
    private var cursorLine = 0x0
    private var cursorColumn = 0x0

    private fun writeByteSerial(rs: Boolean, data: Int){
        //frame{data[0..7],RS}
        frame = if(rs) 1 else 0
        frame = (data shl 1) or frame
        println("frame on LCD: " + Integer.toBinaryString(data))
        serialEmitter.init()
        println("serialEmitter init...")
        serialEmitter.send(SerialEmitter.Destination.LCD, frame)
    }

    private fun writeByte(rs: Boolean, data: Int){
       writeByteSerial(rs,data)
    }

    private fun writeCMD(data: Int){
        writeByte(false,data)
        HAL.timeLapse(1)
    }

    private fun writeData(data: Int){
        writeByte(true,data)
        HAL.timeLapse(1)
    }

    fun init(){
        HAL.timeLapse(100)
        writeCMD(0x30)
        HAL.timeLapse(10)
        writeCMD(0x30)
        writeCMD(0x30)
        writeCMD(0x38)
        writeCMD(DISPLAY_OFF)
        writeCMD(CLEAR_DISPLAY)
        writeCMD(0x06)
        writeCMD(DISPLAY_ON)
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
        cursorLine = line
        cursorColumn = column
        // Instuction (Set DDRAM address) set cursor on the address position, D7=1,D6..0 = cursor address
        // 0x80 put D7=1, when line difference 0 we jump 0x40 positions
        val data = 0x80 or (line*0x40 + column)
        writeCMD(data)
    }

    fun clean(){
        cursorLine = 0
        cursorColumn = 0
        writeCMD(CLEAR_DISPLAY)
    }

}