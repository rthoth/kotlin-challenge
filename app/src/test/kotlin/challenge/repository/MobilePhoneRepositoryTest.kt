package challenge.repository

import challenge.MobilePhoneFixture
import org.ktorm.database.Database
import org.ktorm.dsl.asIterable
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import kotlin.test.Test
import kotlin.test.assertEquals

class MobilePhoneRepositoryTest : RepositoryTest<MobilePhoneRepository>() {

    override fun createRepository(database: Database) = MobilePhoneRepository.create(database)

    @Test
    fun `should add a model in database`() = testRepository { repository, database ->
        val expected = MobilePhoneFixture.createRandom()
        repository.add(expected)

        assertEquals(
            1, database
                .from(MobilePhoneRepository.Companion.MobilePhones)
                .select()
                .asIterable()
                .count()
        )
    }
}