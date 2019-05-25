package flathunter

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtil {

    val formats = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ofPattern("dd.MM.yy"),
        DateTimeFormatter.ofPattern("MM.yyyy"),
        DateTimeFormatter.ofPattern("MM.yy"),
        DateTimeFormatter.ofPattern("MM/yyyy")
    )

    fun parseDate(s: String): LocalDate? =
        when {
            s.isBlank() -> null
            s.toLowerCase().contains("sofort") -> today()
            s in specialDates -> parseDate(specialDates.getValue(s))
            s.startsWith("ab ") -> parseDate(s.removePrefix("ab "))
            else -> {
                formats.mapNotNull {
                    try {
                        LocalDate.from(it.parse(s))
                    } catch (e: ParseException) {
                        null
                    } catch (e: NumberFormatException) {
                        null
                    }
                }.firstOrNull()
            }
        }

    fun today(): LocalDate = LocalDate.now()

    fun nextMonth(): LocalDate =
        today().apply {
            withDayOfMonth(dayOfMonth + 1)
        }

    fun thisMorning(): LocalDateTime =
        today().run {
            atTime(8, 0)
        }

    private val specialDates = mapOf(
        "April 2020" to "01.04.2020",
        "Februar 2020" to "01.02.2020",
        "1. Mai 2020" to "01.05.2020",
        "Mai 2020" to "01.05.2020"
    )
}
