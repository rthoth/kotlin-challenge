package challenge.repository

import challenge.BookingFixture
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class BookingRepositoryTest : RepositoryTest<BookingRepository>() {

    override fun createRepository(database: Database) = BookingRepository.create(database)

    @Test
    fun `should add a booking in database`() = testRepository { repository, database ->
        val expected = BookingFixture.createRandom()
        repository.add(expected)
        assertTrue {
            database.sequenceOf(BookingRepository.Companion.Bookings).filter { it.id eq expected.id }.toList()
                .isNotEmpty()
        }
    }

    @Test
    fun `should find a booking intersection`() = testRepository { repository, database ->
        val priorBooking = BookingFixture.createRandom()
        repository.add(priorBooking)

        val intersection = repository.searchByMobilePhone(
            priorBooking.mobilePhoneId,
            priorBooking.starting.minusHours(2).truncatedTo(ChronoUnit.HOURS),
            priorBooking.ending.plusHours(1).truncatedTo(ChronoUnit.HOURS),
            Duration.ofHours(2)
        )

        assertContentEquals(intersection, listOf(priorBooking))
    }

}