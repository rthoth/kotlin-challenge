package challenge

object BookingResult {

    sealed interface Type

    data class NotFound(val booking: Booking) : Type
    data class Booked(val mobilePhone: MobilePhone, val booking: Booking) : Type
    data class Unavailable(val mobilePhone: MobilePhone, val booking: Booking) : Type
}