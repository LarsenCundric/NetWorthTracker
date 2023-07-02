package domain

import java.time.LocalDate

abstract class Balance(val account: Account) {
    abstract fun toValue(currency: Currency, date: LocalDate): Double
}
