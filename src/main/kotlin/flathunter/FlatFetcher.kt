package flathunter

import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.httpGet
import model.Flat
import java.util.concurrent.ConcurrentHashMap

object FlatFetcher {

    private val FLAT_CACHE: SimpleFlatCache? = SimpleFlatCache()

    suspend fun getFlat(id: String): Flat =
        FLAT_CACHE
            ?.getCachedFlat(id)
            ?: getFlatWithoutCache(id)

    private suspend fun getFlatWithoutCache(id: String): Flat {
        logger?.debug("fetching flat $id")

        val url = getFlatUrl(id)
        val page = url.httpGet().awaitString()

        return FlatParser.parseFlat(page, id, url)
    }

    private suspend fun SimpleFlatCache.getCachedFlat(id: String): Flat =
        get(id) ?: getFlatWithoutCache(id).also { put(it) }

    private class SimpleFlatCache {
        private val store = ConcurrentHashMap<String, Flat>()

        fun get(id: String): Flat? =
            store[id]
                ?.also { logger?.debug("flat cache hit $id") }

        fun put(flat: Flat) {
            store[flat.id] = flat
        }
    }

    private fun getFlatUrl(id: String) = "${Constants.immobilienScoutUrl}/expose/$id"
}
