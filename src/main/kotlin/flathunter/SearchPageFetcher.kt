package flathunter

import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.httpGet
import flathunter.Constants.immobilienScoutUrl
import org.jsoup.Jsoup
import java.util.concurrent.ConcurrentHashMap

data class SearchPage(val url: String, val flatIds: List<String>)

object SearchPageFetcher {

    private val cache: SearchPageCache? = SearchPageCache()

    suspend fun getSearchPage(request: FlatRequest, pageNumber: Int): SearchPage =
        getSearchURL(request.maxPrice, request.minRooms, pageNumber)
            .let {
                logger?.debug("fetching flathunter page number $pageNumber")
                cache
                    ?.getCachedSearchPage(it)
                    ?: getSearchPage(it)
            }

    private suspend fun getSearchPage(url: String): SearchPage {
        val page = url.httpGet().awaitString()
        val flatIds =
            Jsoup.parse(page)
                .select(".result-list__listing")
                .eachAttr("data-id")
        return SearchPage(url, flatIds)
    }

    private suspend fun SearchPageCache.getCachedSearchPage(url: String): SearchPage =
        get(url) ?: getSearchPage(url).also { put(it) }

    private class SearchPageCache {

        private val store = ConcurrentHashMap<String, SearchPage>()

        fun get(url: String): SearchPage? =
            store[url]
                ?.also { logger?.debug("flathunter page cache hit $url") }

        fun put(page: SearchPage) {
            store[page.url] = page
        }
    }
}

private fun getSearchURL(maxPrice: Int, minRooms: Int, pageNumber: Int) =
    "$immobilienScoutUrl/Suche/de/berlin/berlin/wohnung-mieten?numberofrooms=$minRooms-&price=-$maxPrice&pagenumber=$pageNumber"

