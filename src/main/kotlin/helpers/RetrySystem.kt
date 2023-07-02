package helpers

import exceptions.ApiRateException

class RetrySystem(
    val apiKeys: List<String>,
    val delayMilliseconds: Long = 10_000,
    val maxAttempts: Int = 10
) {
    private var nextApiKeyIndex = 0

    fun <T> retry(fn: (apiKey: String) -> T): T {
        repeat(maxAttempts) {
            repeat(apiKeys.count()) {
                try {
                    return fn(getNextApiKey())
                } catch (e: ApiRateException) {
                    println("API rate limit reached for API key #$nextApiKeyIndex...")
                }
            }
            println("API rate limit reached for all API keys, waiting ${delayMilliseconds}ms before retrying...")
            Thread.sleep(delayMilliseconds)
        }
        throw Exception("Failed after $maxAttempts attempts.")
    }

    private fun getNextApiKey(): String {
        val key: String = apiKeys[nextApiKeyIndex]
        incrementNextApiKey()
        return key
    }

    private fun incrementNextApiKey() {
        nextApiKeyIndex = (nextApiKeyIndex + 1) % apiKeys.count()
    }
}
