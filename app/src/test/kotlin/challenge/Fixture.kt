package challenge

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun randomId() =
    UUID.randomUUID().toString()

fun createRandomInstant(): Instant =
    Instant.now().plusSeconds(ThreadLocalRandom.current().nextLong(-3600, 3600)).truncatedTo(ChronoUnit.MINUTES)

object MobilePhoneFixture {

    fun createRandom(): MobilePhone = MobilePhone(
        id = randomId(),
        model = "model-${randomId()}",
        bookedInstant = null,
        personName = null
    )
}

object BookingFixture {

    fun createRandom(): Booking {
        return Booking(
            mobilePhoneId = randomId(),
            personName = "person-${randomId()}"
        )
    }
}