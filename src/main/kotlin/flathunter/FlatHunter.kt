package flathunter

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.toList
import model.Flat

data class PaginatedFlatRequest(val page: Int, val pageSize: Int, val request: FlatRequest)

@FlowPreview
object FlatHunter {

    /**
     * [paginatedSearch] fetches a page of flats based on the given [paginatedRequest].
     */
    suspend fun paginatedSearch(paginatedRequest: PaginatedFlatRequest): List<Flat> {
        val (page, pageSize, request) = paginatedRequest
        return getFlatsFlow(request)
            .drop(page * pageSize)
            .take(pageSize)
            .toList()
    }

    suspend fun batchSearch(requests: List<FlatRequest>): Map<FlatRequest, List<Flat>> =
        requests
            .map { request ->
                GlobalScope.async {
                    request to fullSearch(request)
                }.also { Thread.sleep(10000) }
            }
            .awaitAll()
            .toMap()

    /**
     * [fullSearch] fetches all flats matching the given [request].
     */
    suspend fun fullSearch(request: FlatRequest): List<Flat> = getFlatsFlow(request).toList()

    private fun getFlatsFlow(request: FlatRequest): Flow<Flat> =
        generateSequence(1) { it + 1 }
            .asFlow()
            .map { SearchPageFetcher.getSearchPage(request, it).flatIds }
            .takeWhile { it.isNotEmpty() }
            .flatMapConcat {
                it.map { id ->
                    GlobalScope.async {
                        getMatchingFlat(id, request)
                    }
                }.awaitAll().asFlow()
            }
            .filterNotNull()

    private suspend fun getMatchingFlat(id: String, request: FlatRequest): Flat? =
        FlatFetcher.getFlat(id)
            .takeIf { it.matchesDates(request) && it.matchesWarmRent(request.maxPrice) }
            ?.let { // only fetch transit if the flat matches dates and transit is requested
                logger?.debug("flat $id matched dates")
                if (request.transit == null) it
                else it.withTransit(request.transit)
            }
            ?.takeIf { it.matchesTransit(request) }
            ?.also { logger?.debug("flat $id matched transit") }

    private fun Flat.matchesWarmRent(maxPrice: Int) = warmRent <= maxPrice

    private fun Flat.matchesDates(request: FlatRequest): Boolean =
        when {
            available == null -> request.acceptUnknowns
            request.availableAfter == null && request.availableBefore == null -> true
            request.availableAfter == null -> available <= request.availableBefore
            request.availableBefore == null -> available >= request.availableAfter
            else -> available <= request.availableBefore && available >= request.availableAfter
        }

    private fun Flat.withTransit(request: FlatTransitRequest) =
        this.copy(transitTime = TransitFetcher.getTransitTime(address, request.destinations))
}
