package main.kotlin

class CoinDeposit {
    var coinAmounts : HashMap<Int?, Int?> = hashMapOf()

    fun init() {
        readFile()
    }

    // used in maintenance mode
    fun readFile() {
        val fileAccess = FileAccess()
        val lines = fileAccess.readLines("CoinDeposit.txt")
        for (line in lines) {
            val values = line.split(';') // coin value;coin count
            coinAmounts[values[0].toInt()] = values[1].toInt()
        }
    }

    // used in maintenance mode
    fun resetCounter() {
        for ((key, _) in coinAmounts) {
            coinAmounts[key] = 0
        }
        updateToFile()
    }

     // used in maintenance mode
    fun countCoins() {
        for ((key, _) in coinAmounts) {
            coinAmounts[key] = 0
        }
        updateToFile()
    }

    // used after purchase
    fun updateToFile() {
        val fileAccess = FileAccess()
        val pw = fileAccess.createWriter("CoinDeposit.txt")
        for(coin in coinAmounts) {
            pw.println("${coin.key};${coin.value}") // formato coin;count (50;0)
        }
        pw.close() // fechar processo de escrita
    }

}