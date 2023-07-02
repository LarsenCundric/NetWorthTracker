
import domain.Account
import domain.AccountMetadata
import domain.Book
import domain.Currency
import domain.Institution
import domain.RiskLevel
import domain.Snapshot
import domain.Tag

fun main(args: Array<String>) {
    // Risk levels
    val cash = RiskLevel("Cash", "\uD83D\uDFE2") // green circle
    val realEstate = RiskLevel("Real Estate", "\uD83D\uDFE1") // yellow
    val lowRisk = RiskLevel("Low Risk", "\uD83D\uDFE0") // orange circle
    val highRisk = RiskLevel("High Risk", "\uD83D\uDD34") // red circle

    // Tags
    val downPayment = Tag("Down Payment")

    // Institutions
    val gnb = Institution("Goliath National Bank")
    val stocksAndCryptos = Institution("Stocks & Cryptos")

    // Currencies
    val cad = Currency("CAD", "$")
    val usd = Currency("USD", "US$")

    // Accounts
    val checkingAccount = Account(gnb, "Checking", cad, AccountMetadata(cash))
    val savingsAccount =
        Account(gnb, "Savings", cad, AccountMetadata(lowRisk, setOf(downPayment)))
    val usdAccount = Account(gnb, "Savings USD", usd, AccountMetadata(cash))
    val stockAccount = Account(stocksAndCryptos, "Stock account", usd, AccountMetadata(highRisk))
    val cryptoAccount = Account(stocksAndCryptos, "Crypto account", usd, AccountMetadata(highRisk))

    val institutions = listOf(gnb, stocksAndCryptos)
    val accounts = listOf(checkingAccount, savingsAccount, usdAccount, stockAccount, cryptoAccount)
    val riskLevels = listOf(cash, realEstate, lowRisk, highRisk)
    val tags = listOf(downPayment)
    val currencies = setOf(cad, usd)
    val book = Book(institutions, accounts, riskLevels, tags, currencies, cad)

    val snapshots = listOf(
        Snapshot(
            "2023-03-01",
            setOf(
                checkingAccount.monetaryBalance(400),
                savingsAccount.monetaryBalance(1800),
                stockAccount.stocksBalance(mapOf("TSLA" to 1.0))
            )
        ),
        Snapshot(
            "2023-04-01",
            setOf(
                checkingAccount.monetaryBalance(500),
                savingsAccount.monetaryBalance(2000),
                usdAccount.monetaryBalance(1000),
                stockAccount.stocksBalance(mapOf("TSLA" to 2.5, "AAPL" to 1.0)),
                cryptoAccount.cryptosBalance(mapOf("BTC" to 0.01))
            )
        ),
        Snapshot(
            "2023-05-01",
            setOf(
                checkingAccount.monetaryBalance(600),
                savingsAccount.monetaryBalance(2500),
                usdAccount.monetaryBalance(1000),
                stockAccount.stocksBalance(mapOf("TSLA" to 7.0, "AAPL" to 2.0, "VICI" to 15.0)),
                cryptoAccount.cryptosBalance(mapOf("BTC" to 0.01, "ETH" to 0.1))
            )
        )
    )

    NetWorthTracker(book, snapshots).main(args)
    println("Args: ${args.joinToString(", ")}")
}
