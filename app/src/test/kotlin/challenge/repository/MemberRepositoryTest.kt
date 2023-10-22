package challenge.repository

import challenge.MemberFixture
import kotlin.test.Test
import kotlin.test.assertTrue

class MemberRepositoryTest : RepositoryTest<MemberRepository>() {

    override fun createRepository(session: Session): MemberRepository = MemberRepository.create(session)

    @Test
    fun `should add a member in database`() = testRepository { repository, session ->
        val expected = MemberFixture.createRandom()
        repository.add(expected)
        session.attempt {
            val first = it.query("SELECT FROM ${Classes.Member} WHERE id = ?", expected.id).elementStream()
                .findFirst()

            assertTrue { first.get().get<String>("id") == expected.id }
            assertTrue { first.get().get<String>("name") == expected.name }
        }

    }
}
