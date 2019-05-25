package flathunter

import java.time.Duration
import java.time.LocalDate

data class FlatRequest(
    val maxPrice: Int,
    val minRooms: Int,
    val availableAfter: LocalDate?,
    val availableBefore: LocalDate?,
    val transit: FlatTransitRequest?,
    val acceptUnknowns: Boolean = false
)

fun createRequest(getParameter: (String) -> String?) =
    FlatRequest(
        maxPrice = getParameter("maxPrice")!!.toInt(),
        minRooms = getParameter("minRooms")!!.toInt(),
        availableAfter = DateUtil.parseDate(getParameter("after") ?: ""),
        availableBefore = DateUtil.parseDate(getParameter("before") ?: ""),
        transit = createTransitRequest(getParameter),
        acceptUnknowns = getParameter("acceptUnknowns") == "true"
    ).also {
        it.logger?.debug("request=$it")
    }

private fun createTransitRequest(getParameter: (String) -> String?): FlatTransitRequest? {
    val destinations =
        getParameter("destinations")
            ?.split(";")
            ?: return null
    val maxTransitTime =
        getParameter("maxTransitTime")
            .takeUnless { it.isNullOrBlank() }
            ?.toLong()
            ?.let { Duration.ofMinutes(it) }
            ?: return null
    return FlatTransitRequest(destinations = destinations, maxTransitTime = maxTransitTime)
}