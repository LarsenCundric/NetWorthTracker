package helpers

import java.io.File

class CacheReader(val cacheDirectory: String) {
    fun readOrFetch(cacheKey: String, fetch: () -> String): String {
        val cacheFile = File("$cacheDirectory/$cacheKey")
        if (cacheFile.exists()) {
            return cacheFile.readText()
        }
        val text = fetch()
        cacheTextInFile(text, cacheFile)
        return text
    }

    private fun cacheTextInFile(text: String, cacheFile: File) {
        File(cacheDirectory).mkdirs()
        cacheFile.writeText(text)
    }
}
