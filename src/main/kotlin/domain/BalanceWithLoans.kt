package domain

class BalanceWithLoans(account: Account, value: Double, loans: List<Double>) :
    MonetaryBalance(account, value - loans.sum())
