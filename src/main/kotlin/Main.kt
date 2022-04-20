import isel.leic.UsbPort

fun main(args: Array<String>) {
    println("Hello World!")
    while(true){
        val value = UsbPort.read()
        UsbPort.write(value)
        // Programming in MacIos and git to gitHub
    }
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}