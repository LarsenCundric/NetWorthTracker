package domain

class Account(
    val institution: Institution,
    val name: String,
    val currency: Currency,
    val metadata: AccountMetadata = AccountMetadata()
) {
    fun monetaryBalance(value: Double) = MonetaryBalance(this, value)

    fun monetaryBalance(value: Int) = monetaryBalance(value.toDouble())

    fun stocksBalance(stockAmounts: Map<String, Double>) = StocksBalance(this, stockAmounts, false)

    fun cryptosBalance(cryptoAmounts: Map<String, Double>) = StocksBalance(this, cryptoAmounts, true)
}
