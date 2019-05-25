package flathunter

import flathunter.Constants.shouldLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Constants {
    const val immobilienScoutUrl = "https://www.immobilienscout24.de"
    var shouldLog = System.getProperty("shouldLog")?.toBoolean() ?: false
    val transitApiKey = System.getProperty("TRANSIT_API_KEY").toString()
}

val Any.logger: Logger? get() = if (shouldLog) LoggerFactory.getLogger(javaClass.canonicalName) else null
