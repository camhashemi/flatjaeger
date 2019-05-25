package flathunter

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FlatHunterTest {

    @Test
    @FlowPreview
    fun `paginated search can fetch single item`() {
        val request = PaginatedFlatRequest(
            page = 0,
            pageSize = 1,
            request = FlatRequest(
                maxPrice = 1500,
                minRooms = 2,
                availableAfter = DateUtil.today(),
                availableBefore = DateUtil.nextMonth(),
                transit = null,
                acceptUnknowns = false
            )
        )
        val flats = runBlocking {
            FlatHunter.paginatedSearch(request)
        }.also {
            logger?.debug(it.toString())
        }

        Assertions.assertEquals(flats.size, 1)
    }
}