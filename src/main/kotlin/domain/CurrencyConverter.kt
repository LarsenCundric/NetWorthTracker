package domain

import Config
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate

class CurrencyConverter(val baseCurrencyId: String = "USD") {
    private val config: Config by DI.global.instance()

    fun convert(amount: Double, from: String, to: String, date: LocalDate) =
        if (from == to) {
            amount
        } else {
            amount * rateForCurrencyAtDate(date, from, to)
        }

    private fun rateForCurrencyAtDate(date: LocalDate, from: String, to: String): Double {
        val key = Triple(date, from, to)
        val existingRate = rates[key]
        if (existingRate != null) {
            return existingRate
        }

        val apikey = config.currencyGetGeoApiKey
        val url =
            "${config.currencyGetGeoApiBaseUrl}/currency/historical/$date?" +
                "api_key=$apikey&from=$from&to=$to"

        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        val client = HttpClient.newBuilder().build()
        val json = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        if (requestFailed(json)) {
            throw Exception("Invalid response")
        }

        val result =
            jacksonObjectMapper().readValue(json, CurrencyGetGeoApiHistoricalResponse::class.java)
        val currencyRate = result.rates[to]!!.rate
        rates[key] = currencyRate

        return currencyRate
    }

    private val rates: MutableMap<Triple<LocalDate, String, String>, Double> = mutableMapOf()

    private fun requestFailed(json: String) = json.contains("\"status\":\"failed\"")

    @JsonIgnoreProperties(ignoreUnknown = true)
    private class CurrencyGetGeoApiHistoricalResponse(
        val rates: Map<String, CurrencyGetGeoApiHistoricalResponseRates>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private class CurrencyGetGeoApiHistoricalResponseRates(val rate: Double)
}
