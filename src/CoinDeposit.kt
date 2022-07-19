
object CoinDeposit {
    var coinAmounts : HashMap<Int?, Int?> = hashMapOf()
    var coinValues = hashMapOf('0' to 5, '1' to 10, '2' to 20, '3' to 50, '4' to 100, '5' to 200, '6' to 0,'7' to 500)
    fun init() {
        FileAccess.init() // todo
        readFile()
    }

    // used in maintenance mode
    fun readFile() {
        val lines = FileAccess.readLines("CoinDeposit.txt")
        for (line in lines) {
            val values = line.split(';') // coin value;coin count
            coinAmounts[values[0].toInt()] = values[1].toInt()
        }
    }

    // used in maintenance mode
    fun resetCounter() {
        for ((key, _) in coinAmounts)  coinAmounts[key] = 0
        updateToFile()
    }

    // used after purchase
    fun updateToFile() {
        val pw = FileAccess.createWriter("CoinDeposit.txt")
        // formato coin;count (50;0)
        for(coin in coinAmounts) pw.println("${coin.key};${coin.value}")
        // close write process
        pw.close()
    }
}