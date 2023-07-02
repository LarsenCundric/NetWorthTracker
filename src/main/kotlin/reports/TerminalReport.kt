package reports

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.ColumnWidth
import com.github.ajalt.mordant.table.TableBuilder
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import domain.Account
import domain.Book
import domain.Institution
import domain.RiskLevel
import domain.Snapshot
import helpers.Formatter
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import java.time.format.DateTimeFormatter

// A more robust version of our program could check that the
// snapshots we provide to a report only contain balances
// for accounts that exist in the book, or else our program will fail
class TerminalReport(
    val book: Book,
    val snapshots: List<Snapshot>,
    val options: TerminalReportOptions = TerminalReportOptions()
) {
    private val formatter: Formatter by DI.global.instance()

    fun displayAsTable() {
        val table = table {
            borderType = BorderType.SQUARE_DOUBLE_SECTION_SEPARATOR
            column(0) { width = ColumnWidth.Fixed(4) }
            column(1) { width = ColumnWidth.Fixed(35) }

            headerRows()
            accountsRows()
            if (options.displayTotalsByRiskLevel) {
                totalByRiskLevelRows()
            }
            footerRows()
        }

        // Force terminal to use our colors
        Terminal(AnsiLevel.TRUECOLOR).println(table)
    }

    private fun TableBuilder.headerRows() {
        val dateFormat = DateTimeFormatter.ofPattern("MMM dd, u")
        header {
            row {
                cell("") {
                    columnSpan = 2
                }
                for (snapshot in snapshots) {
                    cell(snapshot.date.format(dateFormat))
                }
            }
        }
    }

    private fun TableBuilder.accountsRows() {
        body {
            for (institution in book.institutions) {
                val accounts = accountsInInstitution(institution)
                if (accounts.isEmpty()) continue // We don’t display institutions with no accounts

                row {
                    cell(institution.name) {
                        columnSpan = Integer.MAX_VALUE
                    }
                }
                for (account in accounts) {
                    row {
                        cellBorders = Borders.LEFT_RIGHT
                        cell(account.metadata.riskLevel?.symbol ?: "")
                        var accountLabel = account.name
                        if (options.displayTags && account.metadata.tags.isNotEmpty()) {
                            val tagList = account.metadata.tags.map { it.name }.toList()
                            accountLabel += " " + TextColors.gray("(" + tagList.joinToString(", ") + ")")
                        }
                        cell(accountLabel)
                        for (snapshot in snapshots) {
                            val balance = snapshot.accountBalance(account)
                            val amount =
                                formatter.formatBalanceToMainCurrencyOnDate(balance, book.mainCurrency, snapshot.date)
                            cell(amount) {
                                align = TextAlign.RIGHT
                            }
                        }
                    }
                }
            }
        }
    }

    private fun TableBuilder.totalByRiskLevelRows() {
        body {
            row {
                cell("TOTAL BY RISK LEVEL") {
                    columnSpan = Integer.MAX_VALUE
                }
            }
            for (riskLevel in book.riskLevels) {
                row {
                    cellBorders = Borders.LEFT_RIGHT
                    cell("${riskLevel.symbol} ${riskLevel.name}") { columnSpan = 2 }
                    for (snapshot in snapshots) {
                        val total = snapshotTotalForRiskLevel(snapshot, riskLevel)
                        val percent = snapshotPercentageForRiskLevel(snapshot, riskLevel)
                        cell(formatter.formatAmountAndPercentage(total, percent)) {
                            align = TextAlign.RIGHT
                        }
                    }
                }
            }
        }
    }

    private fun accountMatchesFilterByTag(account: Account) =
        options.filterByTag == null || account.metadata.tags.contains(options.filterByTag)

    private fun accountsInInstitution(institution: Institution): List<Account> {
        val accounts = book.accountsInInstitution(institution)
        return accounts.filter { accountMatchesFilterByTag(it) }
    }

    private fun TableBuilder.footerRows() {
        footer {
            row {
                cell("TOTAL") {
                    columnSpan = 2
                }
                for (snapshot in snapshots) {
                    cell(formatter.formatAmount(snapshotTotal(snapshot))) {
                        align = TextAlign.RIGHT
                    }
                }
            }
        }
    }

    private fun snapshotTotal(snapshot: Snapshot) =
        snapshot.balances.sumOf {
            if (accountMatchesFilterByTag(it.account)) {
                it.toValue(book.mainCurrency, snapshot.date)
            } else {
                0.0
            }
        }

    private fun snapshotTotalForRiskLevel(snapshot: Snapshot, riskLevel: RiskLevel) =
        snapshot.balances.sumOf {
            if (it.account.metadata.riskLevel == riskLevel && accountMatchesFilterByTag(it.account)) {
                it.toValue(book.mainCurrency, snapshot.date)
            } else {
                0.0
            }
        }

    private fun snapshotPercentageForRiskLevel(snapshot: Snapshot, riskLevel: RiskLevel) =
        snapshotTotalForRiskLevel(snapshot, riskLevel) / snapshotTotal(snapshot)
}
