import java.io.FileInputStream
import java.util.Properties

class Config {
    private val properties: Properties by lazy {
        val props = Properties()
        props.load(FileInputStream("local.properties"))
        props
    }

    val currencyGetGeoApiKey: String by lazy {
        properties.getProperty("currencyGetGeoApi.apiKey")
    }

    val currencyGetGeoApiBaseUrl: String by lazy {
        properties.getProperty("currencyGetGeoApi.baseUrl")
    }

    val alphaVantageApiKeys: List<String> by lazy {
        properties.getProperty("alphaVantage.apiKey").split(Regex(",\\s*"))
    }

    val alphaVantageBaseUrl: String by lazy {
        properties.getProperty("alphaVantage.baseUrl")
    }
}
