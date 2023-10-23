package challenge.repository

import challenge.MobilePhone
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import java.time.Instant
import java.util.*

interface MobilePhoneRepository {

    suspend fun add(mobilePhone: MobilePhone): MobilePhone

    suspend fun get(id: String): Optional<MobilePhone>

    suspend fun update(mobile: MobilePhone): MobilePhone

    companion object {

        interface StoredEntity : Entity<StoredEntity> {

            companion object : Entity.Factory<StoredEntity>()

            var id: String
            var model: String
            var bookedInstant: Instant?
            var personName: String?
        }

        object MobilePhones : Table<StoredEntity>("MOBILE_PHONE") {
            val id = varchar("ID").primaryKey().bindTo { it.id }
            val model = varchar("MODEL").bindTo { it.model }
            val bookedInstant = timestamp("BOOKED_INSTANT").bindTo { it.bookedInstant }
            val personName = varchar("PERSON_NAME").bindTo { it.personName }
        }

        fun create(database: Database): MobilePhoneRepository = object : MobilePhoneRepository {

            override suspend fun add(mobilePhone: MobilePhone): MobilePhone {
                database.sequenceOf(MobilePhones).add(convert(mobilePhone))
                return mobilePhone
            }

            override suspend fun get(id: String): Optional<MobilePhone> {
                return Optional.ofNullable(database.sequenceOf(MobilePhones).filter { it.id eq id }.map { convert(it) }
                    .firstOrNull())
            }

            override suspend fun update(mobile: MobilePhone): MobilePhone {
                TODO("Not yet implemented")
            }

            private fun convert(mobilePhone: MobilePhone) = StoredEntity {
                id = mobilePhone.id
                model = mobilePhone.model
                bookedInstant = mobilePhone.bookedInstant
                personName = mobilePhone.personName
            }

            private fun convert(stored: StoredEntity) = MobilePhone(
                id = stored.id,
                model = stored.model,
                bookedInstant = stored.bookedInstant,
                personName = stored.personName
            )
        }
    }
}