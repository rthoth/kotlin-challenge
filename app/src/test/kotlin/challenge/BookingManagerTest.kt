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
    fun `should add a mobile phone in repository`() {
        val (manager, repository, _) = newContext()
        val now = createRandomInstant()
        val toBeAdded = MobilePhoneFixture.createRandom().copy(bookedInstant = now, personName = "name-${randomId()}")
        val expected = toBeAdded.copy(bookedInstant = null, personName = null)

        coEvery { repository.add(expected) } returns expected

        assertEquals(expected, runBlocking { manager.add(toBeAdded) })
    }

    @Test
    fun `should check availability (available)`() {
        val (manager, repository, _) = newContext()
        val expected = MobilePhoneFixture.createRandom()
            .copy(bookedInstant = null, personName = null)

        coEvery {
            repository.get(expected.id)
        } returns Optional.of(expected)

        assertEquals(MobilePhoneAvailability.Available(expected), runBlocking { manager.available(expected.id) })
    }

    @Test
    fun `should check availability (unavailable)`() {
        val (manager, repository, _) = newContext()
        val expected = MobilePhoneFixture.createRandom()
            .copy(bookedInstant = createRandomInstant(), personName = "person-${randomId()}")

        coEvery {
            repository.get(expected.id)
        } returns Optional.of(expected)

        assertEquals(
            MobilePhoneAvailability.Unavailable(expected, expected.bookedInstant!!, expected.personName!!),
            runBlocking { manager.available(expected.id) })
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
            val mobilePhone = MobilePhoneFixture
                .createRandom().copy(
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
            val toBeReturned = MobilePhoneFixture
                .createRandom().copy(
                    bookedInstant = createRandomInstant(),
                    personName = "somebody-${randomId()}"
                )
            val expected = toBeReturned.copy(bookedInstant = null, personName = null)

            coEvery { repository.get(toBeReturned.id) } returns Optional.of(toBeReturned)
            coEvery { repository.update(expected) } returns expected

            assertEquals(expected, runBlocking { manager.returned(toBeReturned.id) }.get())
        }
    }
}