package flathunter

import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DistanceMatrix
import com.google.maps.model.DistanceMatrixRow
import com.google.maps.model.TravelMode
import flathunter.Constants.transitApiKey
import model.Flat
import java.time.Duration
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

data class FlatTransitRequest(
    val apiKey: String? = null,
    val destinations: List<String>,
    val maxTransitTime: Duration? = null
)

object TransitFetcher {

    private val cache: TransitCache? = TransitCache()

    private val context by lazy { GeoApiContext.Builder().apiKey(transitApiKey).build() }

    fun getTransitTime(origin: String, destinations: List<String>): Duration {
        val request = TransitRequest(origin, destinations)
        return cache?.get(request) ?: fetchTransitTime(request)
    }

    private fun fetchTransitTime(request: TransitRequest): Duration {
        val (origin, destinations) = request
        logger?.debug("fetching transit for $origin")
        return DistanceMatrixApi.newRequest(context)
            .origins(origin)
            .destinations(*destinations.toTypedArray())
            .mode(TravelMode.TRANSIT)
            .departureTime(DateUtil.thisMorning().toInstant(ZoneOffset.UTC))
            .await()
            .transitTime
            .also { logger?.debug("${it.toMinutes()} minutes to $origin") }
            .also { cache?.put(request, it) }
    }

    private val DistanceMatrix.transitTime get(): Duration = rows.first().transitDuration

    private data class TransitRequest(val origin: String, val destinations: List<String>)

    private class TransitCache {
        private val store = ConcurrentHashMap<TransitRequest, Duration>()

        fun get(request: TransitRequest): Duration? =
            store[request]
                ?.also { logger?.debug("transit cache hit $request") }

        fun put(request: TransitRequest, duration: Duration) {
            store[request] = duration
        }
    }
}

fun Flat.matchesTransit(request: FlatRequest): Boolean =
    when {
        request.transit?.maxTransitTime == null -> true
        transitTime == null -> request.acceptUnknowns
        else -> transitTime <= request.transit.maxTransitTime
    }

val DistanceMatrixRow.transitDuration: Duration
    get() = Duration.ofSeconds(
        elements.map { it.duration.inSeconds }.sum()
    )
