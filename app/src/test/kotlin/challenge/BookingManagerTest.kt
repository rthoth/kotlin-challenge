package challenge

import challenge.repository.BookingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BookingManagerTest : ChallengeTest() {

    data class Context(val manager: BookingManager, val repository: BookingRepository, val timeGap: Duration)

    private fun context(): Context {
        val mock = mockk<BookingRepository>()
        val timeGap = Duration.ofHours(1)
        return Context(BookingManager.create(mock, Duration.ofHours(1)), mock, timeGap)
    }

    @Test
    fun `should accept a booking`() = runBlocking {
        val (manager, repository, timeGap) = context()
        val expected = BookingFixture.createRandom()

        coEvery {
            repository.searchByMobilePhone(
                expected.mobilePhoneId,
                expected.starting,
                expected.ending,
                timeGap
            )
        } returns (emptyList())

        coEvery {
            repository.add(expected)
        } returns expected

        assertTrue { manager.book(expected) == expected }
    }

    @Test
    fun `should reject a booking`() {
        runBlocking {
            val (manager, repository, timeGap) = context()
            val expected = BookingFixture.createRandom()

            coEvery {
                repository.searchByMobilePhone(
                    expected.mobilePhoneId,
                    expected.starting,
                    expected.ending,
                    timeGap
                )
            } returns (listOf(BookingFixture.createRandom()))

            assertFailsWith<IllegalStateException> { manager.book(expected) }
        }
    }
}