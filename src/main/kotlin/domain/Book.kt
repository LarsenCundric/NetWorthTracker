package domain

// We could check that all the accounts in accounts refer
// to an institution included in institutions
class Book(
    val institutions: List<Institution>,
    val accounts: List<Account>,
    val riskLevels: List<RiskLevel>,
    val tags: List<Tag>,
    val currencies: Set<Currency>,
    val mainCurrency: Currency
) {
    fun accountsInInstitution(institution: Institution): List<Account> =
        accounts.filter { acc -> acc.institution == institution }

    fun accountsByRiskLevel(riskLevel: RiskLevel): List<Account> =
        accounts.filter { acc -> acc.metadata.riskLevel == riskLevel }
}
