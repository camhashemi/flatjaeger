package email

import model.Email

interface EmailClient {
    fun sendEmail(from: Email, to: Email, subject: String, content: String): Result
}

sealed class Result {
    object Success : Result()
    data class Failure(val reason: String) : Result()

    companion object
}
