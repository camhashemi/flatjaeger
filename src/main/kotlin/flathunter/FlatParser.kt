package flathunter

import model.Flat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object FlatParser {
    fun parseFlat(page: String, id: String, url: String): Flat {
        val html = Jsoup.parse(page)
        val title = html.selectText("#expose-title") ?: error("unable to parse title for flat $id")
        val rooms = html.selectText(".mainCriteria .is24qa-zi") ?: error("unable to parse rooms for flat $id")

        val availableText = html.selectText(".is24qa-bezugsfrei-ab")
        val available =
            availableText
                ?.let { DateUtil.parseDate(it) }
                ?: DateUtil.today().also { logger?.warn("can't parse available for flat $id") }

        val coldRentText = html.selectText(".is24qa-kaltmiete") ?: error("unable to parse cold rent for flat $id")
        val coldRent = coldRentText.toWholeEuros()
        val warmRentText = html.selectText(".is24qa-gesamtmiete") ?: error("unable to parse warm rent for flat $id")
        val warmRent = warmRentText.toWholeEuros()

        val area = html.selectText(".mainCriteria .is24qa-flaeche") ?: error("unable to parse area for flat $id")
        val address = html.selectText(".address-block") ?: error("unable to parse address for flat $id")

        return Flat(
            id,
            url,
            title,
            coldRent,
            warmRent,
            available,
            rooms,
            area,
            address
        )
    }
}

private fun Document.selectText(query: String): String? = select(query).first()?.text()?.trim()

fun String.toWholeEuros(): Int = this.takeWhile { it != ',' }.filter { it.isDigit() }.toInt()
