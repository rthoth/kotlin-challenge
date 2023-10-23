package challenge

import java.time.Instant

object MobilePhoneAvailability {

    sealed interface Type

    data class Available(val mobilePhone: MobilePhone) : Type
    data class Unavailable(val mobilePhone: MobilePhone, val bookedInstante: Instant, val personName: String) : Type
    data class NotFound(val mobilePhoneId: String) : Type
}