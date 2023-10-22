package challenge

import java.time.ZonedDateTime

data class Booking(
    val id: String,
    val mobilePhoneId: String,
    val memberId: String,
    val starting: ZonedDateTime,
    val ending: ZonedDateTime
)
