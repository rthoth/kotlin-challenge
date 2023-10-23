package challenge.repository

import challenge.MobilePhoneFixture
import org.ktorm.database.Database
import org.ktorm.dsl.asIterable
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import kotlin.test.Test
import kotlin.test.assertEquals

class MobilePhoneRepositoryTest : RepositoryTest<MobilePhoneRepository>() {

    override fun createRepository(database: Database) = MobilePhoneRepository.create(database)

    @Test
    fun `should add a mobile phone in database`() = withRepository { repository, database ->
        val expected = MobilePhoneFixture.createRandom()
        repository.add(expected)

        assertEquals(
            1, database.from(MobilePhoneRepository.Companion.MobilePhones).select().asIterable().count()
        )
    }

    @Test
    fun `should get a mobile phone from database`() = withRepository { repository, database ->
        val expected = MobilePhoneFixture.createRandom()
        database.insert(MobilePhoneRepository.Companion.MobilePhones) {
            set(it.id, expected.id)
            set(it.model, expected.model)
            set(it.bookedInstant, expected.bookedInstant)
            set(it.personName, expected.personName)
        }

        assertEquals(expected, repository.get(expected.id).get())
    }
}