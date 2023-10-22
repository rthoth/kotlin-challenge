package challenge.repository

import challenge.BookingFixture
import challenge.MemberFixture
import challenge.MobilePhoneFixture
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class BookingRepositoryTest : RepositoryTest<BookingRepository>() {

    override fun createRepository(session: Session): BookingRepository = BookingRepository.create(session)

    @Test
    fun `should add a booking in database`() = testRepository { repository, session ->

        val mobilePhoneRepository = MobilePhoneRepository.create(session)
        val memberRepository = MemberRepository.create(session)

        val expectedMobilePhone = MobilePhoneFixture.createRandom()
        val expectedMember = MemberFixture.createRandom()

        mobilePhoneRepository.add(expectedMobilePhone)
        memberRepository.add(expectedMember)

        val expectedBooking = BookingFixture.createRandom()
            .copy(memberId = expectedMember.id, mobilePhoneId = expectedMobilePhone.id)

        repository.add(expectedBooking)
        session.attempt {
            val first =
                it.query("SELECT FROM ${Classes.Booking} WHERE id = ?", expectedBooking.id).edgeStream().findFirst()
            assertTrue { first.get().get<String>("id") == expectedBooking.id }
        }
    }

    @Test
    fun `should find a booking intersections`() = testRepository { repository, session ->
        val mobilePhoneRepository = MobilePhoneRepository.create(session)
        val memberRepository = MemberRepository.create(session)

        val expectedMobilePhone = MobilePhoneFixture.createRandom()
        val expectedMember = MemberFixture.createRandom()

        mobilePhoneRepository.add(expectedMobilePhone)
        memberRepository.add(expectedMember)

        val expectedBooking = BookingFixture.createRandom()
            .copy(memberId = expectedMember.id, mobilePhoneId = expectedMobilePhone.id)

        repository.add(expectedBooking)

        val found = repository.searchByMobilePhone(
            expectedBooking.mobilePhoneId,
            expectedBooking.starting.minusMinutes(10),
            expectedBooking.ending.plusMinutes(10),
            Duration.ofHours(2)
        )

        assertContains(found, expectedBooking)

    }
}