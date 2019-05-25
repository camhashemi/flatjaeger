package flathunter.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import kotlinx.coroutines.FlowPreview
import org.slf4j.LoggerFactory
import flathunter.script.runScript
import flathunter.createRequest

class EventHandler : RequestHandler<ScheduledEvent, String> {
    @FlowPreview
    override fun handleRequest(event: ScheduledEvent, context: Context): String {
        val request = createRequest { key -> System.getenv(key) }
        val result = runScript(request)
        return result.toString()
    }
}
