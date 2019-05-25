package email

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import model.Email
import com.sendgrid.helpers.mail.objects.Email as SendGridEmail

class SendGridEmailClient(apiKey: String) : EmailClient {

    private val sendGrid = SendGrid(apiKey)

    override fun sendEmail(from: Email, to: Email, subject: String, content: String): Result =
        createRequest(from, subject, to, content)
            .let { request -> sendGrid.api(request) }
            .let { response -> Result.from(response) }

    private fun createRequest(from: Email, subject: String, to: Email, content: String) =
        Request().apply {
            method = Method.POST
            endpoint = sendGridEndpoint
            body = Mail(
                from.toSendGridEmail(),
                subject,
                to.toSendGridEmail(),
                Content(contentType, content)
            ).build()
        }

    private fun Email.toSendGridEmail() = SendGridEmail(address)

    private fun Result.Companion.from(response: Response) =
        if (response.statusCode == 202) Result.Success
        else Result.Failure("status=${response.statusCode}; body=${response.body}")

    companion object {
        const val sendGridEndpoint = "mail/send"
        const val contentType = "text/html"
    }
}
