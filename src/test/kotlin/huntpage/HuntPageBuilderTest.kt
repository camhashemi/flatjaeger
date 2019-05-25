package huntpage

import flathunter.DateUtil
import model.Flat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class HuntPageBuilderTest {

    @Test
    fun `convert flat to html`() {
        val flats = listOf(
            Flat(
                id = "114811751",
                url = "https://www.immobilienscout24.de/expose/114811751",
                title = "Für Individualisten . Erstbezug .",
                coldRent = 1000,
                warmRent = 1200,
                available = DateUtil.today(),
                rooms = "2",
                area = "57, 96 m²",
                address = "Löwenberger Straße 7, 10315 Berlin, Friedrichsfelde (Lichtenberg)",
                transitTime = null
            ),
            Flat(
                id = "114811712",
                url = "https://www.immobilienscout24.de/expose/114811712",
                title = "Für Communisten. Zweite mal benuzt .",
                coldRent = 1000,
                warmRent = 1200,
                available = DateUtil.today(),
                rooms = "6",
                area = "57, 96 m²",
                address = "Zehdenicker Straße 12B, 10119 Berlin, Mitte",
                transitTime = null
            )
        )

        val email = buildHuntPage(flats)

        File("test.html").writeText(email)

        flats.forEach {
            Assertions.assertTrue(email.contains(it.title))
            Assertions.assertTrue(email.contains(it.url))
            Assertions.assertTrue(email.contains(it.coldRent.toEuroString()))
            Assertions.assertTrue(email.contains(it.warmRent.toEuroString()))
            Assertions.assertTrue(email.contains(it.available.toString()))
            Assertions.assertTrue(email.contains(it.rooms))
            Assertions.assertTrue(email.contains(it.area))
            Assertions.assertTrue(email.contains(it.address))
        }
    }
}