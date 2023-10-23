package challenge.http

import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.complete
import akka.http.javadsl.server.Directives.entity
import akka.http.javadsl.server.Route
import challenge.*
import challenge.fonoapi.FonoapiService
import java.time.Instant
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrElse

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

        sealed interface FonoapiDeviceInfo

        data class FilledFonoapiDeviceInfo(
            val technology: String,
            val _2g_bands: String,
            val _3g_bands: String,
            val _4g_bands: String
        ) : FonoapiDeviceInfo

        data object NoFonoapiDeviceInfo : FonoapiDeviceInfo

        sealed class Availability(
            val mobilePhoneId: String,
            val model: String,
            val available: Boolean,
            val fonoapi: FonoapiDeviceInfo
        )

        class MobilePhoneAvailable(mobilePhone: MobilePhone, deviceInfo: FonoapiDeviceInfo) :
            Availability(mobilePhone.id, mobilePhone.model, true, deviceInfo)

        class MobilePhoneUnavailable(mobilePhone: MobilePhone, deviceInfo: FonoapiDeviceInfo) :
            Availability(mobilePhone.id, mobilePhone.model, false, deviceInfo) {
            val bookedAt = mobilePhone.bookedInstant
            val bookedBy = mobilePhone.personName
        }

        fun create(managerModule: ManagerModule, executor: Executor): BookingHandler =

            object : HttpHandler(executor), BookingHandler {

                private val bookingManager: BookingManager = managerModule.bookingManager

                private val fonoapiService: FonoapiService = managerModule.fonoapiService

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
                            bookingManager.info()
                                .map {
                                    it to fonoapiService.get(it.mobilePhone).map { deviceInfo ->
                                        FilledFonoapiDeviceInfo(
                                            technology = deviceInfo.technology,
                                            _2g_bands = deviceInfo._2g_bands,
                                            _3g_bands = deviceInfo._3g_bands,
                                            _4g_bands = deviceInfo._4g_bands
                                        )
                                    }.getOrElse { NoFonoapiDeviceInfo }
                                }
                                .map { (availability, deviceInfo) ->
                                    when (availability) {
                                        is MobilePhoneAvailability.Available ->
                                            MobilePhoneAvailable(availability.mobilePhone, deviceInfo)

                                        is MobilePhoneAvailability.Unavailable ->
                                            MobilePhoneUnavailable(availability.mobilePhone, deviceInfo)
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