package reports

import com.fasterxml.jackson.module.kotlin.jsonMapper
import domain.Book
import domain.Snapshot
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class WebReport(private val book: Book, private val snapshots: List<Snapshot>) {
    fun display() {
        embeddedServer(
            Netty,
            host = "localhost",
            port = 8080,
            module = {
                install(FreeMarker) {
                    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
                    outputFormat = HTMLOutputFormat.INSTANCE
                }

                routing {
                    get("/") {
                        call.respond(
                            FreeMarkerContent(
                                "report.ftl",
                                mapOf(
                                    "title" to "Net Worth Tracker",
                                    "book" to book,
                                    "snapshots" to snapshots,
                                    "snapshotTotals" to snapshotTotals(),
                                    "chartDataByAccountJson" to jsonMapper().writeValueAsString(chartDataByAccount())
                                )
                            )
                        )
                    }
                }
            }
        ).start(wait = true)
    }

    private fun snapshotTotals(): Map<String, Double> {
        val totals = mutableMapOf<String, Double>()
        for (snapshot in snapshots) {
            totals[snapshot.date.toString()] = snapshotTotal(snapshot)
        }
        return totals

    }

    private fun snapshotTotal(snapshot: Snapshot) =
        snapshot.balances.sumOf { balance ->
            balance.toValue(book.mainCurrency, snapshot.date)
        }

    private fun chartDataByAccount(): List<List<*>> {
        val accounts = book.institutions.flatMap { book.accountsInInstitution(it) }
        val headers = listOf("Date") + accounts.map { it.name } + listOf("Total")
        val rows = snapshots.map { snapshot ->
            val accountRows = accounts.map {
                snapshot.accountBalance(it)?.toValue(book.mainCurrency, snapshot.date)
            }
            listOf(snapshot.date.toString() + "T00:00:00") + accountRows + listOf(snapshotTotal(snapshot))
        }
        return listOf(headers) + rows
    }
}
