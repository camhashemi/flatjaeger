package huntcollector

import email.EmailClient
import model.Email
import java.io.InputStream
import java.io.InputStreamReader

fun EmailClient.sendHuntRequestForm(to: Email) {
    val content = loadResource("hunt-request-email.html")
        ?.let { InputStreamReader(it) }
        ?.let { it.readText() }
        ?: error("can't find email file")

    sendEmail(to = to, from = fromEmail, subject = "Hello, from FlatJäger", content = content)
}

private fun loadResource(filename: String): InputStream? =
    Thread.currentThread()
        .contextClassLoader
        .getResourceAsStream(filename)

private val fromEmail = Email("cam@camhashemi.com")
