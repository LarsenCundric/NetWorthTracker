package helpers

import domain.Balance
import domain.Currency
import java.time.LocalDate

class Formatter {
    fun formatAmount(amount: Double) = String.format("%,.0f", amount)

    fun formatBalanceToMainCurrencyOnDate(balance: Balance?, mainCurrency: Currency, date: LocalDate) =
        if (balance == null) {
            "-"
        } else {
            formatAmount(balance.toValue(mainCurrency, date))
        }

    fun formatAmountAndPercentage(amount: Double, percentage: Double) =
        String.format("%,.0f (%2.0f%%)", amount, percentage * 100.0)
}
