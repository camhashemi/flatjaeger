package flathunter

import kotlinx.coroutines.runBlocking
import model.Flat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FlatFetcherTest {
    @Test
    fun `string to euros`() {
        Assertions.assertEquals("1.014".toWholeEuros(), 1014)
        Assertions.assertEquals("1.014,00".toWholeEuros(), 1014)
        Assertions.assertEquals("1.014,00 €".toWholeEuros(), 1014)
        Assertions.assertEquals("1.014,30 €".toWholeEuros(), 1014)
    }

    @Test
    fun `fetches flat`() {
        val expectedFlat = Flat(
            id = "114811751",
            url = "https://www.immobilienscout24.de/expose/114811751",
            title = "Für Individualisten. Erstbezug.",
            coldRent = 1014,
            warmRent = 1189,
            available = DateUtil.today(),
            rooms = "2",
            area = "57,96 m²",
            address = "Löwenberger Straße 7, 10315 Berlin, Friedrichsfelde (Lichtenberg)",
            transitTime = null
        )
        val flat = runBlocking {
            FlatFetcher.getFlat("114811751")
        }

        Assertions.assertEquals(expectedFlat, flat)
    }
}