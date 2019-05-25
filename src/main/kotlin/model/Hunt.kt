package model

import java.time.LocalDateTime

data class Hunt(
    val id: Id<Hunt>,
    val email: Email,
    val createdAt: LocalDateTime,
    val flats: List<Flat>
)