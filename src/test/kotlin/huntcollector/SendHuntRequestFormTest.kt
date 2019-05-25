package huntcollector

import email.SendGridEmailClient
import model.Email
import org.junit.jupiter.api.Test

class SendHuntRequestFormTest {

    private val sendGridApiKey = System.getenv("sendGridApiKey")

    @Test
    fun sendGrid() {
        val emailClient = SendGridEmailClient(sendGridApiKey)

        emailClient.sendHuntRequestForm(to = Email("camhashemi@gmail.com"))

        // email has some "message clipped" warning that's annoying. other than that, looks good.
    }
}