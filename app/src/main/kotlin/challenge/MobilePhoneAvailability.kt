package challenge

object MobilePhoneAvailability {

    sealed interface Type

    data class Available(val mobilePhone: MobilePhone) : Type
    data class Unavailable(val mobilePhone: MobilePhone) : Type

    fun of(mobilePhone: MobilePhone): Type {
        return if (mobilePhone.bookedInstant == null || mobilePhone.personName == null) {
            Available(mobilePhone)
        } else {
            Unavailable(mobilePhone)
        }
    }
}