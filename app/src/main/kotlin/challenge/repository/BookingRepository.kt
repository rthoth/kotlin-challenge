package challenge.repository

import challenge.Booking
import java.time.Duration
import java.time.ZonedDateTime

interface BookingRepository {

    suspend fun add(booking: Booking): Booking

    suspend fun searchByMobilePhone(
        phoneId: String,
        starting: ZonedDateTime,
        ending: ZonedDateTime,
        timeGap: Duration
    ): List<Booking>
}