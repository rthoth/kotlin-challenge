package challenge

import java.time.Instant

data class MobilePhone(
    val id: String,
    val model: String,
    val bookedInstant: Instant?,
    val personName: String?
)