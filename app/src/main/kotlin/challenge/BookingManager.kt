package challenge

import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.jvm.optionals.getOrElse

interface BookingManager {

    suspend fun booked(booking: Booking): BookingResult.Type

    suspend fun returned(mobilePhoneId: String): Optional<MobilePhone>

    suspend fun available(mobilePhoneId: String): MobilePhoneAvailability.Type

    suspend fun add(mobilePhone: MobilePhone): MobilePhone

    companion object {

        fun create(repository: MobilePhoneRepository, instantFactory: InstantFactory): BookingManager =
            object : BookingManager {

                override suspend fun add(mobilePhone: MobilePhone): MobilePhone {
                    return repository.add(
                        mobilePhone.copy(
                            bookedInstant = null, personName = null
                        )
                    )
                }

                override suspend fun available(mobilePhoneId: String): MobilePhoneAvailability.Type {
                    return repository.get(mobilePhoneId).map { MobilePhoneAvailability.of(it) }
                        .getOrElse { MobilePhoneAvailability.NotFound(mobilePhoneId) }
                }

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