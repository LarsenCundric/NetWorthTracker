package domain

import java.time.LocalDate

class Snapshot(val date: LocalDate, val balances: Set<Balance>) {
    constructor(date: String, balances: Set<Balance>) : this(LocalDate.parse(date), balances)

    fun accountBalance(account: Account): Balance? =
        balances.find { it.account == account }
}
