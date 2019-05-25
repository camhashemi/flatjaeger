package flathunter.script

import email.Result
import email.SendGridEmailClient
import flathunter.FlatHunter
import flathunter.FlatRequest
import flathunter.createRequest
import huntpage.buildHuntPage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import model.Email
import model.Flat
import org.slf4j.LoggerFactory
import java.io.File

@FlowPreview
fun main(args: Array<String>) {
    loadProperties(filename = args[0])
    val parameterMap = { key: String -> System.getProperty(key) }
    runBatchSearch(request = createRequest(parameterMap))
}

data class ScriptResult(val numFlats: Int, val emailSuccess: Boolean)

@FlowPreview
fun runScript(request: FlatRequest): ScriptResult {
    val flats = runSearch(request)
        .also { logger.info("fetched ${it.size} flats") }

    val response = sendEmail(flats)
        .also { logger.info(if (it is Result.Success) "email sent" else "email failed") }

    return ScriptResult(flats.size, response is Result.Success)
}

private fun loadProperties(filename: String) {
    File(filename)
        .readLines()
        .filter { it.matches("^[^#].+=.*".toRegex()) }
        .map { it.split("=", limit = 2) }
        .forEach { (key, value) -> System.setProperty(key, value) }
}

@FlowPreview
private fun runSearch(request: FlatRequest): List<Flat> =
    runBlocking {
        withTimeout(600_000) {
            getFlats(request)
        }
    }

@FlowPreview
private fun runBatchSearch(request: FlatRequest): Map<FlatRequest, List<Flat>> =
    runBlocking {
        withTimeout(600_000) {
            FlatHunter.batchSearch(
                listOf(request, request)
            )
        }
    }

@FlowPreview
private suspend fun getFlats(request: FlatRequest): List<Flat> =
    FlatHunter.fullSearch(request)

private fun sendEmail(flats: List<Flat>): Result {
    val emailClient = SendGridEmailClient(System.getenv("sendGridApiKey"))
    return emailClient.sendEmail(
        from = Email("cam@camhashemi.com"),
        to = Email("camhashemi@gmail.com"),
        subject = "Today's Hunt",
        content = buildHuntPage(flats)
    )
}

private val logger = LoggerFactory.getLogger("")
