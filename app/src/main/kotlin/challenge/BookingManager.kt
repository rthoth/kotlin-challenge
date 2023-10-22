package challenge

import challenge.repository.BookingRepository
import java.time.Duration

interface BookingManager {

    suspend fun book(booking: Booking): Booking

    companion object {

        val DEFAULT_TIME_GAP = Duration.ofHours(2)

        fun create(bookingRepository: BookingRepository): BookingManager =
            object : BookingManager {

                override suspend fun book(booking: Booking): Booking {
                    val previous = bookingRepository.searchByMobilePhone(
                        booking.mobilePhoneId,
                        booking.starting,
                        booking.ending,
                        DEFAULT_TIME_GAP
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