package reports

import domain.Tag

class TerminalReportOptions(
    val displayTotalsByRiskLevel: Boolean = false,
    val displayTags: Boolean = false,
    val filterByTag: Tag? = null
)
