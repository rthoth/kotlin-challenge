package challenge

import challenge.repository.MobilePhoneRepository
import kotlinx.coroutines.runBlocking
import kotlin.jvm.optionals.getOrElse

interface BookingManager {

    suspend fun book(booking: Booking): BookingResult.Type

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
                    return repository.get(mobilePhoneId).map { mobilePhone ->
                        if (mobilePhone.bookedInstant == null && mobilePhone.personName == null) {
                            MobilePhoneAvailability.Available(mobilePhone)
                        } else {
                            MobilePhoneAvailability.Unavailable(
                                mobilePhone,
                                mobilePhone.bookedInstant!!,
                                mobilePhone.personName!!
                            )
                        }
                    }.getOrElse { MobilePhoneAvailability.NotFound(mobilePhoneId) }
                }

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