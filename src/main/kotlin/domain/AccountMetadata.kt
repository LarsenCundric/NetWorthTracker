package domain

class AccountMetadata(
    val riskLevel: RiskLevel? = null,
    val tags: Set<Tag> = emptySet()
)
