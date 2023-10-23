package challenge

object MobilePhoneAvailability {

    sealed interface Type {
        val mobilePhone: MobilePhone
    }

    data class Available(override val mobilePhone: MobilePhone) : Type
    data class Unavailable(override val mobilePhone: MobilePhone) : Type

    fun of(mobilePhone: MobilePhone): Type {
        return if (mobilePhone.bookedInstant == null || mobilePhone.personName == null) {
            Available(mobilePhone)
        } else {
            Unavailable(mobilePhone)
        }
    }
}