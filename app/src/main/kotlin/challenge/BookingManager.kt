package challenge

import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.jvm.optionals.getOrElse

interface BookingManager {

    suspend fun booked(booking: Booking): BookingResult.Type

    suspend fun returned(mobilePhoneId: String): Optional<MobilePhone>

    suspend fun info(): List<MobilePhoneAvailability.Type>

    companion object {

        fun create(repository: MobilePhoneRepository, instantFactory: InstantFactory): BookingManager =
            object : BookingManager {

                override suspend fun booked(booking: Booking): BookingResult.Type {
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

                override suspend fun info(): List<MobilePhoneAvailability.Type> {
                    return repository.list().map { MobilePhoneAvailability.of(it) }
                }

                override suspend fun returned(mobilePhoneId: String): Optional<MobilePhone> {
                    return repository.get(mobilePhoneId).map { mobile ->
                        if (mobile.bookedInstant == null) {
                            mobile
                        } else {
                            runBlocking {
                                repository.update(
                                    mobile.copy(
                                        bookedInstant = null, personName = null
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }
}