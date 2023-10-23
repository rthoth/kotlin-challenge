package challenge.http

import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.complete
import akka.http.javadsl.server.Directives.entity
import akka.http.javadsl.server.Route
import challenge.*
import java.time.Instant
import java.util.concurrent.Executor

interface BookingHandler {

    fun booked(mobilePhoneId: String): Route

    fun info(): Route

    fun returned(mobilePhoneId: String): Route

    companion object {

        class BookMobilePhone {
            var who: String = ""
        }

        data class Forbidden(val mobilePhoneId: String)

        data class Booked(
            val mobilePhoneId: String,
            val bookedAt: Instant,
            val bookedBy: String
        )

        data class NotFound(val mobilePhoneId: String)

        data class Returned(val mobilePhoneId: String)

        sealed class Availability(
            val mobilePhoneId: String,
            val model: String,
            val available: Boolean
        )

        class MobilePhoneAvailable(mobilePhone: MobilePhone) : Availability(mobilePhone.id, mobilePhone.model, true)

        class MobilePhoneUnavailable(mobilePhone: MobilePhone) :
            Availability(mobilePhone.id, mobilePhone.model, false) {
            val bookedAt = mobilePhone.bookedInstant
            val bookedBy = mobilePhone.personName
        }

        fun create(managerModule: ManagerModule, executor: Executor): BookingHandler =

            object : HttpHandler(executor), BookingHandler {

                private val bookingManager: BookingManager = managerModule.bookingManager

                override fun booked(mobilePhoneId: String): Route {
                    return entity(Jackson.unmarshaller(BookMobilePhone::class.java)) { addBooking ->
                        completeWithSuspend(
                            block = {
                                bookingManager.booked(
                                    Booking(
                                        mobilePhoneId,
                                        addBooking.who
                                    )
                                )
                            },
                            onSuccess = {
                                completeAsJson(
                                    when (it) {
                                        is BookingResult.Booked -> StatusCodes.OK to Booked(
                                            it.mobilePhone.id,
                                            it.mobilePhone.bookedInstant!!,
                                            it.mobilePhone.personName!!
                                        )

                                        is BookingResult.Unavailable -> StatusCodes.FORBIDDEN to Forbidden(it.mobilePhone.id)
                                        is BookingResult.NotFound -> StatusCodes.NOT_FOUND to NotFound(mobilePhoneId)
                                    }
                                )
                            }
                        )
                    }
                }

                override fun info(): Route {
                    return completeWithSuspend(
                        block = {
                            bookingManager.info().map {
                                when (it) {
                                    is MobilePhoneAvailability.Available -> MobilePhoneAvailable(it.mobilePhone)
                                    is MobilePhoneAvailability.Unavailable -> MobilePhoneUnavailable(it.mobilePhone)
                                }
                            }
                        },
                        onSuccess = {
                            complete(StatusCodes.OK, it, jacksonMarshaller())
                        }
                    )
                }

                override fun returned(mobilePhoneId: String): Route {
                    return completeWithSuspend(
                        block = {
                            bookingManager.returned(mobilePhoneId)
                        },
                        onSuccess = {
                            completeAsJson(
                                when {
                                    it.isPresent -> StatusCodes.OK to Returned(mobilePhoneId)
                                    else -> StatusCodes.NOT_FOUND to NotFound(mobilePhoneId)
                                }
                            )
                        }
                    )
                }
            }
    }
}