package challenge

import challenge.repository.BookingRepository
import java.time.Duration

interface BookingManager {

    suspend fun book(booking: Booking): Booking

    companion object {

        private val DEFAULT_TIME_GAP: Duration = Duration.ofHours(2)

        fun create(bookingRepository: BookingRepository, timeGap: Duration = DEFAULT_TIME_GAP): BookingManager =
            object : BookingManager {

                override suspend fun book(booking: Booking): Booking {
                    val previous = bookingRepository.searchByMobilePhone(
                        booking.mobilePhoneId,
                        booking.starting,
                        booking.ending,
                        timeGap
                    )

                    if (previous.isEmpty()) {
                        return bookingRepository.add(booking)
                    } else {
                        throw IllegalStateException("It is impossible to make the booking for mobile=${booking.mobilePhoneId}!")
                    }
                }
            }
    }
}