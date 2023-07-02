package domain

import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import java.time.LocalDate

open class MonetaryBalance(account: Account, private val value: Double) : Balance(account) {
    private val currencyConverter: CurrencyConverter by DI.global.instance()

    override fun toValue(currency: Currency, date: LocalDate) =
        currencyConverter.convert(
            value,
            account.currency.id,
            currency.id,
            date
        )
}
