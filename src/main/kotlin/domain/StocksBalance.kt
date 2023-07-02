package domain

import helpers.StockApi
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import java.time.LocalDate

class StocksBalance(
    account: Account,
    val stockAmounts: Map<String, Double>,
    val isCrypto: Boolean
) : Balance(account) {
    private val stockApi: StockApi by DI.global.instance()

    override fun toValue(currency: Currency, date: LocalDate) =
        stockAmounts.map { (symbol, amount) ->
            amount * stockApi.value(symbol, currency, date)
        }.sum()
}
