import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import domain.Book
import domain.CurrencyConverter
import domain.Snapshot
import domain.StocksBalance
import helpers.AlphaVantageApi
import helpers.CacheReader
import helpers.Formatter
import helpers.StockApi
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.conf.global
import reports.TerminalReport
import reports.TerminalReportOptions
import reports.WebReport

class NetWorthTracker(
    private val book: Book,
    private val snapshots: List<Snapshot>
) : CliktCommand() {
    private val displayTotalsByRiskLevel by option(help = "Display totals by risk level").flag(default = true)
    private val displayTags by option(help = "Display tags for each account").flag() // false by default
    private val filterByTag by option(help = "Filter by tag")
        .choice(book.tags.joinToString(",") { it.name })
        .convert { selectedTagName -> book.tags.find { it.name == selectedTagName }!! }
    val web by option(help = "Start a web server").flag()

    override fun run() {
        val (stocks, cryptos) = stockAndCryptoSymbols(snapshots)

        DI.global.addConfig {
            bindSingleton { Config() }
            bindSingleton { Formatter() }
            bindSingleton { CacheReader(".cache") }
            bindSingleton { CurrencyConverter() }
            bindSingleton<StockApi> { AlphaVantageApi(stocks, cryptos) }
        }

        val options = TerminalReportOptions(
            displayTotalsByRiskLevel = displayTotalsByRiskLevel,
            displayTags = displayTags,
            filterByTag = filterByTag
        )

        if (web) {
            WebReport(book, snapshots).display()
        } else {
            TerminalReport(book, snapshots, options).displayAsTable()
        }
    }

    private fun stockAndCryptoSymbols(snapshots: List<Snapshot>): Pair<Set<String>, Set<String>> {
        val stocks = mutableSetOf<String>()
        val cryptos = mutableSetOf<String>()
        for (snapshot in snapshots) {
            for (balance in snapshot.balances) {
                if (balance is StocksBalance) {
                    val symbols = balance.stockAmounts.keys
                    if (balance.isCrypto) {
                        cryptos.addAll(symbols)
                    } else {
                        stocks.addAll(symbols)
                    }
                }
            }
        }
        return Pair(stocks, cryptos)
    }
}
