package model

import java.time.Duration
import java.time.LocalDate

data class Flat(
    val id: String,
    val url: String,
    val title: String,
    val coldRent: Int,
    val warmRent: Int,
    val available: LocalDate?,
    val rooms: String,
    val area: String,
    val address: String,
    val transitTime: Duration? = null
)