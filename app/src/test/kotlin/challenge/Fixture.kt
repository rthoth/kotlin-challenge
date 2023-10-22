package challenge

import java.time.Clock
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

fun randomId() = UUID.randomUUID().toString()

fun randomZonedDateTime(): ZonedDateTime =
    ZonedDateTime.now(Clock.systemUTC()).plusHours(ThreadLocalRandom.current().nextLong(-3, 3))
        .truncatedTo(ChronoUnit.HOURS)

object MobilePhoneFixture {

    fun createRandom(): MobilePhone = MobilePhone(
        id = randomId(),
        model = "Model-${randomId()}"
    )
}

object MemberFixture {

    fun createRandom(): Member = Member(
        id = randomId(),
        name = "Member-${randomId()}"
    )
}

object BookingFixture {

    fun createRandom(): Booking {
        val starting = randomZonedDateTime()
        return Booking(
            id = randomId(),
            mobilePhoneId = randomId(),
            memberId = randomId(),
            starting = starting,
            ending = starting.plusHours(24)
        )
    }
}