package helpers

import domain.Currency
import java.time.LocalDate

interface StockApi {
    fun value(symbol: String, currency: Currency, date: LocalDate): Double
}
