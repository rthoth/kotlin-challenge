package challenge

import org.ktorm.database.Database
import org.ktorm.dsl.insert
import kotlin.test.Test
import kotlin.test.assertEquals

class MobilePhoneRepositoryTest : RepositoryTest<MobilePhoneRepository>() {

    override fun createRepository(database: Database) = MobilePhoneRepository.create(database)

    private fun Database.insert(mobilePhone: MobilePhone) {
        this.insert(MobilePhoneRepository.Companion.MobilePhones) {
            set(it.id, mobilePhone.id)
            set(it.model, mobilePhone.model)
            set(it.bookedInstant, mobilePhone.bookedInstant)
            set(it.personName, mobilePhone.personName)
        }
    }

    @Test
    fun `should get a mobile phone from database`() = withRepository { repository, database ->
        val expected = MobilePhoneFixture.createRandom()
        database.insert(expected)
        assertEquals(expected, repository.get(expected.id).get())
    }

    @Test
    fun `should update a mobile phone in database(set bookedInstant and personName not null)`() {
        withRepository { repository, database ->
            val toBeUpdated = MobilePhoneFixture.createRandom()
            database.insert(toBeUpdated)

            val expected = toBeUpdated.copy(
                bookedInstant = createRandomInstant(), personName = "name-${randomId()}"
            )

            assertEquals(expected, repository.update(expected))
        }
    }

    @Test
    fun `return all mobile phones`() {
        withRepository { repository, _ ->
            assertEquals(
                setOf("s9", "s8", "moto6", "plus9", "iphone13", "iphone12", "iphone11", "iphoneX", "n3310"),
                repository.list().map { it.id }.toSet()
            )
        }
    }
}