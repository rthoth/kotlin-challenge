package challenge.repository

import challenge.MemberFixture
import org.ktorm.database.Database
import org.ktorm.dsl.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MemberRepositoryTest : RepositoryTest<MemberRepository>() {

    override fun createRepository(database: Database) = MemberRepository.create(database)

    @Test
    fun `should add a member in database`() = testRepository { repository, database ->
        val expected = MemberFixture.createRandom()
        repository.add(expected)

        assertEquals(1, database.from(MemberRepository.Companion.Members)
            .select()
            .where { MemberRepository.Companion.Members.id eq expected.id }
            .asIterable()
            .count()
        )

    }
}
