package challenge.repository

import challenge.MobilePhoneFixture
import kotlin.test.Test
import kotlin.test.assertTrue

class MobilePhoneRepositoryTest : RepositoryTest<MobilePhoneRepository>() {

    override fun createRepository(session: Session): MobilePhoneRepository = MobilePhoneRepository.create(session)

    @Test
    fun `should add a model in database`() = testRepository { repository, session ->
        val expected = MobilePhoneFixture.createRandom()
        repository.add(expected)

        session.attempt { database ->
            val first = database
                .query("SELECT FROM ${Classes.MobilePhone} WHERE id = ?", expected.id)
                .elementStream()
                .findFirst()

            assertTrue { first.get().getProperty<String>("id") == expected.id }
            assertTrue { first.get().getProperty<String>("model") == expected.model }
        }
    }
}