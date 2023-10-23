package challenge

import challenge.repository.MobilePhoneRepository
import kotlinx.coroutines.runBlocking
import kotlin.jvm.optionals.getOrElse

interface BookingManager {

    suspend fun book(booking: Booking): BookingResult.Type

    companion object {

        fun create(repository: MobilePhoneRepository, instantFactory: InstantFactory): BookingManager =
            object : BookingManager {

                override suspend fun book(booking: Booking): BookingResult.Type {
                    return repository.get(booking.mobilePhoneId).map { mobile ->
                        if (mobile.bookedInstant == null) {
                            BookingResult.Booked(
                                runBlocking {
                                    repository.update(
                                        mobile.copy(
                                            bookedInstant = instantFactory.now(), personName = booking.personName
                                        )
                                    )
                                }, booking
                            )
                        } else {
                            BookingResult.Unavailable(mobile, booking)
                        }
                    }.getOrElse { BookingResult.NotFound(booking) }
                }
            }
    }
}