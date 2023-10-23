package challenge

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingManagerTest : ChallengeTest() {

    data class Context(
        val manager: BookingManager, val repository: MobilePhoneRepository, val instantFactory: InstantFactory
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

        assertEquals(BookingResult.Booked(expectedMobilePhone, expectedBooking), manager.booked(expectedBooking))
    }

    @Test
    fun `should reject a booking when the mobile phone is already booked`() {
        runBlocking {
            val (manager, repository, instantFactory) = newContext()
            val expectedBooking = BookingFixture.createRandom()
            val mobilePhone = MobilePhoneFixture.createRandom().copy(
                id = expectedBooking.mobilePhoneId,
                bookedInstant = createRandomInstant(),
                personName = "somebody-${randomId()}"
            )

            coEvery {
                repository.get(expectedBooking.mobilePhoneId)
            } returns Optional.of(mobilePhone)

            assertEquals(BookingResult.Unavailable(mobilePhone, expectedBooking), manager.booked(expectedBooking))
        }
    }

    @Test
    fun `should return a mobile phone`() {
        runBlocking {
            val (manager, repository, instantFactory) = newContext()
            val toBeReturned = MobilePhoneFixture.createRandom().copy(
                bookedInstant = createRandomInstant(), personName = "somebody-${randomId()}"
            )
            val expected = toBeReturned.copy(bookedInstant = null, personName = null)

            coEvery { repository.get(toBeReturned.id) } returns Optional.of(toBeReturned)
            coEvery { repository.update(expected) } returns expected

            assertEquals(expected, runBlocking { manager.returned(toBeReturned.id) }.get())
        }
    }

    @Test
    fun `should list availability of all mobile phones`() {
        val (manager, repository) = newContext()

        val availableMobilePhone = MobilePhoneFixture.createRandom().copy(
            bookedInstant = null, personName = null
        )
        val unavailableMobilePhone = MobilePhoneFixture.createRandom().copy(
            bookedInstant = createRandomInstant(), personName = "somebody-${randomId()}"
        )

        coEvery { repository.list() }.returns(listOf(availableMobilePhone, unavailableMobilePhone))

        val info = runBlocking {
            manager.info()
        }

        assertEquals(
            listOf(
                MobilePhoneAvailability.Available(availableMobilePhone),
                MobilePhoneAvailability.Unavailable(
                    unavailableMobilePhone,
                    unavailableMobilePhone.bookedInstant!!,
                    unavailableMobilePhone.personName!!
                )
            ), info
        )
    }
}