package challenge

import challenge.repository.MobilePhoneRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingManagerTest : ChallengeTest() {

    data class Context(
        val manager: BookingManager,
        val repository: MobilePhoneRepository,
        val instantFactory: InstantFactory
    )

    private fun newContext(): Context {
        val repository = mockk<MobilePhoneRepository>()
        val instantFactory = mockk<InstantFactory>()
        return Context(BookingManager.create(repository, instantFactory), repository, instantFactory)
    }

    @Test
    fun `should accept a booking`() = runBlocking {
        val (manager, repository, instantFactory) = newContext()
        val expectedBooking = BookingFixture.createRandom()
        val mobilePhone = MobilePhoneFixture.createRandom()
            .copy(id = expectedBooking.mobilePhoneId, bookedInstant = null, personName = null)

        val now = createRandomInstant()
        val expectedMobilePhone = mobilePhone.copy(
            bookedInstant = now, personName = expectedBooking.personName
        )

        every { instantFactory.now() } returns now

        coEvery {
            repository.get(expectedBooking.mobilePhoneId)
        } returns Optional.of(mobilePhone)

        coEvery {
            repository.update(expectedMobilePhone)
        } returns expectedMobilePhone

        assertEquals(BookingResult.Booked(expectedMobilePhone, expectedBooking), manager.book(expectedBooking))
    }

    @Test
    fun `should reject a booking when the mobile phone is already booked`() {
        runBlocking {
            val (manager, repository, instantFactory) = newContext()
            val expectedBooking = BookingFixture.createRandom()
            val mobilePhone = MobilePhoneFixture
                .createRandom().copy(
                    id = expectedBooking.mobilePhoneId,
                    bookedInstant = createRandomInstant(),
                    personName = "somebody-${randomId()}"
                )

            coEvery {
                repository.get(expectedBooking.mobilePhoneId)
            } returns Optional.of(mobilePhone)

            assertEquals(BookingResult.Unavailable(mobilePhone, expectedBooking), manager.book(expectedBooking))
        }
    }
}